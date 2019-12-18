package xyz.fluxinc.auctionhouse.entries.auction;

import net.jini.core.entry.Entry;

public class Auction implements Entry, Comparable<Auction> {

    public Integer auctionId;
    public String ownerName;
    public String name;
    public Double currentPrice;
    public Double buyItNowPrice;
    public Integer bidCount;
    public AuctionStatus status;
    public String purchasedBy;

    public Auction() {}

    public Auction(int auctionId) { this.auctionId = auctionId; }

    public Auction(int auctionId, String ownerName, String name, double buyItNowPrice) {
        this.auctionId = auctionId;
        this.ownerName = ownerName;
        this.name = name;
        this.buyItNowPrice = buyItNowPrice;
        this.currentPrice = 1d;
        this.status = AuctionStatus.OPEN;
    }

    public Auction(int auctionId, String ownerName, String name, double buyItNowPrice, double currentPrice) {
        this.auctionId = auctionId;
        this.ownerName = ownerName;
        this.name = name;
        this.buyItNowPrice = buyItNowPrice;
        this.currentPrice = currentPrice;
        this.bidCount = 0;
        this.status = AuctionStatus.OPEN;
    }

    public void addBid() { bidCount++; }

    public void buyItNow() { status = AuctionStatus.BOUGHT; }

    public void acceptBid() { status = AuctionStatus.BID_ACCEPTED; }

    @Override
    public int compareTo(Auction other) {
        if (status == other.status) {
            return 0;
        } else if (status == AuctionStatus.OPEN) {
            return -1;
        } else {
            return 1;
        }
    }

    public String toString() {
        return name + " (" + status + ")";
    }
}
