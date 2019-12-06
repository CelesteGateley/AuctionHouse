package xyz.fluxinc.AuctionHouse.entries;

import net.jini.core.entry.Entry;

public class AuctionHouseLock implements Entry {

    private String lockCode;

    public AuctionHouseLock() {}

    public AuctionHouseLock(String lockCode) { this.lockCode = lockCode; }
}
