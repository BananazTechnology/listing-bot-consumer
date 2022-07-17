package tech.bananaz.bot.services;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.Getter;
import tech.bananaz.bot.models.Contract;
import tech.bananaz.bot.models.ListingEvent;
import tech.bananaz.bot.repositories.ListingEventRepository;
import tech.bananaz.bot.utils.EventType;

import static java.util.Objects.nonNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class ListingsScheduler extends TimerTask {
	
	private Contract contract;
	private boolean active						   = false;
	@Getter
	private Instant lastChecked     			   = offset(Instant.now());
	private final static int DEFAULT_OFFSET_MINUTE = 5;
	private Timer timer 		 				   = new Timer(); // creating timer
    private TimerTask task; // creating timer task
	private static final Logger LOGGER 			   = LoggerFactory.getLogger(ListingsScheduler.class);

	public ListingsScheduler(Contract contract) {
		this.contract = contract;
	}
	
	@Override
	public void run() {
		if(nonNull(this.contract) && this.active && this.contract.isActive()) {
			try {
				watchListings();
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(String.format("Failed during get listing: %s, stack: %s", this.contract.getContractAddress(), Arrays.toString(e.getStackTrace()))); 
			}
		}
	}

	public boolean start() {
		// Creates a new integer between 1-5 and * by 1000 turns it into a second in milliseconds
		// first random number
		int startsIn = (ThreadLocalRandom.current().nextInt(1, 10)*1000);
		if(nonNull(this.contract)) {
			this.active = true;
			this.task   = this;
			LOGGER.info(String.format("Starting new ListingsScheduler in %sms for: %s", startsIn, this.contract.toString()));
			// Starts this new timer, starts at random time and runs per <interval> milliseconds
			this.timer.schedule(task, startsIn , this.contract.getInterval());
		}
		return this.active;
	}
	
	public boolean stop() {
		this.active = false;
		LOGGER.info("Stopping ListingScheduler on " + this.contract.toString());
		return this.active;
	}
	
	private void watchListings() throws Exception {
		Instant newTime = Instant.now();
		ListingEventRepository repo = this.contract.getEvents();
		// Get any new items
		List<ListingEvent> queryEvents = 
			repo.findByConfigIdAndConsumedFalseAndCreatedDateGreaterThanAndEventTypeOrderByCreatedDateAsc(this.contract.getId(), this.lastChecked, EventType.LISTING);
		// Process if events exist
		if(queryEvents.size() > 0) {
			// Loop through available items
			for(ListingEvent e : queryEvents) {
				// Ensure a single transaction of GET and SET which should ensure no overwrite
				int updateCount = repo.updateByIdSetConsumedTrueAndConsumedBy(e.getId(), this.contract.getUuid());
				// Ensure the item was updated
				if(nonNull(updateCount) && updateCount > 0) {
					ListingEvent refreshedEvent = repo.findById(e.getId());
					// Ensure the item is consumed and the owner is this contract instance
					if(refreshedEvent.isConsumed() && refreshedEvent.getConsumedBy().equalsIgnoreCase(this.contract.getUuid())) {
						// Log
						logInfoNewEvent(e);
						// Discord
						if(!this.contract.isExcludeDiscord()) this.contract.getBot().sendListing(this.contract, e);
						// Twitter
						if(!this.contract.isExcludeTwitter()) this.contract.getTwitBot().sendListing(e);
					}
				}
			}
		}
		
		// Save time stamp for subsequent requests
		this.lastChecked = offset(newTime);
	}
	
	private void logInfoNewEvent(ListingEvent event) {
		LOGGER.info("{}, {}", event.toString(),this.contract.toString());
	}
	
	private Instant offset(Instant v) {
		return v
				.minus(DEFAULT_OFFSET_MINUTE, ChronoUnit.MINUTES)
				.minus(v.getNano(), ChronoUnit.NANOS);
	}
}
