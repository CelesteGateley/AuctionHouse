package xyz.fluxinc.auctionhouse.entries.auction;

import net.jini.core.entry.Entry;

public class Auction1755082 implements Entry, Comparable<Auction1755082> {

    public Integer auctionId;
    public String ownerName;
    public String name;
    public Double minimumBid;
    public Double buyItNowPrice;
    public Integer bidCount;
    public AuctionStatus status;
    public String purchasedBy;

    public Auction1755082() {}

    public Auction1755082(int auctionId) { this.auctionId = auctionId; }

    public Auction1755082(int auctionId, String ownerName, String name, double buyItNowPrice) {
        this.auctionId = auctionId;
        this.ownerName = ownerName;
        this.name = name;
        this.buyItNowPrice = buyItNowPrice;
        this.minimumBid = 1d;
        this.status = AuctionStatus.OPEN;
    }

    public Auction1755082(int auctionId, String ownerName, String name, double buyItNowPrice, double minimumBid) {
        this.auctionId = auctionId;
        this.ownerName = ownerName;
        this.name = name;
        this.buyItNowPrice = buyItNowPrice;
        this.minimumBid = minimumBid;
        this.bidCount = 0;
        this.status = AuctionStatus.OPEN;
    }

    public void addBid() { bidCount++; }

    public void buyItNow() { status = AuctionStatus.BOUGHT; }

    public void acceptBid() { status = AuctionStatus.BID_ACCEPTED; }

    @Override
    public int compareTo(Auction1755082 other) {
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
