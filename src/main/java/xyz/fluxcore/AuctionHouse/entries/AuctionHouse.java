package xyz.fluxcore.AuctionHouse.entries;

import net.jini.core.entry.Entry;

public class AuctionHouse implements Entry {

    public int currentCount;

    public AuctionHouse() { currentCount = 0; }

    public void addAuction() { currentCount += 1;}


}
