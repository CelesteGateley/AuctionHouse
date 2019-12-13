package xyz.fluxinc.auctionhouse.listeners;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import xyz.fluxinc.auctionhouse.controllers.SystemController;
import xyz.fluxinc.auctionhouse.entries.auction.Auction;
import xyz.fluxinc.auctionhouse.entries.auction.Bid;
import xyz.fluxinc.auctionhouse.entries.notifications.NotificationType;
import xyz.fluxinc.auctionhouse.exceptions.auction.AuctionNotFoundException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.List;

public class AuctionListener implements RemoteEventListener {

   private SystemController systemController;
   private Auction template;
   private RemoteEventListener stub;

    public AuctionListener(SystemController systemController, Auction template) throws SpaceException, ExportException {
        this.systemController = systemController;
        this.template = template;

        Exporter defaultExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory(), false, true);
        this.stub = (RemoteEventListener) defaultExporter.export(this);
        systemController.getSpaceController().notify(this.stub, template);

    }

    public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {
        try {
            Auction auction = systemController.getAuctionHouseController().readAuction(template.auctionId);
            if (auction.isClosed) {
                boolean hasBid = false;
                double highestBid = 0;
                boolean hasHighestBid = false;
                List<Bid> bids = systemController.getAuctionHouseController().getBids(auction.auctionId);
                for (Bid bid : bids) {
                    if (bid.username.equals(systemController.getAuthenticationController().getUsername())) { hasBid = true; }
                    if (bid.bidAmount > highestBid) { highestBid = bid.bidAmount; }
                    hasHighestBid = bid.bidAmount == highestBid && bid.username.equals(systemController.getAuthenticationController().getUsername());
                }
                if (hasHighestBid) {
                    systemController.getAuctionHouseController().addNotification(auction, NotificationType.AUCTION_WON);
                } else if (hasBid) {
                    systemController.getAuctionHouseController().addNotification(auction, NotificationType.AUCTION_LOST);
                } else {
                    systemController.getAuctionHouseController().addNotification(auction, NotificationType.AUCTION_CLOSED);
                }

            } else {
                systemController.getAuctionHouseController().addNotification(auction, NotificationType.AUCTION_ADDED);
            }

        } catch (SpaceException | AuctionNotFoundException e) {
            e.printStackTrace();
        }

    }
}
