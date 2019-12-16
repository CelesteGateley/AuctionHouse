package xyz.fluxinc.auctionhouse.ui.actions;

import xyz.fluxinc.auctionhouse.controllers.AuctionHouseController;
import xyz.fluxinc.auctionhouse.controllers.UserInterfaceController;
import xyz.fluxinc.auctionhouse.entries.auction.AuctionU1755082;
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
                double dValue = Double.parseDouble(value);


                break;
            case "accept-bid":
                // TODO: Implement
                break;
            case "buy-it-now":
                try {
                    AuctionU1755082 auction = auctionHouseController.buyAuction(auctionId);
                    userInterfaceController.showAuctions();
                } catch (SpaceException ignored) {}
                break;
            default:
                break;
        }
    }
}
