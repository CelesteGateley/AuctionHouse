package xyz.fluxinc.auctionhouse.listeners;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import xyz.fluxinc.auctionhouse.controllers.AuctionHouseController;
import xyz.fluxinc.auctionhouse.controllers.SpaceController;
import xyz.fluxinc.auctionhouse.controllers.SystemController;
import xyz.fluxinc.auctionhouse.entries.auction.Auction;
import xyz.fluxinc.auctionhouse.entries.auction.Bid;
import xyz.fluxinc.auctionhouse.entries.notifications.NotificationType;
import xyz.fluxinc.auctionhouse.exceptions.auction.AuctionNotFoundException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;

import java.rmi.RemoteException;
import java.util.List;

public class BidListener implements RemoteEventListener {

    private SpaceController spaceController;
    private AuctionHouseController auctionHouseController;
   private Bid template;

    public BidListener(SpaceController spaceController, AuctionHouseController auctionHouseController, Bid template) throws SpaceException {
        this.spaceController = spaceController;
        this.auctionHouseController = auctionHouseController;
        this.template = template;

        Exporter defaultExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory(), false, true);

        spaceController.notify(this, template);

    }

    public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {
        try {
            List<Bid> bids = auctionHouseController.getBids(template.auctionId);
            Bid bid = bids.get(bids.size()-1);
            Auction auction = auctionHouseController.readAuction(bid.auctionId);
            if (auction.ownerName.equals(auctionHouseController.getCurrentUser().username)) {
                auctionHouseController.addNotification(bid, NotificationType.BID_PLACED_OWNED);
            } else if (!bid.username.equals(auctionHouseController.getCurrentUser().username)) {
                auctionHouseController.addNotification(bid, NotificationType.BID_PLACED_WATCHED);
            } else if (bid.username.equals(auctionHouseController.getCurrentUser().username) && bid.isAccepted) {
                auctionHouseController.addNotification(bid, NotificationType.BID_ACCEPTED);
            }
        } catch (SpaceException | AuctionNotFoundException e) {
            e.printStackTrace();
        }

    }
}
