package xyz.fluxinc.auctionhouse.ui.views.auction;

import xyz.fluxinc.auctionhouse.controllers.AuctionHouseController;
import xyz.fluxinc.auctionhouse.controllers.UserInterfaceController;
import xyz.fluxinc.auctionhouse.entries.auction.AuctionU1755082;
import xyz.fluxinc.auctionhouse.exceptions.auction.AuctionNotFoundException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;
import xyz.fluxinc.auctionhouse.ui.views.Screen;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;

@SuppressWarnings("unchecked")
public class AllAuctionsScreen extends Screen implements ListSelectionListener {

    private AuctionHouseController auctionHouseController;
    private UserInterfaceController userInterfaceController;

    private JList<AuctionU1755082> list;

    public AllAuctionsScreen(AuctionHouseController auctionHouseController, UserInterfaceController userInterfaceController) throws SpaceException {
        this.auctionHouseController = auctionHouseController;
        this.userInterfaceController = userInterfaceController;
        initialize();
    }

    private void initialize() throws SpaceException {
        DefaultListModel defaultListModel = new DefaultListModel();
        List<AuctionU1755082> auctions = auctionHouseController.getActiveAuctions();
        for (AuctionU1755082 auction : auctions) { defaultListModel.addElement(auction); }

        list = new JList(defaultListModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);
        JScrollPane scrollPane = new JScrollPane(list);
        getPanel().setLayout(new GridLayout(1,1));
        getPanel().add(scrollPane);
    }


    @Override
    public void valueChanged(ListSelectionEvent e) {
        try {
            userInterfaceController.showAuction(list.getSelectedValue());
        } catch (AuctionNotFoundException | SpaceException ex) {
            ex.printStackTrace();
        }
    }
}
