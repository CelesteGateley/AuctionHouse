package xyz.fluxcore.AuctionHouse.entries;

import net.jini.core.entry.Entry;

public class u1755082AuctionHouse implements Entry {

    public int currentCount;

    public u1755082AuctionHouse() { currentCount = 0; }

    public void addAuction() { currentCount += 1;}


}
