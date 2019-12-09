package xyz.fluxinc.AuctionHouse.entries;

import net.jini.core.entry.Entry;

import java.util.ArrayList;
import java.util.List;

public class Auction implements Entry {

    public Integer counterValue;
    public String name;
    public Double currentPrice;
    public Double buyItNowPrice;

    public Auction() {}

    public Auction(int counterValue, String name, double buyItNowPrice) {
        this.counterValue = counterValue;
        this.name = name;
        this.buyItNowPrice = buyItNowPrice;
        this.currentPrice = 1d;
    }

    public Auction(int counterValue, String name, double buyItNowPrice, double currentPrice) {
        this.counterValue = counterValue;
        this.name = name;
        this.buyItNowPrice = buyItNowPrice;
        this.currentPrice = currentPrice;
    }
}
