package xyz.fluxinc.AuctionHouse.entries;

import net.jini.core.entry.Entry;

public class Bid implements Entry {

    public Integer auctionCounter;
    public Integer userId;
    public Double bidAmount;

    public Bid() {}

    public Bid(int auctionCounter, int userId, double bidAmount) {
        this.auctionCounter = auctionCounter;
        this.userId = userId;
        this.bidAmount = bidAmount;

    }
}
