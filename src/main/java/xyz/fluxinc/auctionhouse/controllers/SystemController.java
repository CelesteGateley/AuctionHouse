package xyz.fluxinc.auctionhouse.controllers;

import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceNotFoundException;

public class SystemController {

    private AuthenticationController authenticationController;
    private UserInterfaceController userInterfaceController;
    private AuctionHouseController auctionHouseController;

    private SpaceController spaceController;

    public SystemController(String url) throws SpaceException, SpaceNotFoundException {
        spaceController = new SpaceController(url);
        authenticationController = new AuthenticationController(spaceController);
        auctionHouseController = new AuctionHouseController(spaceController, authenticationController);
        //userInterfaceController = new UserInterfaceController(authenticationController);
    }

    public UserInterfaceController getUserInterfaceController() { return userInterfaceController;}

    public AuctionHouseController getAuctionHouseController() { return auctionHouseController; }

    public AuthenticationController getAuthenticationController() { return authenticationController; }

    public SpaceController getSpaceController() { return spaceController; }
}