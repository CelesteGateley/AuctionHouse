package xyz.fluxinc.auctionhouse.entries;

import net.jini.core.entry.Entry;

public class Auction1755082 implements Entry {

    public Integer auctionId;
    public String ownerName;
    public String name;
    public Double currentPrice;
    public Double buyItNowPrice;
    public Integer bidCount;

    public Auction1755082() {}

    public Auction1755082(int auctionId, String ownerName, String name, double buyItNowPrice) {
        this.auctionId = auctionId;
        this.ownerName = ownerName;
        this.name = name;
        this.buyItNowPrice = buyItNowPrice;
        this.currentPrice = 1d;
    }

    public Auction1755082(int auctionId, String ownerName, String name, double buyItNowPrice, double currentPrice) {
        this.auctionId = auctionId;
        this.ownerName = ownerName;
        this.name = name;
        this.buyItNowPrice = buyItNowPrice;
        this.currentPrice = currentPrice;
        this.bidCount = 0;
    }

    public void addBid() {
        bidCount++;
    }
}
