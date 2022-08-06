package tech.bananaz.bot.services;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.bananaz.bot.models.Contract;
import tech.bananaz.bot.models.ContractCollection;
import tech.bananaz.bot.utils.ContractBuilder;
import tech.bananaz.models.DiscordConfig;
import tech.bananaz.models.Listing;
import tech.bananaz.models.TwitterConfig;
import tech.bananaz.repositories.EventPagingRepository;
import tech.bananaz.repositories.ListingConfigPagingRepository;
import tech.bananaz.utils.DiscordUtils;
import tech.bananaz.utils.TwitterUtils;

@Component
public class RunetimeScheduler {
	
	@Autowired
	private ListingConfigPagingRepository config;
	
	@Autowired
	private EventPagingRepository events;
	
	@Autowired
	private UpdateScheduler uScheduler;
	
	@Autowired
	private ContractCollection contracts;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RunetimeScheduler.class);
	
	@PostConstruct
	public void init() throws RuntimeException, InterruptedException {
		LOGGER.debug("--- Main App Statup ---");
		Iterable<Listing> listingStartupItems = config.findAll();
		for(Listing confItem : listingStartupItems) {
			try {
				// Build required components for each entry
				TwitterUtils twitBot = new TwitterConfig().configProperties(confItem);
				DiscordUtils bot = new DiscordConfig().configProperties(confItem);
				Contract watcher = new ContractBuilder().configProperties(confItem, bot, twitBot, this.config, this.events);
				watcher.startListingsScheduler();
				// Add this to internal memory buffer
				this.contracts.addContract(watcher);
			} catch (Exception e) {
				LOGGER.error("Failed to start config {}", confItem);
			}
		}
		LOGGER.debug("--- Init the UpdateScheduler ---");
		this.uScheduler.start();
	}
}