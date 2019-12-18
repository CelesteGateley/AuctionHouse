package xyz.fluxinc.auctionhouse.ui.actions;

import xyz.fluxinc.auctionhouse.controllers.UserInterfaceController;

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
            default:
                break;
        }
    }
}
