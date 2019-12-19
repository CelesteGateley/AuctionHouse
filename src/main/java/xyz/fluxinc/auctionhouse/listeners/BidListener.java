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

public class BidListener implements RemoteEventListener {

    private AuctionHouseController auctionHouseController;
   private Bid1755082 template;

    public BidListener(SpaceController spaceController, AuctionHouseController auctionHouseController, Bid1755082 template) throws SpaceException, ExportException {
        this.auctionHouseController = auctionHouseController;
        this.template = template;

        Exporter defaultExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory(), false, true);
        RemoteEventListener stub = (RemoteEventListener) defaultExporter.export(this);
        spaceController.notify(stub, template);

        System.out.println("Started Listening For Bids on " + template.auctionId);

    }

    public void notify(RemoteEvent remoteEvent) throws RemoteException {
        System.out.println("Bid Placed!");
        try {
            List<Bid1755082> bids = auctionHouseController.getBids(template.auctionId);
            Bid1755082 bid = bids.get(0);
            Auction1755082 auction = auctionHouseController.getAuction(bid.auctionId);
            if (auction.ownerName.equals(auctionHouseController.getCurrentUser().username)) {
                auctionHouseController.addNotification(bid, NotificationType.BID_PLACED_OWNED);
            } else if (!bid.placedBy.equals(auctionHouseController.getCurrentUser().username)) {
                auctionHouseController.addNotification(bid, NotificationType.BID_PLACED_WATCHED);
            } else if (bid.placedBy.equals(auctionHouseController.getCurrentUser().username) && bid.isAccepted) {
                auctionHouseController.addNotification(bid, NotificationType.BID_ACCEPTED);
            }
        } catch (SpaceException | AuctionNotFoundException e) {
            e.printStackTrace();
        }

    }
}
