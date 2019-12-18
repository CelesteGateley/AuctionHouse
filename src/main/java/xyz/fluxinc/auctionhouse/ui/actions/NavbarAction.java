package xyz.fluxinc.auctionhouse.ui.actions;

import xyz.fluxinc.auctionhouse.controllers.UserInterfaceController;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NavbarAction implements ActionListener {

    private UserInterfaceController uiController;

    public NavbarAction(UserInterfaceController uiController) {
        this.uiController = uiController;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        switch(actionEvent.getActionCommand()) {
            case "login":
                uiController.showLoginScreen();
                break;
            case "register":
                uiController.showRegisterScreen();
                break;
            case "back":
                uiController.showPreviousScreen();
                break;
            case "show-all-auctions":
                try {
                    uiController.showAuctions();
                } catch (SpaceException e) { e.printStackTrace(); }
                break;
            case "place-auction":
                uiController.showMakeAuction();
            default:
                break;
        }
    }
}
