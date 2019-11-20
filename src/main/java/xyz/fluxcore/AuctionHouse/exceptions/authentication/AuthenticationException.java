package xyz.fluxcore.AuctionHouse.exceptions.authentication;

public class AuthenticationException extends Exception {

    public AuthenticationException(Exception message) {
        super(message);
    }
    public AuthenticationException(String message) { super(message); }
}
