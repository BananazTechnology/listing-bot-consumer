package tech.bananaz.bot.discord;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.*;
import org.javacord.api.entity.message.*;
import org.javacord.api.entity.message.embed.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.bananaz.bot.models.Contract;
import tech.bananaz.bot.models.DiscordProperties;
import tech.bananaz.bot.models.ListingEvent;
import tech.bananaz.bot.utils.StringUtils;

import static java.util.Objects.nonNull;
import static java.util.Objects.isNull;
import java.awt.Color;

public class DiscordBot {
	
	/** Required */
	private DiscordApi bot = null;
	private ServerTextChannel channel;
	private Color color;
	
	/** Custom */
	private static final String CREATORNAME    = "@BananazTech";
	private static final String FOOTERIMAGEICO = "https://raw.githubusercontent.com/BananazTechnology/bananaz-assets/main/assets/navLogo.png";
	private static final Logger LOGGER  	   = LoggerFactory.getLogger(DiscordBot.class);
	private StringUtils sUtils 				   = new StringUtils();
	
	public DiscordBot(DiscordProperties config) {
		if(nonNull(config.getDiscordToken()) && nonNull(config.getChannelId())) {
			try {
		        this.bot = new DiscordApiBuilder().setToken(config.getDiscordToken()).login().join();
		        this.channel = this.bot.getChannelById(config.getChannelId()).get().asServerTextChannel().get();
				this.color = config.getMessageColor();
		        
		        if(!bot.getAccountType().name().equalsIgnoreCase("BOT")) {
		        	LOGGER.error("The account token is not for a BOT account!");
		        	throw new RuntimeException("The account token is not for a BOT account!");
		        }
			} catch (Exception e) {
				LOGGER.error("Failed starting bot! Exception: " + e.getMessage());
	        	throw new RuntimeException(e.getMessage());
			}
		}
	}
	
	public void sendListing(Contract contract, ListingEvent event) {
		logSend();
		if(nonNull(this.bot)) {
			// Build rarity value
			String finalRarity = 
				(nonNull(event.getRarity())) ? 
					String.format("**Rank** [%s](%s) on %s \n", event.getRarity(), event.getRarityRedirect(), event.getEngine().getDisplayName()) : 
					"";
			// Build title
			String title = String.format("%s Listed! (%s)", event.getName(), event.getMarket().getSlug());
			// Build embed
			EmbedBuilder newMsg = new EmbedBuilder()
				.setColor(this.color)
				.setAuthor(title)
				.setThumbnail(event.getImageUrl())
				.setDescription(
						finalRarity + 
					   ((event.getQuantity() > 1) ? "**Bundle** " + event.getQuantity() + "x \n" : "") +
					   "**Amount** " + sUtils.pricelineFormat(event.getPriceInCrypto(), event.getCryptoType(), event.getPriceInUsd()) + "\n" +
					   "**Seller** " + String.format("[`%s`](%s) \n", event.getSellerName(), event.getSellerUrl()) +
					   "**Link**   " + String.format("[Click Here](%s)", event.getPermalink())
				   )
				.setTimestamp(event.getCreatedDate())
				.setFooter(CREATORNAME, FOOTERIMAGEICO);
			
			new MessageBuilder().setEmbed(newMsg).send(this.channel);
		}
	}
	
	public void logSend() {
		LOGGER.debug("messaged triggered, connection={}", this.bot);
	}
	
	public boolean isTokenEqual(String thatToken) {
		if(nonNull(this.bot) && nonNull(thatToken)) return this.bot.getToken().equals(thatToken);
		return false;
	}

	public boolean isChannelIdEqual(String thatId) {
		if(isNull(this.bot)) return true;
		if(nonNull(this.bot) && nonNull(thatId)) return this.channel.getIdAsString().equals(thatId);
		return false;
	}

	public void setServerTextChannel(String id) {
		if(nonNull(this.bot)) this.channel = this.bot.getChannelById(id).get().asServerTextChannel().get();
	}

	public boolean isColorRgbEqual(Color thatRGB) {
		if(nonNull(this.color) && nonNull(thatRGB)) return this.color.equals(thatRGB);
		return false;
	}

	public void setColor(Color colorRGB) {
		if(nonNull(colorRGB)) this.color = colorRGB;
	}
	
	public DiscordApi getBot() {
		return this.bot;
	}
}