package tech.bananaz.bot.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.bananaz.bot.discord.DiscordBot;
import tech.bananaz.bot.repositories.ListingConfigRepository;
import tech.bananaz.bot.repositories.ListingEventRepository;
import tech.bananaz.bot.twitter.TwitterBot;

@Component
public class ListingsProperties {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ListingsProperties.class);

	public Contract configProperties(ListingConfig config, DiscordBot bot, TwitterBot twitBot, ListingConfigRepository configs, ListingEventRepository events) throws RuntimeException, InterruptedException {
		Contract output = null;
		try {
			// If no server or outputChannel then throw exception
			output = new Contract();
			output.setConfigs(configs);
			output.setEvents(events);
			output.setId(config.getId());
			output.setContractAddress(config.getContractAddress());
			output.setInterval(config.getInterval());
			output.setBot(bot);
			output.setTwitBot(twitBot);
			output.setExcludeOpensea(config.getExcludeOpensea());
			output.setExcludeLooks(config.getExcludeLooksrare());
			output.setExcludeDiscord(config.getExcludeDiscord());
			output.setExcludeTwitter(config.getExcludeTwitter());
			output.setShowBundles(config.getShowBundles());
			
		} catch (Exception e) {
			LOGGER.error("Check properties {}, Exception: {}", config.toString(), e.getMessage());
			throw new RuntimeException("Check properties " + config.toString() + ", Exception: " + e.getMessage());
		}
		return output;
	}

}
