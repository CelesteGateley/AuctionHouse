package xyz.fluxinc.AuctionHouse.entries;

import net.jini.core.entry.Entry;

public class Bid implements Entry {

    public Integer auctionId;
    public Integer userId;
    public Double bidAmount;

    public Bid() {}

    public Bid(int auctionId) { this.auctionId = auctionId; }

    public Bid(int auctionId, int userId, double bidAmount) {
        this.auctionId = auctionId;
        this.userId = userId;
        this.bidAmount = bidAmount;
    }
}
