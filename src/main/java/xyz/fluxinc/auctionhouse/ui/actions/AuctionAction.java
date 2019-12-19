package xyz.fluxinc.auctionhouse.ui.actions;

import xyz.fluxinc.auctionhouse.controllers.AuctionHouseController;
import xyz.fluxinc.auctionhouse.controllers.UserInterfaceController;
import xyz.fluxinc.auctionhouse.entries.auction.Auction1755082;
import xyz.fluxinc.auctionhouse.exceptions.auction.AuctionNotFoundException;
import xyz.fluxinc.auctionhouse.exceptions.auction.BidTooLowException;
import xyz.fluxinc.auctionhouse.exceptions.authentication.AuthenticationException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuctionAction implements ActionListener {

    private UserInterfaceController userInterfaceController;
    private AuctionHouseController auctionHouseController;
    private int auctionId;

    public AuctionAction(UserInterfaceController userInterfaceController, AuctionHouseController auctionHouseController, int auctionId) {
        this.auctionId = auctionId;
        this.userInterfaceController = userInterfaceController;
        this.auctionHouseController = auctionHouseController;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "back":
                try {
                    userInterfaceController.showAuctions();
                } catch (SpaceException ex) {
                    ex.printStackTrace();
                }
                break;
            case "place-bid":
                String value = JOptionPane.showInputDialog(userInterfaceController.getWindow(), "How much would you like to bid?");
                double dValue;
                try {
                    dValue = Double.parseDouble(value);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(userInterfaceController.getWindow(), "Bid must be number!");
                    break;
                }

                try {
                    auctionHouseController.placeBid(auctionId, dValue);
                    userInterfaceController.showAuction(auctionId);
                } catch (BidTooLowException ex) {
                    JOptionPane.showMessageDialog(userInterfaceController.getWindow(), "Bid must be greater than the highest bid!");
                } catch (SpaceException | AuctionNotFoundException ex) {
                    ex.printStackTrace();
                } catch (AuthenticationException ex) {
                    JOptionPane.showMessageDialog(userInterfaceController.getWindow(), "You must be logged in to perform this action!");
                }
                break;
            case "accept-bid":
                try {
                    auctionHouseController.acceptBid(auctionId);
                    userInterfaceController.invalidateLastScreen();
                    userInterfaceController.showAuction(auctionHouseController.readAuction(auctionId));
                } catch (SpaceException | AuctionNotFoundException ex) {
                    ex.printStackTrace();
                }
                break;
            case "buy-it-now":
                try {
                    Auction1755082 auction = auctionHouseController.buyAuction(auctionId);
                    userInterfaceController.showAuctions();
                } catch (SpaceException ignored) {
                }
                break;
            default:
                break;
        }
    }
}
