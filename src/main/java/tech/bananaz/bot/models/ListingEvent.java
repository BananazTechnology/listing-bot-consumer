package tech.bananaz.bot.models;

import java.math.BigDecimal;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;
import lombok.ToString;
import tech.bananaz.bot.utils.EventType;
import tech.bananaz.bot.utils.MarketPlace;
import tech.bananaz.bot.utils.RarityEngine;
import tech.bananaz.bot.utils.Ticker;

@ToString(includeFieldNames=true)
@Data
@Entity
@Table(name = "event")
public class ListingEvent {
	
	@Transient
	private Contract contract;
	
	// Required for entity
	@Id
	private long         id;
	@Column(columnDefinition = "VARCHAR(75)")
	private String       name;
	private Instant      createdDate;
	private Instant      startTime;
	private Instant      endTime;
	@Column(columnDefinition = "VARCHAR(50)")
	private String       tokenId;
	@Column(columnDefinition = "VARCHAR(75)")
	private String       collectionName;
	private String       collectionImageUrl;
	@Column(columnDefinition = "VARCHAR(50)")
	private String       slug;
	private String       imageUrl;
	@Column(columnDefinition = "VARCHAR(127)")
	private String       permalink;
	private int	         quantity;
	@Column(columnDefinition = "VARCHAR(50)")
	private String       sellerWalletAddy;
	@Column(columnDefinition = "VARCHAR(50)")
	private String       sellerName;
	@Column(columnDefinition = "VARCHAR(127)")
	private String       sellerUrl;
	@Column(columnDefinition = "VARCHAR(6)")
	private String       rarity;
	@Enumerated( EnumType.STRING )
	@Column(columnDefinition = "VARCHAR(50)")
	private RarityEngine rarityEngine;
	@Column(columnDefinition = "VARCHAR(50)")
	private String       rarityUrl;
	@Column(columnDefinition = "VARCHAR(25)")
	private BigDecimal   priceInCrypto;
	@Column(columnDefinition = "VARCHAR(25)")
	private BigDecimal   priceInUsd;
	@Enumerated( EnumType.STRING )
	@Column(columnDefinition = "VARCHAR(6)")
	private Ticker       cryptoType;
	@Enumerated( EnumType.STRING )
	@Column(columnDefinition = "VARCHAR(7)")
	private EventType    eventType;
	@Enumerated( EnumType.STRING )
	@Column(columnDefinition = "VARCHAR(50)")
	private MarketPlace  market;
	// These last few items are for the consumer
	private long         configId;
	@Column(columnDefinition = "VARCHAR(50)")
	private String       consumedBy;
	@Column(nullable = false, columnDefinition="TINYINT(1) UNSIGNED DEFAULT 0")
	private boolean      consumed;
	
	public ListingEvent() {}
	
	public ListingEvent(Contract contract) {
		this.contract = contract;
	}
}
