package xyz.fluxinc.auctionhouse.ui.views.auction;

import xyz.fluxinc.auctionhouse.controllers.AuctionHouseController;
import xyz.fluxinc.auctionhouse.entries.auction.AuctionU1755082;
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
    private AuctionU1755082 auction;
    private AuctionAction auctionAction;
    private JList<Bid1755082> list;

    public AuctionScreen(AuctionU1755082 auction, AuctionHouseController auctionHouseController, AuctionAction auctionAction) throws AuctionNotFoundException, SpaceException {
        this.auction = auction;
        this.auctionHouseController = auctionHouseController;
        initialize();
    }

    public void initialize() throws AuctionNotFoundException, SpaceException {

        DefaultListModel<Bid1755082> defaultListModel = new DefaultListModel<>();
        List<Bid1755082> bids = auctionHouseController.getBids(auction.auctionId);
        for (Bid1755082 bid : bids) { defaultListModel.addElement(bid); }

        list = new JList<>(defaultListModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);

        getPanel().setLayout(new GridLayout(2,1));
        JPanel subPanel = new JPanel(new GridLayout(3, 2));

        subPanel.add(new JLabel(auction.name));
        subPanel.add(new JLabel(auction.ownerName));
        subPanel.add(new JLabel("Buy it now: " + auction.buyItNowPrice));
        String message = auction.currentPrice < bids.get(0).bidAmount ? "" + auction.currentPrice : "" + bids.get(0).bidAmount;
        subPanel.add(new JLabel("Current Highest Bid: " + message));

        JButton bidButton = new JButton("Place Bid");
        bidButton.setActionCommand("place-bid");
        bidButton.addActionListener(auctionAction);
        subPanel.add(bidButton);

        JButton buyItNowButton = new JButton("Buy it Now!");
        buyItNowButton.setActionCommand("buy-it-now");
        buyItNowButton.addActionListener(auctionAction);
        subPanel.add(buyItNowButton);

        getPanel().add(subPanel);
        getPanel().add(scrollPane);
    }
}
