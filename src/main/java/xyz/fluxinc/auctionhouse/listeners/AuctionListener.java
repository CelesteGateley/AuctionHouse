package xyz.fluxinc.auctionhouse.listeners;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import xyz.fluxinc.auctionhouse.controllers.AuctionHouseController;
import xyz.fluxinc.auctionhouse.controllers.SpaceController;
import xyz.fluxinc.auctionhouse.entries.auction.Auction1755082;
import xyz.fluxinc.auctionhouse.entries.auction.Bid1755082;
import xyz.fluxinc.auctionhouse.entries.notifications.NotificationType;
import xyz.fluxinc.auctionhouse.exceptions.auction.AuctionNotFoundException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.List;

import static xyz.fluxinc.auctionhouse.entries.auction.AuctionStatus.BID_ACCEPTED;

public class AuctionListener implements RemoteEventListener {

   private AuctionHouseController auctionHouseController;
   private Auction1755082 template;

    public AuctionListener(SpaceController spaceController, AuctionHouseController auctionHouseController, Auction1755082 template) throws SpaceException, ExportException {
        this.auctionHouseController = auctionHouseController;
        this.template = template;

        Exporter defaultExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory(), false, true);
        RemoteEventListener stub = (RemoteEventListener) defaultExporter.export(this);
        spaceController.notify(stub, template);

        System.out.println("Started Watching Auction " + template.auctionId);

    }

    public void notify(RemoteEvent remoteEvent) throws RemoteException {
        System.out.println("Notification Received!");
        try {
            Auction1755082 auction = auctionHouseController.getAuction(template.auctionId);
            if (auction.status == BID_ACCEPTED) {
                boolean hasBid = false;
                double highestBid = 0;
                List<Bid1755082> bids = auctionHouseController.getBids(auction.auctionId);
                for (Bid1755082 bid : bids) {
                    if (bid.placedBy.equals(auctionHouseController.getCurrentUser().username)) { hasBid = true; }
                    if (bid.bidAmount > highestBid) { highestBid = bid.bidAmount; }
                }
                if (auction.purchasedBy.equals(auctionHouseController.getCurrentUser().username)) {
                    auctionHouseController.addNotification(auction, NotificationType.AUCTION_WON);
                } else if (hasBid) {
                    auctionHouseController.addNotification(auction, NotificationType.AUCTION_LOST);
                } else {
                    auctionHouseController.addNotification(auction, NotificationType.AUCTION_CLOSED);
                }

            } else {
                auctionHouseController.addNotification(auction, NotificationType.AUCTION_ADDED);
            }

        } catch (SpaceException | AuctionNotFoundException e) {
            e.printStackTrace();
        }

    }
}
