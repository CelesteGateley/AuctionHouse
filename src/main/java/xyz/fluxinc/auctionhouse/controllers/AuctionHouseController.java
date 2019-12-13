package xyz.fluxinc.auctionhouse.controllers;

import xyz.fluxinc.auctionhouse.entries.auction.Auction;
import xyz.fluxinc.auctionhouse.entries.auction.Bid;
import xyz.fluxinc.auctionhouse.entries.auctionhouse.AuctionHouse;
import xyz.fluxinc.auctionhouse.entries.auctionhouse.AuctionHouseLock;
import xyz.fluxinc.auctionhouse.entries.notifications.Notification;
import xyz.fluxinc.auctionhouse.entries.notifications.NotificationType;
import xyz.fluxinc.auctionhouse.exceptions.auction.AuctionNotFoundException;
import xyz.fluxinc.auctionhouse.exceptions.authentication.AuthenticationException;
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

    private List<Notification> notifications;

    public AuctionHouseController(SpaceController spaceController, AuthenticationController authenticationController) throws SpaceException {
        this.spaceController = spaceController;
        this.authenticationController = authenticationController;
        setupAuctionHouse();
    }

    private AuctionHouse setupAuctionHouse() throws SpaceException {
        AuctionHouse house = null;
        if (isHouseLocked()) { house = spaceController.read(new AuctionHouse(), AUCTION_HOUSE_LOOKUP_TIMEOUT); }
        if (house == null) {
            spaceController.put(new AuctionHouse(0), AUCTION_HOUSE_VALIDITY_PERIOD);
            return (AuctionHouse) spaceController.read(new AuctionHouse());
        }
        return house;
    }

    public AuctionHouse getAuctionHouse() throws SpaceException {
        AuctionHouse house = spaceController.read(new AuctionHouse());
        if (house == null) { return setupAuctionHouse(); }
        return (AuctionHouse) spaceController.read(new AuctionHouse());
    }

    private AuctionHouse takeAuctionHouse() throws SpaceException {
        AuctionHouse house = spaceController.read(new AuctionHouse());
        if (house == null) { return setupAuctionHouse(); }

        return (AuctionHouse) spaceController.take(new AuctionHouse());
    }
    private boolean isHouseLocked() throws SpaceException {
        return spaceController.read(new AuctionHouseLock()) == null;
    }

    public Auction placeAuction(String name, double buyItNowPrice) throws SpaceException, AuthenticationException {
        return placeAuction(name, buyItNowPrice, 1);
    }

    public Auction placeAuction(String name, double buyItNowPrice, double currentBid) throws SpaceException, AuthenticationException {
        if (!authenticationController.isLoggedIn())  { throw new AuthenticationException("You must be logged in to perform this action"); }
        byte[] key = new byte[7];
        new Random().nextBytes(key);
        AuctionHouseLock lock = new AuctionHouseLock(key);
        spaceController.put(lock, SpaceController.ONE_MINUTE);
        AuctionHouse auctionHouse = takeAuctionHouse();

        Auction auction = new Auction(auctionHouse.currentCount, authenticationController.getUsername(), name, buyItNowPrice, currentBid);
        auctionHouse.addAuction();

        spaceController.put(auction, AUCTION_VALIDITY_PERIOD);
        spaceController.put(auctionHouse, AUCTION_HOUSE_VALIDITY_PERIOD);
        spaceController.take(lock, SpaceController.ONE_MINUTE);

        return auction;
    }

    public Auction readAuction(int auctionId) throws SpaceException {
        Auction auction = new Auction();
        auction.auctionId = auctionId;
        return spaceController.read(auction);
    }

    public List<Auction> readAllAuctions() throws SpaceException {
        return spaceController.readAll(new Auction(), 1000);
    }

    public List<Bid> getBids(int auctionId) throws SpaceException, AuctionNotFoundException {
        Auction templateAuction = new Auction();
        templateAuction.auctionId = auctionId;
        Auction auction = spaceController.read(templateAuction);
        if (auction == null) { throw new AuctionNotFoundException("An Auction with the ID " + auctionId + " was not found within the system"); }
        return spaceController.readAll(new Bid(auctionId), auction.bidCount);
    }

    public Bid placeBid(int auctionId, double amount) throws SpaceException, AuctionNotFoundException, AuthenticationException {
        if (!authenticationController.isLoggedIn())  { throw new AuthenticationException("You must be logged in to perform this action"); }
        Auction template = new Auction();
        template.auctionId = auctionId;
        Auction auction = spaceController.take(template);
        if (auction == null) { throw new AuctionNotFoundException("An Auction with the ID " + auctionId + " was not found within the system"); }
        Bid bid = new Bid(auctionId, authenticationController.getUsername(), amount);
        spaceController.put(bid, BID_VALIDITY_PERIOD);
        auction.addBid();
        spaceController.put(auction, AUCTION_VALIDITY_PERIOD);
        return bid;
    }

    public List<Notification> getNotifications() { return notifications; }

    public void addNotification(Bid bid, NotificationType type) { notifications.add(new Notification(type, bid)); }

    public void addNotification(Auction auction, NotificationType type) { notifications.add(new Notification(type, auction)); }
}
