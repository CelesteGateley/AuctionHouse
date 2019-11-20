package xyz.fluxcore.AuctionHouse;

import xyz.fluxcore.AuctionHouse.controllers.AuctionHouseController;
import xyz.fluxcore.AuctionHouse.entries.User;
import xyz.fluxcore.AuctionHouse.exceptions.authentication.AuthenticationException;
import xyz.fluxcore.AuctionHouse.exceptions.SpaceException;
import xyz.fluxcore.AuctionHouse.exceptions.SpaceNotFoundException;
import xyz.fluxcore.AuctionHouse.exceptions.authentication.UserExistsException;
import xyz.fluxcore.AuctionHouse.exceptions.authentication.UserNotFoundException;

import java.util.Collection;

public class Main {

    public static void main(String[] args) throws SpaceNotFoundException, SpaceException, UserNotFoundException, AuthenticationException, UserExistsException {
        AuctionHouseController auctionHouseController = new AuctionHouseController("homeserver");
        //auctionHouseController.register("login", "password");
        auctionHouseController.login("login", "password");
    }
}
