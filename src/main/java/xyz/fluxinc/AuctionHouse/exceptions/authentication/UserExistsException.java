package xyz.fluxinc.AuctionHouse.exceptions.authentication;

public class UserExistsException extends Exception {

    public UserExistsException(Exception message) {
        super(message);
    }
    public UserExistsException(String message) { super(message); }
}
