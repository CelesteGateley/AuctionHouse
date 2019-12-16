package xyz.fluxinc.auctionhouse.controllers;

import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceNotFoundException;

import java.rmi.server.ExportException;

public class SystemController {

    private AuthenticationController authenticationController;
    private UserInterfaceController userInterfaceController;
    private AuctionHouseController auctionHouseController;

    private SpaceController spaceController;

    public SystemController(String url) throws SpaceException, SpaceNotFoundException, ExportException {
        spaceController = new SpaceController(url);
        authenticationController = new AuthenticationController(spaceController);
        auctionHouseController = new AuctionHouseController(spaceController, authenticationController);
        authenticationController.assignAuctionHouse(auctionHouseController);
        userInterfaceController = new UserInterfaceController(authenticationController, auctionHouseController);
    }

    public UserInterfaceController getUserInterfaceController() { return userInterfaceController;}

    public AuctionHouseController getAuctionHouseController() { return auctionHouseController; }

    public AuthenticationController getAuthenticationController() { return authenticationController; }

    public SpaceController getSpaceController() { return spaceController; }
}
