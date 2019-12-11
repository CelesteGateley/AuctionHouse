package xyz.fluxinc.AuctionHouse.entries;

import net.jini.core.entry.Entry;

public class Bid implements Entry {

    public Integer auctionId;
    public String username;
    public Double bidAmount;

    public Bid() {}

    public Bid(int auctionId) { this.auctionId = auctionId; }

    public Bid(int auctionId, String username, double bidAmount) {
        this.auctionId = auctionId;
        this.username = username;
        this.bidAmount = bidAmount;
    }
}
