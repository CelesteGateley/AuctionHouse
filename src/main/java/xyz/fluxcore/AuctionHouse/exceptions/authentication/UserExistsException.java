package xyz.fluxcore.AuctionHouse.exceptions.authentication;

public class UserExistsException extends Exception {

    public UserExistsException(String message) {
        super(message);
    }
}
