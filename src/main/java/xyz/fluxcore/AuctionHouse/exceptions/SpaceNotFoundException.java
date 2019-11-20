package xyz.fluxcore.AuctionHouse.exceptions;

public class SpaceNotFoundException extends Exception {

    public SpaceNotFoundException(Exception error) {
        super(error);
    }
    public SpaceNotFoundException(String error) { super(error); }
}
