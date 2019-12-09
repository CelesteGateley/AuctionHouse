package xyz.fluxinc.AuctionHouse.controllers;

import xyz.fluxinc.AuctionHouse.entries.Auction;
import xyz.fluxinc.AuctionHouse.entries.AuctionHouse;
import xyz.fluxinc.AuctionHouse.entries.AuctionHouseLock;
import xyz.fluxinc.AuctionHouse.exceptions.space.SpaceException;

import java.util.Random;

public class AuctionHouseController {

    private static final long AUCTION_HOUSE_VALIDITY_PERIOD = SpaceController.ONE_DAY;
    private static final long AUCTION_HOUSE_LOOKUP_TIMEOUT = SpaceController.ONE_SECOND * 30;
    private static final long AUCTION_VALIDITY_PERIOD = SpaceController.ONE_DAY;

    private SpaceController spaceController;

    public AuctionHouseController(SpaceController spaceController) {
        this.spaceController = spaceController;
    }

    private AuctionHouse setupAuctionHouse() throws SpaceException {
        AuctionHouse house = null;
        if (isHouseLocked()) {
            house = (AuctionHouse) spaceController.read(new AuctionHouse(), AUCTION_HOUSE_LOOKUP_TIMEOUT);
        }
        if (house == null) {
            spaceController.put(new AuctionHouse(), AUCTION_HOUSE_VALIDITY_PERIOD);
            return (AuctionHouse) spaceController.read(new AuctionHouse());
        }
        return house;
    }

    public AuctionHouse getAuctionHouse() throws SpaceException {
        AuctionHouse house = (AuctionHouse) spaceController.read(new AuctionHouse());
        if (house == null) { setupAuctionHouse(); }

        return (AuctionHouse) spaceController.read(new AuctionHouse());
    }

    private AuctionHouse takeAuctionHouse() throws SpaceException {
        AuctionHouse house = (AuctionHouse) spaceController.read(new AuctionHouse());
        if (house == null) { setupAuctionHouse(); }

        return (AuctionHouse) spaceController.take(new AuctionHouse());
    }
    private boolean isHouseLocked() throws SpaceException {
        return spaceController.read(new AuctionHouseLock()) == null;
    }

    public Auction placeAuction(String name, double buyItNowPrice) throws SpaceException {
        return placeAuction(name, buyItNowPrice, 1);
    }

    public Auction placeAuction(String name, double buyItNowPrice, double currentBid) throws SpaceException {
        byte[] key = new byte[7];
        new Random().nextBytes(key);
        AuctionHouseLock lock = new AuctionHouseLock(key);
        spaceController.put(lock, SpaceController.ONE_MINUTE);
        AuctionHouse auctionHouse = takeAuctionHouse();

        Auction auction = new Auction(auctionHouse.currentCount, name, buyItNowPrice, currentBid);
        auctionHouse.addAuction();

        spaceController.put(auction, AUCTION_VALIDITY_PERIOD);
        spaceController.put(auctionHouse, AUCTION_HOUSE_VALIDITY_PERIOD);
        spaceController.take(lock, SpaceController.ONE_MINUTE);

        return auction;
    }

    public Auction readAuction(String name) throws SpaceException {
        Auction auction = new Auction();
        auction.name = name;
        return (Auction) spaceController.read(auction);
    }
}
