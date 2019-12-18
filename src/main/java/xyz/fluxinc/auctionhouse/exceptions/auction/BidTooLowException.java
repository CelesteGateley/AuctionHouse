package xyz.fluxinc.auctionhouse.exceptions.auction;

public class BidTooLowException extends Throwable {
    public BidTooLowException(String s) {
        super(s);
    }
}
