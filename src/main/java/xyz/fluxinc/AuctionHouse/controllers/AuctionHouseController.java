package xyz.fluxinc.AuctionHouse.controllers;

import xyz.fluxinc.AuctionHouse.entries.AuctionHouse;
import xyz.fluxinc.AuctionHouse.entries.AuctionHouseLock;
import xyz.fluxinc.AuctionHouse.exceptions.space.SpaceException;
import xyz.fluxinc.AuctionHouse.exceptions.space.SpaceNotFoundException;

public class AuctionHouseController {

    private static final long AUCTION_HOUSE_VALIDITY_PERIOD = SpaceController.ONE_DAY * 7;

    private SpaceController spaceController;

    public AuctionHouseController(SpaceController spaceController) throws SpaceException, SpaceNotFoundException {
        this.spaceController = spaceController;
    }

    private AuctionHouse setupAuctionHouse() throws SpaceException {
        AuctionHouse auctionHouse = new AuctionHouse();
        spaceController.put(auctionHouse, AUCTION_HOUSE_VALIDITY_PERIOD);
        return auctionHouse;
    }


    private AuctionHouse getAuctionHouse() throws SpaceException {
        AuctionHouse house = (AuctionHouse) spaceController.read(new AuctionHouse());
        if (house == null) { return setupAuctionHouse(); }
        else { return house;}
    }

    private boolean isHouseLocked() throws SpaceException {
        return spaceController.read(new AuctionHouseLock()) == null;
    }


}
