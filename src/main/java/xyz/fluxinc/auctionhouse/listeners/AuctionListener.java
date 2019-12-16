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
import xyz.fluxinc.auctionhouse.entries.auction.AuctionU1755082;
import xyz.fluxinc.auctionhouse.entries.auction.Bid1755082;
import xyz.fluxinc.auctionhouse.entries.notifications.NotificationType;
import xyz.fluxinc.auctionhouse.exceptions.auction.AuctionNotFoundException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.List;

public class AuctionListener implements RemoteEventListener {

   private AuctionHouseController auctionHouseController;
   private AuctionU1755082 template;
   private RemoteEventListener stub;

    public AuctionListener(SpaceController spaceController, AuctionHouseController auctionHouseController, AuctionU1755082 template) throws SpaceException, ExportException {
        this.auctionHouseController = auctionHouseController;
        this.template = template;

        Exporter defaultExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory(), false, true);
        this.stub = (RemoteEventListener) defaultExporter.export(this);
        spaceController.notify(this.stub, template);

    }

    public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {
        try {
            AuctionU1755082 auction = auctionHouseController.readAuction(template.auctionId);
            if (auction.isClosed) {
                boolean hasBid = false;
                double highestBid = 0;
                boolean hasHighestBid = false;
                List<Bid1755082> bids = auctionHouseController.getBids(auction.auctionId);
                for (Bid1755082 bid : bids) {
                    if (bid.username.equals(auctionHouseController.getCurrentUser().username)) { hasBid = true; }
                    if (bid.bidAmount > highestBid) { highestBid = bid.bidAmount; }
                    hasHighestBid = bid.bidAmount == highestBid && bid.username.equals(auctionHouseController.getCurrentUser().username);
                }
                if (hasHighestBid) {
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
