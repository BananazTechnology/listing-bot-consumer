package tech.bananaz.bot.models;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import lombok.ToString.Exclude;
import tech.bananaz.utils.DiscordUtils;
import tech.bananaz.repositories.EventPagingRepository;
import tech.bananaz.repositories.ListingConfigPagingRepository;
import tech.bananaz.bot.services.ListingsScheduler;
import tech.bananaz.models.Listing;
import tech.bananaz.utils.TwitterUtils;
import static java.util.Objects.nonNull;

@ToString(includeFieldNames=true)
@Data
public class Contract {
	
	@Exclude
	@JsonIgnore
	private ListingsScheduler newRequest;
	
	@Exclude
	@JsonIgnore
	private ListingConfigPagingRepository configs;
	
	@Exclude
	@JsonIgnore
	private EventPagingRepository events;

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
	private DiscordUtils bot;
	boolean excludeDiscord = false;

	// Twitter Settings
	@Exclude
	@JsonIgnore
	private TwitterUtils twitBot;
	private boolean excludeTwitter 	  = false;
	
	// LooksRare settings
	private boolean excludeLooks      = false;
	
	// For the DB and API
	private String uuid				  = UUID.randomUUID().toString();
	
	// To save on DB calls
	Listing config;

	public void startListingsScheduler() {
		newRequest = new ListingsScheduler(this);
		newRequest.start();
	}
	
	public void stopListingsScheduler() {
		newRequest.stop();
	}
	
	public boolean getIsSchedulerActive() {
		return this.newRequest.isActive();
	}
	
	public String getDiscordInviteLink() {
		String outputUrl = null;
		if(nonNull(this.bot)) {
			if(nonNull(this.bot.getBot())) outputUrl = this.bot.getBot().createBotInvite();
		}
		return outputUrl;
	}
}
