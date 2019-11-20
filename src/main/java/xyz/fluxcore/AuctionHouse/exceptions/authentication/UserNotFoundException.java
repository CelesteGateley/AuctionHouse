package xyz.fluxcore.AuctionHouse.exceptions.authentication;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(Exception message) {
        super(message);
    }
    public UserNotFoundException(String message) { super(message); }
}
