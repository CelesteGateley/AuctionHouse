package xyz.fluxinc.auctionhouse.ui.views.auction;

import xyz.fluxinc.auctionhouse.controllers.AuctionHouseController;
import xyz.fluxinc.auctionhouse.controllers.AuthenticationController;
import xyz.fluxinc.auctionhouse.entries.auction.Auction1755082;
import xyz.fluxinc.auctionhouse.entries.auction.AuctionStatus;
import xyz.fluxinc.auctionhouse.entries.auction.Bid1755082;
import xyz.fluxinc.auctionhouse.entries.authentication.User1755082;
import xyz.fluxinc.auctionhouse.exceptions.auction.AuctionNotFoundException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;
import xyz.fluxinc.auctionhouse.ui.actions.AuctionAction;
import xyz.fluxinc.auctionhouse.ui.views.Screen;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class AuctionScreen extends Screen {

    private AuthenticationController authenticationController;
    private AuctionHouseController auctionHouseController;
    private Auction1755082 auction;
    private AuctionAction auctionAction;

    public AuctionScreen(Auction1755082 auction, AuthenticationController authenticationController, AuctionHouseController auctionHouseController, AuctionAction auctionAction) {
        this.auction = auction;
        this.auctionAction = auctionAction;
        this.authenticationController = authenticationController;
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
            e.printStackTrace();
            return;
        }

        for (Bid1755082 bid : bids) {
            defaultListModel.addElement(bid);
        }

        JList<Bid1755082> list = new JList<>(defaultListModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);

        getPanel().setLayout(new GridLayout(2, 1));
        JPanel subPanel = new JPanel(new GridLayout(2, 1));
        JPanel topPanel = new JPanel(new GridLayout(2, 2));
        JPanel buttonPanel = new JPanel();
        User1755082 owner = null;
        try { owner = authenticationController.getUser(auction.ownerName); } catch (SpaceException ignored) {}
        topPanel.add(new JLabel(auction.auctionName));
        JLabel ownerLabel = new JLabel();
        ownerLabel.setText("<html><body>Owner: " + auction.ownerName + "<br>Contact: " + owner.contactInfo + "</body></html>");
        topPanel.add(ownerLabel);
        topPanel.add(new JLabel("Buy it now: " + auction.buyItNowPrice));
        if (!bids.isEmpty()) {
            topPanel.add(new JLabel("Current Highest Bid: " + bids.get(0).bidAmount));
        } else {
            topPanel.add(new JLabel("Minimum Bid: " + auction.minimumBid));
        }

        if (auction.status == AuctionStatus.BID_ACCEPTED || auction.status == AuctionStatus.BOUGHT || auction.status == AuctionStatus.CLOSED) {
            buttonPanel.setLayout(new GridLayout(1, 2));
            buttonPanel.add(new JLabel());
            if (auction.status == AuctionStatus.CLOSED) {
                buttonPanel.add(new JLabel("Auction Closed by Owner"));
            } else {
                buttonPanel.add(new JLabel("Purchased by " + auction.purchasedBy));
            }
        } else if (auction.ownerName.equals(auctionHouseController.getCurrentUser().username)) {
            buttonPanel.setLayout(new GridLayout(1, 3));
            JButton buyItNowButton = new JButton("Set Buy It Now Price");
            buyItNowButton.setActionCommand("set-buy-it-now");
            buyItNowButton.addActionListener(auctionAction);
            buttonPanel.add(buyItNowButton);

            JButton closeAuction = new JButton("Close Auction");
            closeAuction.setActionCommand("close-auction");
            closeAuction.addActionListener(auctionAction);
            buttonPanel.add(closeAuction);

            JButton bidButton = new JButton("Accept Bid");
            bidButton.setActionCommand("accept-bid");
            bidButton.addActionListener(auctionAction);
            if (bids.isEmpty()) {
                bidButton.setEnabled(false);
            }
            buttonPanel.add(bidButton);
        } else if (auction.status == AuctionStatus.OPEN) {
            buttonPanel.setLayout(new GridLayout(1,2));
            JButton bidButton = new JButton("Place Bid");
            bidButton.setActionCommand("place-bid");
            bidButton.addActionListener(auctionAction);
            buttonPanel.add(bidButton);

            JButton buyItNowButton = new JButton("Buy it Now!");
            buyItNowButton.setActionCommand("buy-it-now");
            buyItNowButton.addActionListener(auctionAction);
            buttonPanel.add(buyItNowButton);
        }
        subPanel.add(topPanel);
        subPanel.add(buttonPanel);
        getPanel().add(subPanel);
        getPanel().add(scrollPane);
    }
}
