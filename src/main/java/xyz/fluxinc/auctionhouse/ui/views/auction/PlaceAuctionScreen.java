package xyz.fluxinc.auctionhouse.ui.views.auction;

import xyz.fluxinc.auctionhouse.controllers.AuctionHouseController;
import xyz.fluxinc.auctionhouse.controllers.UserInterfaceController;
import xyz.fluxinc.auctionhouse.exceptions.authentication.AuthenticationException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;
import xyz.fluxinc.auctionhouse.ui.views.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlaceAuctionScreen extends Screen implements ActionListener {

    private UserInterfaceController userInterfaceController;
    private AuctionHouseController auctionHouseController;
    private JTextField descriptionField;
    private JTextField buyItNowField;
    private JTextField bidField;

    public PlaceAuctionScreen(AuctionHouseController auctionHouseController, UserInterfaceController userInterfaceController) {
        this.auctionHouseController = auctionHouseController;
        this.userInterfaceController = userInterfaceController;
    }

    @Override
    public void initialize() {
        getPanel().removeAll();
        getPanel().setLayout(new GridLayout(2,1));
        JPanel subPanel = new JPanel(new GridLayout(3, 2));

        subPanel.add(new JLabel("Auction Description"));
        descriptionField = new JTextField();
        subPanel.add(descriptionField);

        subPanel.add(new JLabel("Buy it Now Price"));
        buyItNowField = new JTextField();
        subPanel.add(buyItNowField);

        subPanel.add(new JLabel("Minimum Bid"));
        bidField = new JTextField();
        subPanel.add(bidField);

        getPanel().add(subPanel);

        JButton submit = new JButton("Submit");
        submit.addActionListener(this);
        getPanel().add(submit);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (descriptionField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Auctions must have a description");
        }
        if (buyItNowField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Auctions must have a Buy It Now Price");
        }

        Double minBid;
        Double buyItNow;
        try {
            buyItNow = Double.parseDouble(buyItNowField.getText());
            minBid = bidField.getText().isEmpty() ? 1.0 : Double.parseDouble(bidField.getText());
        } catch (NumberFormatException err) {
            JOptionPane.showMessageDialog(null, "Buy it Now and Current Bid must be numbers");
            return;
        }

        try {
            auctionHouseController.placeAuction(descriptionField.getText(), buyItNow, minBid);
            userInterfaceController.showAuctions();
        } catch (AuthenticationException | SpaceException ex) {
            ex.printStackTrace();
        }
    }
}
