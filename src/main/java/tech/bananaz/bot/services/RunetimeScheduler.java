package tech.bananaz.bot.services;

import java.util.*;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tech.bananaz.bot.discord.DiscordBot;
import tech.bananaz.bot.models.Contract;
import tech.bananaz.bot.models.ContractCollection;
import tech.bananaz.bot.models.DiscordProperties;
import tech.bananaz.bot.models.ListingConfig;
import tech.bananaz.bot.models.ListingsProperties;
import tech.bananaz.bot.models.TwitterProperties;
import tech.bananaz.bot.repositories.ListingConfigRepository;
import tech.bananaz.bot.repositories.ListingEventRepository;
import tech.bananaz.bot.twitter.TwitterBot;

@Component
public class RunetimeScheduler {
	
	@Autowired
	private ListingConfigRepository config;
	
	@Autowired
	private ListingEventRepository events;
	
	@Autowired
	private UpdateScheduler uScheduler;
	
	@Autowired
	private ContractCollection contracts;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RunetimeScheduler.class);
	
	@PostConstruct
	public void init() throws RuntimeException, InterruptedException {
		LOGGER.debug("--- Main App Statup ---");
		List<ListingConfig> listingStartupItems = config.findAll();
		for(ListingConfig confItem : listingStartupItems) {
			// Build required components for each entry
			TwitterBot twitBot = new TwitterProperties().configProperties(confItem);
			DiscordBot bot = new DiscordProperties().configProperties(confItem);
			Contract watcher = new ListingsProperties().configProperties(confItem, bot, twitBot, this.config, this.events);
			watcher.startListingsScheduler();
			// Add this to internal memory buffer
			this.contracts.addContract(watcher);
		}
		LOGGER.debug("--- Init the UpdateScheduler ---");
		this.uScheduler.start();
	}
}