package xyz.fluxinc.auctionhouse.controllers;

import xyz.fluxinc.auctionhouse.entries.Auction1755082;
import xyz.fluxinc.auctionhouse.entries.AuctionHouse1755082;
import xyz.fluxinc.auctionhouse.entries.AuctionHouseLock1755082;
import xyz.fluxinc.auctionhouse.entries.Bid1755082;
import xyz.fluxinc.auctionhouse.exceptions.auction.AuctionNotFoundException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;

import java.util.List;
import java.util.Random;

public class AuctionHouseController {

    private static final long AUCTION_HOUSE_VALIDITY_PERIOD = SpaceController.ONE_DAY;
    private static final long AUCTION_HOUSE_LOOKUP_TIMEOUT = SpaceController.ONE_SECOND * 30;
    private static final long AUCTION_VALIDITY_PERIOD = SpaceController.ONE_DAY;
    private static final long BID_VALIDITY_PERIOD = SpaceController.ONE_DAY;

    private AuthenticationController authenticationController;
    private SpaceController spaceController;

    public AuctionHouseController(SpaceController spaceController, AuthenticationController authenticationController) throws SpaceException {
        this.spaceController = spaceController;
        this.authenticationController = authenticationController;
        setupAuctionHouse();
    }

    private AuctionHouse1755082 setupAuctionHouse() throws SpaceException {
        AuctionHouse1755082 house = null;
        if (isHouseLocked()) { house = spaceController.read(new AuctionHouse1755082(), AUCTION_HOUSE_LOOKUP_TIMEOUT); }
        if (house == null) {
            spaceController.put(new AuctionHouse1755082(0), AUCTION_HOUSE_VALIDITY_PERIOD);
            return (AuctionHouse1755082) spaceController.read(new AuctionHouse1755082());
        }
        return house;
    }

    public AuctionHouse1755082 getAuctionHouse() throws SpaceException {
        AuctionHouse1755082 house = spaceController.read(new AuctionHouse1755082());
        if (house == null) { return setupAuctionHouse(); }
        return (AuctionHouse1755082) spaceController.read(new AuctionHouse1755082());
    }

    private AuctionHouse1755082 takeAuctionHouse() throws SpaceException {
        AuctionHouse1755082 house = spaceController.read(new AuctionHouse1755082());
        if (house == null) { return setupAuctionHouse(); }

        return (AuctionHouse1755082) spaceController.take(new AuctionHouse1755082());
    }
    private boolean isHouseLocked() throws SpaceException {
        return spaceController.read(new AuctionHouseLock1755082()) == null;
    }

    public Auction1755082 placeAuction(String name, double buyItNowPrice) throws SpaceException {
        return placeAuction(name, buyItNowPrice, 1);
    }

    public Auction1755082 placeAuction(String name, double buyItNowPrice, double currentBid) throws SpaceException {
        byte[] key = new byte[7];
        new Random().nextBytes(key);
        AuctionHouseLock1755082 lock = new AuctionHouseLock1755082(key);
        spaceController.put(lock, SpaceController.ONE_MINUTE);
        AuctionHouse1755082 auctionHouse = takeAuctionHouse();

        Auction1755082 auction = new Auction1755082(auctionHouse.currentCount, authenticationController.getUsername(), name, buyItNowPrice, currentBid);
        auctionHouse.addAuction();

        spaceController.put(auction, AUCTION_VALIDITY_PERIOD);
        spaceController.put(auctionHouse, AUCTION_HOUSE_VALIDITY_PERIOD);
        spaceController.take(lock, SpaceController.ONE_MINUTE);

        return auction;
    }

    public Auction1755082 readAuction(int auctionId) throws SpaceException {
        Auction1755082 auction = new Auction1755082();
        auction.auctionId = auctionId;
        return spaceController.read(auction);
    }

    public List<Auction1755082> readAllAuctions() throws SpaceException {
        return spaceController.readAll(new Auction1755082(), 1000);
    }

    public List<Bid1755082> getBids(int auctionId) throws SpaceException, AuctionNotFoundException {
        Auction1755082 templateAuction = new Auction1755082();
        templateAuction.auctionId = auctionId;
        Auction1755082 auction = spaceController.read(templateAuction);
        if (auction == null) { throw new AuctionNotFoundException("An Auction with the ID " + auctionId + " was not found within the system"); }
        return spaceController.readAll(new Bid1755082(auctionId), auction.bidCount);
    }

    public Bid1755082 placeBid(int auctionId, double amount) throws SpaceException, AuctionNotFoundException {
        Auction1755082 template = new Auction1755082();
        template.auctionId = auctionId;
        Auction1755082 auction = spaceController.take(template);
        if (auction == null) { throw new AuctionNotFoundException("An Auction with the ID " + auctionId + " was not found within the system"); }
        Bid1755082 bid = new Bid1755082(auctionId, authenticationController.getUsername(), amount);
        spaceController.put(bid, BID_VALIDITY_PERIOD);
        auction.addBid();
        spaceController.put(auction, AUCTION_VALIDITY_PERIOD);
        return bid;
    }
}
