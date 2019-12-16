package xyz.fluxinc.auctionhouse.controllers;

import xyz.fluxinc.auctionhouse.entries.auction.AuctionU1755082;
import xyz.fluxinc.auctionhouse.entries.auction.Bid1755082;
import xyz.fluxinc.auctionhouse.entries.auctionhouse.AuctionHouse1755082;
import xyz.fluxinc.auctionhouse.entries.auctionhouse.AuctionHouseLock1755082;
import xyz.fluxinc.auctionhouse.entries.authentication.User1755082;
import xyz.fluxinc.auctionhouse.entries.notifications.Notification;
import xyz.fluxinc.auctionhouse.entries.notifications.NotificationType;
import xyz.fluxinc.auctionhouse.exceptions.auction.AuctionNotFoundException;
import xyz.fluxinc.auctionhouse.exceptions.authentication.AuthenticationException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;
import xyz.fluxinc.auctionhouse.listeners.AuctionListener;
import xyz.fluxinc.auctionhouse.listeners.BidListener;

import java.rmi.server.ExportException;
import java.util.*;

@SuppressWarnings({"unchecked", "MismatchedQueryAndUpdateOfCollection"})
public class AuctionHouseController {

    private static final long AUCTION_HOUSE_VALIDITY_PERIOD = SpaceController.ONE_DAY;
    private static final long AUCTION_HOUSE_LOOKUP_TIMEOUT = SpaceController.ONE_SECOND * 10;
    private static final long AUCTION_VALIDITY_PERIOD = SpaceController.ONE_DAY;
    private static final long BID_VALIDITY_PERIOD = SpaceController.ONE_DAY;

    private AuthenticationController authenticationController;
    private SpaceController spaceController;

    private AuctionListener auctionListener;
    private BidListener bidListener;

    private List<Notification> notifications;
    private List<AuctionListener> auctionListeners;
    private List<BidListener> bidListeners;

    public AuctionHouseController(SpaceController spaceController, AuthenticationController authenticationController) throws SpaceException, ExportException {
        this.spaceController = spaceController;
        this.authenticationController = authenticationController;
        this.auctionListeners = new ArrayList<>();
        this.bidListeners = new ArrayList<>();
        setupAuctionHouse();
        notifications = new ArrayList<>();
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

    public AuctionU1755082 buyAuction(int auctionId) throws SpaceException {
        AuctionU1755082 auction = spaceController.take(new AuctionU1755082(auctionId));
        auction.isClosed = true;
        //auction.purchasedBy = authenticationController.getUsername();

        spaceController.put(auction);
        return auction;
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

    public AuctionU1755082 placeAuction(String name, double buyItNowPrice) throws SpaceException, AuthenticationException {
        return placeAuction(name, buyItNowPrice, 1);
    }

    public List<AuctionU1755082> getAllAuctions() throws SpaceException {
        return spaceController.readAll(new AuctionU1755082(), 200);
    }

    public List<AuctionU1755082> getActiveAuctions() throws SpaceException {
        AuctionU1755082 template = new AuctionU1755082();
        template.isClosed = false;
        return spaceController.readAll(template, 200);
    }

    public List<AuctionU1755082> getAuctionsByUser(String username) throws SpaceException {
        AuctionU1755082 template = new AuctionU1755082();
        template.ownerName = username;
        return spaceController.readAll(template, 200);
    }

    public AuctionU1755082 placeAuction(String name, double buyItNowPrice, double currentBid) throws AuthenticationException, SpaceException {
        if (!authenticationController.isLoggedIn())  { throw new AuthenticationException("You must be logged in to perform this action"); }
        return placeAuction(name, buyItNowPrice, currentBid, authenticationController.getUsername());
    }

    public AuctionU1755082 placeAuction(String name, double buyItNowPrice, double currentBid, String username) throws SpaceException {
        byte[] key = new byte[7];
        new Random().nextBytes(key);
        AuctionHouseLock1755082 lock = new AuctionHouseLock1755082(key);
        spaceController.put(lock, SpaceController.ONE_MINUTE);
        AuctionHouse1755082 auctionHouse = takeAuctionHouse();

        AuctionU1755082 auction = new AuctionU1755082(auctionHouse.currentCount, authenticationController.getUsername(), name, buyItNowPrice, currentBid);
        auctionHouse.addAuction();

        spaceController.put(auction, AUCTION_VALIDITY_PERIOD);
        spaceController.put(auctionHouse, AUCTION_HOUSE_VALIDITY_PERIOD);
        spaceController.take(lock, SpaceController.ONE_MINUTE);

        return auction;
    }

    public AuctionU1755082 readAuction(int auctionId) throws SpaceException {
        AuctionU1755082 auction = new AuctionU1755082();
        auction.auctionId = auctionId;
        return spaceController.read(auction);
    }

    public List<AuctionU1755082> readAllAuctions() throws SpaceException {
        return spaceController.readAll(new AuctionU1755082(), 1000);
    }

    public List<Bid1755082> getBids(int auctionId) throws SpaceException, AuctionNotFoundException {
        AuctionU1755082 templateAuction = new AuctionU1755082();
        templateAuction.auctionId = auctionId;
        AuctionU1755082 auction = spaceController.read(templateAuction);
        if (auction == null) {
            throw new AuctionNotFoundException("An Auction with the ID " + auctionId + " was not found within the system");
        }
        List<Bid1755082> bids = spaceController.readAll(new Bid1755082(auctionId), auction.bidCount);
        Collections.sort(bids);
        return bids;
    }

    public Bid1755082 placeBid(int auctionId, double amount) throws AuthenticationException, AuctionNotFoundException, SpaceException {
        if (!authenticationController.isLoggedIn())  {
            throw new AuthenticationException("You must be logged in to perform this action");
        }
        return placeBid(auctionId, amount, authenticationController.getUsername());
    }
    public Bid1755082 placeBid(int auctionId, double amount, String username) throws SpaceException, AuctionNotFoundException {

        AuctionU1755082 template = new AuctionU1755082();
        template.auctionId = auctionId;
        AuctionU1755082 auction = spaceController.take(template);
        if (auction == null) {
            throw new AuctionNotFoundException("An Auction with the ID " + auctionId + " was not found within the system");
        }
        Bid1755082 bid = new Bid1755082(auctionId, authenticationController.getUsername(), amount);
        spaceController.put(bid, BID_VALIDITY_PERIOD);
        auction.addBid();
        spaceController.put(auction, AUCTION_VALIDITY_PERIOD);
        return bid;
    }

    public void watchAuction(int auctionId) throws ExportException, SpaceException {
        auctionListeners.add(new AuctionListener(spaceController, this, new AuctionU1755082(auctionId)));
        bidListeners.add(new BidListener(spaceController, this, new Bid1755082(auctionId)));
    }

    public List<Notification> getNotifications() { return notifications; }

    public void addNotification(Bid1755082 bid, NotificationType type) {
        notifications.add(new Notification(type, bid));
    }

    public void addNotification(AuctionU1755082 auction, NotificationType type) { notifications.add(new Notification(type, auction)); }

    public User1755082 getCurrentUser() {
        return authenticationController.getUser();
    }

    public Bid1755082 getHighestBid(int auctionId) throws AuctionNotFoundException, SpaceException {
        List<Bid1755082> bids = getBids(auctionId);
        return null;
    }

}
