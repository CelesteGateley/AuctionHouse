package xyz.fluxinc.auctionhouse.exceptions.bid;

public class BidBySameUserException extends Exception {

    public BidBySameUserException(String e) { super(e); }

    public BidBySameUserException(Exception e) { super(e); }
}
