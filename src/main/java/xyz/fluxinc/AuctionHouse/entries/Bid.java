package xyz.fluxinc.AuctionHouse.entries;

import net.jini.core.entry.Entry;

public class Bid implements Entry {

    public int userId;
    public double bidAmount;

    public Bid() {}

    public Bid(int userId, double bidAmount) {

    }
}
