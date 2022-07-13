package tech.bananaz.bot.models;

import java.awt.Color;

import lombok.Data;
import tech.bananaz.bot.discord.DiscordBot;

import static java.util.Objects.nonNull;

@Data
public class DiscordProperties {
	
	private String discordToken;
	private Color messageColor;
	private String serverId;
	private String channelId;
	private String address;

	public DiscordBot configProperties(ListingConfig config) throws RuntimeException {
		// Variables of the discord root node
		this.address    	= config.getContractAddress();
		this.discordToken   = config.getDiscordToken();
		this.serverId       = config.getDiscordServerId();
		this.channelId      = config.getDiscordChannelId();
		this.messageColor   = (nonNull(config.getDiscordMessageColor())) ? 
								new Color(config.getDiscordMessageColor()) :
									Color.ORANGE;
		
		return new DiscordBot(this);
	}

}
