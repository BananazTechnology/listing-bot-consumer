package tech.bananaz.bot.services;

import static tech.bananaz.utils.EncryptionUtils.decryptListing;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class RuntimeScheduler {
	
	// Security
	@Value("${bot.encryptionKey}")
	private String key;
	
	@Autowired
	private ListingConfigPagingRepository config;
	
	@Autowired
	private EventPagingRepository events;
	
	@Autowired
	private UpdateScheduler uScheduler;
	
	@Autowired
	private ContractCollection contracts;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeScheduler.class);
	
	@PostConstruct
	public void init() throws RuntimeException, InterruptedException {
		LOGGER.debug("--- Main App Statup ---");
		Iterable<Listing> listingStartupItems = config.findAll();
		for(Listing confItem : listingStartupItems) {
			try {
				Listing decryptedListing = decryptListing(this.key, confItem);
				// Build required components for each entry
				TwitterUtils twitBot = new TwitterConfig().configProperties(decryptedListing);
				DiscordUtils bot = new DiscordConfig().configProperties(decryptedListing);
				Contract watcher = new ContractBuilder().configProperties(decryptedListing, bot, twitBot, this.config, this.events);
				watcher.startListingsScheduler();
				// Add this to internal memory buffer
				this.contracts.addContract(watcher);
			} catch (Exception e) {
				LOGGER.error("Failed starting config with id {}, exception {}", confItem.getId(), e.getMessage());
			}
		}
		LOGGER.debug("--- Init the UpdateScheduler ---");
		this.uScheduler.start();
	}
}