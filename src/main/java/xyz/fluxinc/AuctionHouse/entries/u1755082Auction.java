package xyz.fluxinc.AuctionHouse.entries;

import net.jini.core.entry.Entry;

import java.util.ArrayList;
import java.util.List;

public class u1755082Auction implements Entry {

    public int counterValue;
    public String name;
    public double currentPrice;
    public double buyItNowPrice;
    public List<u1755082Bid> bids;

    public u1755082Auction() {}

    public u1755082Auction(int counterValue, String name, double buyItNowPrice) {
        this.counterValue = counterValue;
        this.name = name;
        this.buyItNowPrice = buyItNowPrice;
        this.currentPrice = 1;
        this.bids = new ArrayList<u1755082Bid>();
    }

    public u1755082Auction(int counterValue, String name, double buyItNowPrice, double currentPrice) {
        this.counterValue = counterValue;
        this.name = name;
        this.buyItNowPrice = buyItNowPrice;
        this.currentPrice = currentPrice;
        this.bids = new ArrayList<u1755082Bid>();
    }

    public void addBid(int userId, double amount) {

    }
}
