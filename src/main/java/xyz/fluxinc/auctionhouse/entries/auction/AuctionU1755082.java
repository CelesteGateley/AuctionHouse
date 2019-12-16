package xyz.fluxinc.auctionhouse.entries.auction;

import net.jini.core.entry.Entry;

public class AuctionU1755082 implements Entry {

    public Integer auctionId;
    public String ownerName;
    public String name;
    public Double currentPrice;
    public Double buyItNowPrice;
    public Integer bidCount;
    public Boolean isClosed;
    public String purchasedBy;

    public AuctionU1755082() {}

    public AuctionU1755082(int auctionId) { this.auctionId = auctionId; }

    public AuctionU1755082(int auctionId, String ownerName, String name, double buyItNowPrice) {
        this.auctionId = auctionId;
        this.ownerName = ownerName;
        this.name = name;
        this.buyItNowPrice = buyItNowPrice;
        this.currentPrice = 1d;
        this.isClosed = false;
    }

    public AuctionU1755082(int auctionId, String ownerName, String name, double buyItNowPrice, double currentPrice) {
        this.auctionId = auctionId;
        this.ownerName = ownerName;
        this.name = name;
        this.buyItNowPrice = buyItNowPrice;
        this.currentPrice = currentPrice;
        this.bidCount = 0;
        this.isClosed = false;
    }

    public void addBid() { bidCount++; }

    public void close() { isClosed = false; }

    public String toString() { return name; }
}
