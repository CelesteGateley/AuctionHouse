package xyz.fluxinc.auctionhouse.entries.auctionhouse;

import net.jini.core.entry.Entry;

public class AuctionHouse1755082 implements Entry {

    public Integer currentCount;

    public AuctionHouse1755082() {}

    public AuctionHouse1755082(int currentCount) { this.currentCount = currentCount; }

    public void addAuction() { currentCount += 1;}


}
