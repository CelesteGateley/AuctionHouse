package xyz.fluxinc.auctionhouse.controllers;

import xyz.fluxinc.auctionhouse.entries.auction.Auction;
import xyz.fluxinc.auctionhouse.entries.auction.Bid;
import xyz.fluxinc.auctionhouse.entries.auctionhouse.AuctionHouse;
import xyz.fluxinc.auctionhouse.entries.auctionhouse.AuctionHouseLock;
import xyz.fluxinc.auctionhouse.entries.authentication.User;
import xyz.fluxinc.auctionhouse.entries.notifications.Notification;
import xyz.fluxinc.auctionhouse.entries.notifications.NotificationType;
import xyz.fluxinc.auctionhouse.exceptions.auction.AuctionNotFoundException;
import xyz.fluxinc.auctionhouse.exceptions.auction.BidTooLowException;
import xyz.fluxinc.auctionhouse.exceptions.authentication.AuthenticationException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;
import xyz.fluxinc.auctionhouse.listeners.AuctionListener;
import xyz.fluxinc.auctionhouse.listeners.BidListener;

import java.rmi.server.ExportException;
import java.util.*;

import static xyz.fluxinc.auctionhouse.entries.auction.AuctionStatus.*;

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

    private AuctionHouse setupAuctionHouse() throws SpaceException {
        AuctionHouse house = null;
        if (isHouseLocked()) { house = spaceController.read(new AuctionHouse(), AUCTION_HOUSE_LOOKUP_TIMEOUT); }
        if (house == null) {
            spaceController.put(new AuctionHouse(0), AUCTION_HOUSE_VALIDITY_PERIOD);
            return (AuctionHouse) spaceController.read(new AuctionHouse());
        }
        return house;
    }

    public Auction buyAuction(int auctionId) throws SpaceException {
        Auction auction = spaceController.take(new Auction(auctionId));
        auction.buyItNow();
        auction.purchasedBy = authenticationController.getUsername();

        spaceController.put(auction);
        return auction;
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

    public List<Auction> getAllAuctions() throws SpaceException {
        List<Auction> auctions = spaceController.readAll(new Auction(), 1000);
        Collections.sort(auctions);
        return auctions;
    }

    public List<Auction> getActiveAuctions() throws SpaceException {
        Auction template = new Auction();
        template.status = OPEN;
        return spaceController.readAll(template, 200);
    }

    public List<Auction> getAuctionsByUser(String username) throws SpaceException {
        Auction template = new Auction();
        template.ownerName = username;
        return spaceController.readAll(template, 200);
    }

    public Auction placeAuction(String name, double buyItNowPrice, double currentBid) throws AuthenticationException, SpaceException {
        if (!authenticationController.isLoggedIn())  { throw new AuthenticationException("You must be logged in to perform this action"); }
        return placeAuction(name, buyItNowPrice, currentBid, authenticationController.getUsername());
    }

    public Auction placeAuction(String name, double buyItNowPrice, double currentBid, String username) throws SpaceException {
        byte[] key = new byte[7];
        new Random().nextBytes(key);
        AuctionHouseLock lock = new AuctionHouseLock(key);
        spaceController.put(lock, SpaceController.ONE_MINUTE);
        AuctionHouse auctionHouse = takeAuctionHouse();

        Auction auction = new Auction(auctionHouse.currentCount, username, name, buyItNowPrice, currentBid);

        User user = spaceController.take(new User(username));
        if (user != null) { user.watchedLots.add(auctionHouse.currentCount); }

        auctionHouse.addAuction();

        spaceController.put(auction, AUCTION_VALIDITY_PERIOD);
        spaceController.put(auctionHouse, AUCTION_HOUSE_VALIDITY_PERIOD);
        spaceController.put(user, SpaceController.ONE_DAY*7);
        spaceController.take(lock, SpaceController.ONE_SECOND*5);

        return auction;
    }

    public Auction readAuction(int auctionId) throws SpaceException {
        Auction auction = new Auction();
        auction.auctionId = auctionId;
        return spaceController.read(auction);
    }

    public List<Bid> getBids(int auctionId) throws SpaceException, AuctionNotFoundException {
        Auction templateAuction = new Auction();
        templateAuction.auctionId = auctionId;
        Auction auction = spaceController.read(templateAuction);
        if (auction == null) {
            throw new AuctionNotFoundException("An Auction with the ID " + auctionId + " was not found within the system");
        }
        List<Bid> bids = spaceController.readAll(new Bid(auctionId), auction.bidCount);
        Collections.sort(bids);
        return bids;
    }

    public Bid placeBid(int auctionId, double amount) throws AuthenticationException, AuctionNotFoundException, SpaceException, BidTooLowException {
        if (!authenticationController.isLoggedIn())  {
            throw new AuthenticationException("You must be logged in to perform this action");
        }
        Bid highestBid = getHighestBid(auctionId);
        if (highestBid.bidAmount >= amount) { throw new BidTooLowException("The bid placed was below the highest bid"); }
        return placeBid(auctionId, amount, authenticationController.getUsername());
    }

    public Bid placeBid(int auctionId, double amount, String username) throws SpaceException, AuctionNotFoundException, BidTooLowException {
        Auction template = new Auction();
        template.auctionId = auctionId;
        Auction auction = spaceController.take(template);
        if (auction == null) {
            throw new AuctionNotFoundException("An Auction with the ID " + auctionId + " was not found within the system");
        }

        Bid bid = new Bid(auctionId, username, amount);
        spaceController.put(bid, BID_VALIDITY_PERIOD);
        auction.addBid();
        spaceController.put(auction, AUCTION_VALIDITY_PERIOD);
        return bid;
    }

    public void watchAuction(int auctionId) throws ExportException, SpaceException {
        auctionListeners.add(new AuctionListener(spaceController, this, new Auction(auctionId)));
        bidListeners.add(new BidListener(spaceController, this, new Bid(auctionId)));
    }

    public List<Notification> getNotifications() { return notifications; }

    public void addNotification(Bid bid, NotificationType type) {
        notifications.add(new Notification(type, bid));
    }

    public void addNotification(Auction auction, NotificationType type) { notifications.add(new Notification(type, auction)); }

    public User getCurrentUser() {
        return authenticationController.getUser();
    }

    public Bid getHighestBid(int auctionId) throws AuctionNotFoundException, SpaceException {
        List<Bid> bids = getBids(auctionId);
        Collections.sort(bids);
        return bids.get(0);
    }

    public void acceptBid(int auctionId) throws SpaceException, AuctionNotFoundException {
        Bid bid = getHighestBid(auctionId);
        Auction auction = spaceController.take(new Auction(auctionId));

        spaceController.take(bid);
        bid.isAccepted = true;
        auction.status = BID_ACCEPTED;
        auction.purchasedBy = bid.username;

        spaceController.put(auction, AUCTION_VALIDITY_PERIOD);
        spaceController.put(bid, BID_VALIDITY_PERIOD);
    }

    public void buyItNow(int auctionId) throws SpaceException {
        Auction auction = spaceController.take(new Auction(auctionId));

        auction.status = BOUGHT;
        auction.purchasedBy = getCurrentUser().username;

        spaceController.put(auction, AUCTION_VALIDITY_PERIOD);
    }

}
