package xyz.fluxinc.auctionhouse.entries.auctionhouse;

import net.jini.core.entry.Entry;

public class AuctionHouse1755082 implements Entry {

    public Integer currentAuctionCounter;

    public AuctionHouse1755082() {}

    public AuctionHouse1755082(int currentAuctionCounter) { this.currentAuctionCounter = currentAuctionCounter; }

    public void addAuction() { currentAuctionCounter += 1;}


}
