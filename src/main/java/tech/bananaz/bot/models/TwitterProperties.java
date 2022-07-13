package tech.bananaz.bot.models;

import lombok.Data;
import tech.bananaz.bot.twitter.TwitterBot;

@Data
public class TwitterProperties {

	private String address;
	private String apiKey;
	private String apiSecretKey;
	private String accessToken;
	private String accessTokenSecret;
	
	public TwitterBot configProperties(ListingConfig config) {
		// Variables of the discord root node
		this.address  		   = config.getContractAddress();
		this.apiKey 		   = config.getTwitterApiKey();
		this.apiSecretKey 	   = config.getTwitterApiKeySecret();
		this.accessToken 	   = config.getTwitterAccessToken();
		this.accessTokenSecret = config.getTwitterAccessTokenSecret();
		
		return new TwitterBot(this);
	}

}