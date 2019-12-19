package xyz.fluxinc.auctionhouse.ui.views.auction;

import xyz.fluxinc.auctionhouse.controllers.AuctionHouseController;
import xyz.fluxinc.auctionhouse.entries.auction.Auction1755082;
import xyz.fluxinc.auctionhouse.entries.auction.AuctionStatus;
import xyz.fluxinc.auctionhouse.entries.auction.Bid1755082;
import xyz.fluxinc.auctionhouse.exceptions.auction.AuctionNotFoundException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;
import xyz.fluxinc.auctionhouse.ui.actions.AuctionAction;
import xyz.fluxinc.auctionhouse.ui.views.Screen;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AuctionScreen extends Screen {

    private AuctionHouseController auctionHouseController;
    private Auction1755082 auction;
    private AuctionAction auctionAction;

    public AuctionScreen(Auction1755082 auction, AuctionHouseController auctionHouseController, AuctionAction auctionAction) {
        this.auction = auction;
        this.auctionAction = auctionAction;
        this.auctionHouseController = auctionHouseController;
    }

    @Override
    public void initialize() {
        getPanel().removeAll();
        DefaultListModel<Bid1755082> defaultListModel = new DefaultListModel<>();
        List<Bid1755082> bids;
        try {
            bids = auctionHouseController.getBids(auction.auctionId);
        } catch (SpaceException | AuctionNotFoundException e) {
            e.printStackTrace(); return;
        }

        for (Bid1755082 bid : bids) {
            defaultListModel.addElement(bid);
        }

        JList<Bid1755082> list = new JList<>(defaultListModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);

        getPanel().setLayout(new GridLayout(2, 1));
        JPanel subPanel = new JPanel(new GridLayout(3, 2));

        subPanel.add(new JLabel(auction.name));
        subPanel.add(new JLabel(auction.ownerName));
        subPanel.add(new JLabel("Buy it now: " + auction.buyItNowPrice));
        if (!bids.isEmpty()) {
            String message = auction.minimumBid < bids.get(0).bidAmount ? "" + auction.minimumBid : "" + bids.get(0).bidAmount;
            subPanel.add(new JLabel("Current Highest Bid: " + message));
        } else {
            subPanel.add(new JLabel("No Bids Have Been Placed"));
        }

        if (auction.status == AuctionStatus.BID_ACCEPTED || auction.status == AuctionStatus.BOUGHT) {
            subPanel.add(new JLabel());
            subPanel.add(new JLabel("Purchased by " + auction.purchasedBy));
        } else if (auction.ownerName.equals(auctionHouseController.getCurrentUser().username)){
            JButton buyItNowButton = new JButton("Set Buy It Now Price!");
            buyItNowButton.setActionCommand("set-buy-it-now");
            buyItNowButton.addActionListener(auctionAction);
            subPanel.add(buyItNowButton);

            JButton bidButton = new JButton("Accept Bid");
            bidButton.setActionCommand("accept-bid");
            bidButton.addActionListener(auctionAction);
            if (bids.isEmpty()) { bidButton.setEnabled(false); }
            subPanel.add(bidButton);
        } else if (auction.status == AuctionStatus.OPEN) {
            JButton bidButton = new JButton("Place Bid");
            bidButton.setActionCommand("place-bid");
            bidButton.addActionListener(auctionAction);
            subPanel.add(bidButton);

            JButton buyItNowButton = new JButton("Buy it Now!");
            buyItNowButton.setActionCommand("buy-it-now");
            buyItNowButton.addActionListener(auctionAction);
            subPanel.add(buyItNowButton);
        }
        getPanel().add(subPanel);
        getPanel().add(scrollPane);
    }
}
