package xyz.fluxcore.AuctionHouse.controllers;

import xyz.fluxcore.AuctionHouse.ui.actions.AuthenticateAction;
import xyz.fluxcore.AuctionHouse.ui.actions.NavbarAction;
import xyz.fluxcore.AuctionHouse.ui.views.LoginScreen;
import xyz.fluxcore.AuctionHouse.ui.views.RegisterScreen;

import javax.swing.*;

public class UserInterfaceController {

    private AuctionHouseController auctionHouseController;
    private JFrame window;
    private JMenuBar navbar;

    public UserInterfaceController(AuctionHouseController auctionHouseController) {
        this.auctionHouseController = auctionHouseController;
        window = new JFrame();
        navbar = new JMenuBar();
        initializeNavbar();
        window.setJMenuBar(navbar);
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

    private void initializeNavbar() {
        // Auth Menu
        JMenu authMenu = new JMenu("Authentication");

        // Login Button
        NavbarAction navbarAction = new NavbarAction(this);
        JMenuItem loginButton = new JMenuItem("Login");
        loginButton.addActionListener(navbarAction);
        loginButton.setActionCommand("login");
        authMenu.add(loginButton);

        // Login Button
        JMenuItem registerButton = new JMenuItem("Register");
        registerButton.addActionListener(navbarAction);
        registerButton.setActionCommand("register");
        authMenu.add(registerButton);

        navbar.add(authMenu);

    }

    private void clearScreen() {
        window.getContentPane().removeAll();
    }
}
