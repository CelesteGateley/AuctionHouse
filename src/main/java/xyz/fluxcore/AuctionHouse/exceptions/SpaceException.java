package xyz.fluxcore.AuctionHouse.exceptions;

public class SpaceException extends Exception {

    public SpaceException(Exception error) {
        super(error);
    }
    public SpaceException(String error) { super(error); }
}
