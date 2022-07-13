package tech.bananaz.bot.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.redouane59.twitter.TwitterClient;
import com.github.redouane59.twitter.signature.*;

import tech.bananaz.bot.models.ListingEvent;
import tech.bananaz.bot.models.TwitterProperties;

import static java.util.Objects.nonNull;
import static java.util.Objects.isNull;

public class TwitterBot {
	
	private TwitterClient bot = null;
	private String apiKey;
	private String apiSecret;
	private static final Logger LOGGER 	 = LoggerFactory.getLogger(TwitterBot.class);
	private final static String NEWLINE = "\n";
	private final static String BRAIL_BLANK = "⠀";

	public TwitterBot(TwitterProperties config) {
		if(nonNull(config.getApiKey()) && nonNull(config.getApiSecretKey()) && nonNull(config.getAccessToken())) {
			try {
				this.apiKey    = config.getApiKey();
				this.apiSecret = config.getApiSecretKey();
				this.bot = new TwitterClient(TwitterCredentials.builder()
	                .accessToken(config.getAccessToken())
	                .accessTokenSecret(config.getAccessTokenSecret())
	                .apiKey(config.getApiKey())
	                .apiSecretKey(config.getApiSecretKey())
	                .build());
			} catch (Exception e) {
				LOGGER.error("Failed starting bot! Exception: " + e.getMessage());
	        	throw new RuntimeException(e.getMessage());
			}
		}
	}
	
	public void sendListing(ListingEvent event) {
		logSend();
		if(this.bot != null) {
			String finalRarity = (event.getRarity() != null) ? String.format("rank %s on rarity tools %s", event.getRarity(), NEWLINE) : "";
			bot.postTweet(
				event.getDisplayNameOutput() + NEWLINE +
				finalRarity +
				"Listed for " + event.getListingAmoutOutput() + NEWLINE +
				event.getPermalink() + BRAIL_BLANK + NEWLINE)
			;
		}
	}
	
	public void logSend() {
		LOGGER.debug("message triggered, botConnection={}", this.bot);
	}

	public boolean apiKeyEquals(String thatAPIKey) {
		if(isNull(this.apiKey) && isNull(thatAPIKey)) return true;
		if(nonNull(this.apiKey)) return this.apiKey.equals(thatAPIKey);
		return false;
		
	}

	public boolean apiKeySecretEquals(String thatAPIKeySecret) {
		if(isNull(this.apiSecret) && isNull(thatAPIKeySecret)) return true;
		if(nonNull(this.apiSecret)) return this.apiSecret.equals(thatAPIKeySecret);
		return false;
	}
	
	public TwitterClient getBot() {
		return this.bot;
	}

}