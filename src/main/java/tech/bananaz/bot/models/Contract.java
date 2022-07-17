package tech.bananaz.bot.models;

import java.time.Instant;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import lombok.ToString.Exclude;
import tech.bananaz.bot.discord.DiscordBot;
import tech.bananaz.bot.repositories.ListingConfigRepository;
import tech.bananaz.bot.repositories.ListingEventRepository;
import tech.bananaz.bot.services.ListingsScheduler;
import tech.bananaz.bot.twitter.TwitterBot;

@ToString(includeFieldNames=true)
@Data
public class Contract {
	
	@Exclude
	@JsonIgnore
	private ListingsScheduler newRequest;
	
	@Exclude
	@JsonIgnore
	private ListingConfigRepository configs;
	
	@Exclude
	@JsonIgnore
	private ListingEventRepository events;

	// Pairs from DB definition
	private long id;
	private String contractAddress;
	private int interval;
	private boolean active 			  = true;

	// OpenSea settings
	boolean excludeOpensea 			  = false;
	// For bundles support
	private boolean showBundles 	  = true;

	// Discord Settings
	@Exclude
	@JsonIgnore
	private DiscordBot bot;
	boolean excludeDiscord = false;

	// Twitter Settings
	@Exclude
	@JsonIgnore
	private TwitterBot twitBot;
	private boolean excludeTwitter 	  = false;
	
	// LooksRare settings
	private boolean excludeLooks      = false;
	
	// For the DB and API
	private String uuid				  = UUID.randomUUID().toString();
	@SuppressWarnings("unused")
	private Instant lastChecked;
	

	public void startListingsScheduler() {
		newRequest = new ListingsScheduler(this);
		newRequest.start();
	}
	
	public void stopListingsScheduler() {
		newRequest.stop();
	}
	
	public Instant getLastChecked() {
		return this.newRequest.getLastChecked();
	}
}
