package xyz.fluxinc.auctionhouse.exceptions.auction;

public class AuctionNotFoundException extends Exception {

    public AuctionNotFoundException(Exception message) { super(message); }

    public AuctionNotFoundException(String message) { super(message); }
}

