package xyz.fluxinc.AuctionHouse.ui.actions;

import xyz.fluxinc.AuctionHouse.controllers.UserInterfaceController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NavbarAction implements ActionListener {

    UserInterfaceController uiController;

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
            default:
                break;
        }
    }
}
