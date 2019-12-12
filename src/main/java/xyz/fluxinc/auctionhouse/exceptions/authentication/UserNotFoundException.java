package xyz.fluxinc.auctionhouse.exceptions.authentication;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(Exception message) { super(message); }

    public UserNotFoundException(String message) { super(message); }
}
