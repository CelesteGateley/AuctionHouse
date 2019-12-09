package xyz.fluxinc.AuctionHouse.entries;

import net.jini.core.entry.Entry;

public class AuctionHouseLock implements Entry {

    private byte[] lockCode;

    public AuctionHouseLock() {}

    public AuctionHouseLock(byte[] lockCode) { this.lockCode = lockCode; }
}
