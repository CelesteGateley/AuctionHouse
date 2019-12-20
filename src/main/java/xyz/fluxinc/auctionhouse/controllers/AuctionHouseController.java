package xyz.fluxinc.auctionhouse.controllers;

import xyz.fluxinc.auctionhouse.entries.auction.Auction1755082;
import xyz.fluxinc.auctionhouse.entries.auction.Bid1755082;
import xyz.fluxinc.auctionhouse.entries.auctionhouse.AuctionHouse1755082;
import xyz.fluxinc.auctionhouse.entries.auctionhouse.AuctionHouseLock1755082;
import xyz.fluxinc.auctionhouse.entries.authentication.User1755082;
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

    public AuctionHouseController(SpaceController spaceController, AuthenticationController authenticationController) throws SpaceException {
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
            return spaceController.read(new AuctionHouse1755082());
        }
        return house;
    }

    private AuctionHouse1755082 takeAuctionHouse() throws SpaceException {
        AuctionHouse1755082 house = spaceController.read(new AuctionHouse1755082());
        if (house == null) { return setupAuctionHouse(); }

        return spaceController.take(new AuctionHouse1755082());
    }

    private boolean isHouseLocked() throws SpaceException { return spaceController.read(new AuctionHouseLock1755082()) == null; }

    public List<Auction1755082> getAllAuctions() throws SpaceException {
        List<Auction1755082> auctions = spaceController.readAll(new Auction1755082(), 1000);
        Collections.sort(auctions);
        return auctions;
    }

    public Auction1755082 getAuction(int auctionId) throws SpaceException {
        Auction1755082 auction = new Auction1755082();
        auction.auctionId = auctionId;
        return spaceController.read(auction);
    }

    public Auction1755082 placeAuction(String name, double buyItNowPrice, double currentBid) throws AuthenticationException, SpaceException {
        if (!authenticationController.isLoggedIn())  { throw new AuthenticationException("You must be logged in to perform this action"); }
        return placeAuction(name, buyItNowPrice, currentBid, authenticationController.getUsername());
    }

    public Auction1755082 placeAuction(String name, double buyItNowPrice, double currentBid, String username) throws SpaceException {
        byte[] key = new byte[7];
        new Random().nextBytes(key);
        AuctionHouseLock1755082 lock = new AuctionHouseLock1755082(key);
        spaceController.put(lock, SpaceController.ONE_MINUTE);
        AuctionHouse1755082 auctionHouse = takeAuctionHouse();

        Auction1755082 auction = new Auction1755082(auctionHouse.currentAuctionCounter, username, name, buyItNowPrice, currentBid);

        auctionHouse.addAuction();

        spaceController.put(auction, AUCTION_VALIDITY_PERIOD);
        spaceController.put(auctionHouse, AUCTION_HOUSE_VALIDITY_PERIOD);
        spaceController.take(lock, SpaceController.ONE_SECOND*5);

        try {
            watchAuction(auction.auctionId);
            authenticationController.addWatchedAuction(auction.auctionId);
        } catch (ExportException e) { e.printStackTrace(); }

        return auction;
    }

    public Auction1755082 buyAuction(int auctionId) throws SpaceException {
        Auction1755082 auction = spaceController.take(new Auction1755082(auctionId));
        auction.status = BOUGHT;
        auction.purchasedBy = authenticationController.getUsername();

        spaceController.put(auction);
        return auction;
    }

    public List<Bid1755082> getBids(int auctionId) throws SpaceException, AuctionNotFoundException {
        Auction1755082 templateAuction = new Auction1755082();
        templateAuction.auctionId = auctionId;
        Auction1755082 auction = spaceController.read(templateAuction);
        if (auction == null) {
            throw new AuctionNotFoundException("An Auction with the ID " + auctionId + " was not found within the system");
        }
        List<Bid1755082> bids = new ArrayList<>();
        if (auction.bidCount > 0) {
            bids = spaceController.readAll(new Bid1755082(auctionId), auction.bidCount);
        }
        Collections.sort(bids);
        return bids;
    }

    public Bid1755082 placeBid(int auctionId, double amount) throws AuthenticationException, AuctionNotFoundException, SpaceException, BidTooLowException {
        if (!authenticationController.isLoggedIn())  {
            throw new AuthenticationException("You must be logged in to perform this action");
        }
        Auction1755082 auction = getAuction(auctionId);
        Bid1755082 highestBid = getHighestBid(auctionId);
        double minBidAmount = auction.minimumBid > highestBid.bidAmount ? auction.minimumBid : highestBid.bidAmount;
        if (minBidAmount >= amount) { throw new BidTooLowException("The bid must be greater than " + minBidAmount); }
        return placeBid(auctionId, amount, authenticationController.getUsername());
    }

    public Bid1755082 placeBid(int auctionId, double amount, String username) throws SpaceException, AuctionNotFoundException {
        Auction1755082 template = new Auction1755082();
        template.auctionId = auctionId;
        Auction1755082 auction = spaceController.take(template);
        if (auction == null) {
            throw new AuctionNotFoundException("An Auction with the ID " + auctionId + " was not found within the system");
        }

        Bid1755082 bid = new Bid1755082(auctionId, username, amount);
        spaceController.put(bid, BID_VALIDITY_PERIOD);
        auction.addBid();
        spaceController.put(auction, AUCTION_VALIDITY_PERIOD);
        try {
            watchAuction(auctionId);
            authenticationController.addWatchedAuction(auctionId);
        } catch (ExportException e) { e.printStackTrace(); }
        return bid;
    }

    public Bid1755082 getHighestBid(int auctionId) throws AuctionNotFoundException, SpaceException {
        List<Bid1755082> bids = getBids(auctionId);
        Collections.sort(bids);
        if (bids.isEmpty()) return new Bid1755082(auctionId, "", 0);
        return bids.get(0);
    }

    public void acceptBid(int auctionId) throws SpaceException, AuctionNotFoundException {
        Bid1755082 bid = getHighestBid(auctionId);
        Auction1755082 auction = spaceController.take(new Auction1755082(auctionId));

        spaceController.take(bid);
        bid.isAccepted = true;
        auction.status = BID_ACCEPTED;
        auction.purchasedBy = bid.placedBy;

        spaceController.put(auction, AUCTION_VALIDITY_PERIOD);
        spaceController.put(bid, BID_VALIDITY_PERIOD);
    }

    public void watchAuction(int auctionId) throws ExportException, SpaceException {
        auctionListeners.add(new AuctionListener(spaceController, this, new Auction1755082(auctionId)));
        bidListeners.add(new BidListener(spaceController, this, new Bid1755082(auctionId)));
    }

    public List<Notification> getNotifications() { return notifications; }

    public void addNotification(Bid1755082 bid, NotificationType type) {
        try {
            notifications.add(new Notification(type, bid.auctionId, getAuction(bid.auctionId).auctionName));
        } catch (SpaceException ignored) { }
    }

    public void addNotification(Auction1755082 auction, NotificationType type) { notifications.add(new Notification(type, auction.auctionId, auction.auctionName)); }

    public User1755082 getCurrentUser() { return authenticationController.getUser(); }

    public void closeAuction(int auctionId) throws SpaceException {
        Auction1755082 auction = spaceController.take(new Auction1755082(auctionId));
        auction.status = CLOSED;
        spaceController.put(auction, AUCTION_VALIDITY_PERIOD);
    }
}
