package xyz.fluxinc.AuctionHouse.controllers;

import xyz.fluxinc.AuctionHouse.exceptions.SpaceException;
import xyz.fluxinc.AuctionHouse.exceptions.SpaceNotFoundException;

public class AuctionHouseController {

    private SpaceController spaceController;

    public AuctionHouseController(SpaceController spaceController) throws SpaceException, SpaceNotFoundException {
        this.spaceController = spaceController;
    }


}
