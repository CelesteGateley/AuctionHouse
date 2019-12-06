package xyz.fluxinc.AuctionHouse.controllers;

import xyz.fluxinc.AuctionHouse.exceptions.space.SpaceException;
import xyz.fluxinc.AuctionHouse.exceptions.space.SpaceNotFoundException;

public class SystemController {

    private AuthenticationController authenticationController;
    private UserInterfaceController userInterfaceController;
    private AuctionHouseController auctionHouseController;

    private SpaceController spaceController;

    public SystemController(String url) throws SpaceException, SpaceNotFoundException {
        spaceController = new SpaceController(url);
        authenticationController = new AuthenticationController(spaceController);
        auctionHouseController = new AuctionHouseController(spaceController);
        userInterfaceController = new UserInterfaceController(authenticationController);
    }

    public UserInterfaceController getUserInterfaceController() {
        return userInterfaceController;
    }

    public AuctionHouseController getAuctionHouseController() {
        return auctionHouseController;
    }

    public AuthenticationController getAuthenticationController() {
        return authenticationController;
    }
}
