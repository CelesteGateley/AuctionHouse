package xyz.fluxcore.AuctionHouse.entries;

import net.jini.core.entry.Entry;

import java.util.ArrayList;
import java.util.List;

public class Auction implements Entry {

    public int counterValue;
    public String name;
    public double currentPrice;
    public double buyItNowPrice;
    public List<Bid> bids;

    public Auction() {}

    public Auction(int counterValue, String name, double buyItNowPrice) {
        this.counterValue = counterValue;
        this.name = name;
        this.buyItNowPrice = buyItNowPrice;
        this.currentPrice = 1;
        this.bids = new ArrayList<Bid>();
    }

    public Auction(int counterValue, String name, double buyItNowPrice, double currentPrice) {
        this.counterValue = counterValue;
        this.name = name;
        this.buyItNowPrice = buyItNowPrice;
        this.currentPrice = currentPrice;
        this.bids = new ArrayList<Bid>();
    }

    public void addBid(int userId, double amount) {

    }
}
