package xyz.fluxcore.AuctionHouse.exceptions;

import xyz.fluxcore.AuctionHouse.entries.User;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(String message) {
        super(message);
    }
}
