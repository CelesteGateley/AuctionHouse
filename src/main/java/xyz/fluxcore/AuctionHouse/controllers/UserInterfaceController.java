package xyz.fluxcore.AuctionHouse.controllers;

import xyz.fluxcore.AuctionHouse.ui.actions.LoginAction;
import xyz.fluxcore.AuctionHouse.ui.views.LoginScreen;

import javax.swing.*;

public class UserInterfaceController {

    private AuctionHouseController auctionHouseController;
    private JFrame window;

    public UserInterfaceController(AuctionHouseController auctionHouseController) {
        this.auctionHouseController = auctionHouseController;
        window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle("Welcome to my Auction House!");
        window.setSize(400, 400);
        window.setVisible(true);
    }


    public void showLoginScreen() {
        LoginScreen loginScreen = new LoginScreen(new LoginAction(auctionHouseController));
        window.setContentPane(loginScreen.getPanel());
        window.setVisible(true);
    }

    private void clearScreen() {
        window.getContentPane().removeAll();
    }
}
