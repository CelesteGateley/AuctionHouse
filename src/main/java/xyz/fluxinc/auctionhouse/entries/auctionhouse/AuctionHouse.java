package xyz.fluxinc.auctionhouse.entries.auctionhouse;

import net.jini.core.entry.Entry;

public class AuctionHouse implements Entry {

    public Integer currentCount;

    public AuctionHouse() {}

    public AuctionHouse(int currentCount) { this.currentCount = currentCount; }

    public void addAuction() { currentCount += 1;}


}