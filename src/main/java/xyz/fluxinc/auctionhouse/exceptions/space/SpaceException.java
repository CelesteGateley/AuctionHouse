package xyz.fluxinc.auctionhouse.exceptions.space;

public class SpaceException extends Exception {

    public SpaceException(Exception error) {
        super(error);
    }
    public SpaceException(String error) { super(error); }
}
