package xyz.fluxinc.AuctionHouse.exceptions;

public class SpaceNotFoundException extends Exception {

    public SpaceNotFoundException(Exception error) {
        super(error);
    }
    public SpaceNotFoundException(String error) { super(error); }
}
