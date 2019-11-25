package xyz.fluxcore.AuctionHouse.controllers;

import xyz.fluxcore.AuctionHouse.ui.actions.AuthenticateAction;
import xyz.fluxcore.AuctionHouse.ui.views.LoginScreen;
import xyz.fluxcore.AuctionHouse.ui.views.RegisterScreen;

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
        clearScreen();
        LoginScreen loginScreen = new LoginScreen(new AuthenticateAction(auctionHouseController));
        window.setContentPane(loginScreen.getPanel());
        window.setVisible(true);
    }

    public void showRegisterScreen() {
        clearScreen();
        RegisterScreen registerScreen = new RegisterScreen(new AuthenticateAction(auctionHouseController));
        window.setContentPane(registerScreen.getPanel());
        window.setVisible(true);
    }

    private void clearScreen() {
        window.getContentPane().removeAll();
    }
}
