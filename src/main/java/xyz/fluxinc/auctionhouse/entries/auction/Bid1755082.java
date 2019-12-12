package xyz.fluxinc.auctionhouse.entries.auction;

import net.jini.core.entry.Entry;

public class Bid1755082 implements Entry, Comparable<Bid1755082> {

    public Integer auctionId;
    public String username;
    public Double bidAmount;
    public Boolean isAccepted;

    public Bid1755082() {}

    public Bid1755082(int auctionId) { this.auctionId = auctionId; }

    public Bid1755082(int auctionId, String username, double bidAmount) {
        this.auctionId = auctionId;
        this.username = username;
        this.bidAmount = bidAmount;
    }

    @Override
    public int compareTo(Bid1755082 other) {
        return bidAmount.compareTo(other.bidAmount);
    }
}
