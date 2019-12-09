package xyz.fluxinc.AuctionHouse.controllers;

import xyz.fluxinc.AuctionHouse.ui.actions.AuthenticateAction;
import xyz.fluxinc.AuctionHouse.ui.actions.NavbarAction;
import xyz.fluxinc.AuctionHouse.ui.views.LoginScreen;
import xyz.fluxinc.AuctionHouse.ui.views.RegisterScreen;

import javax.swing.*;

public class UserInterfaceController {

    private AuthenticationController authenticationController;
    private JFrame window;
    private JMenuBar navbar;

    public UserInterfaceController(AuthenticationController authenticationController) {
        this.authenticationController = authenticationController;
        window = new JFrame();
        navbar = new JMenuBar();
        initializeNavbar();
        window.setJMenuBar(navbar);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle("Welcome to my Auction House!");
        window.setSize(400, 400);
    }


    public void showLoginScreen() {
        clearScreen();
        LoginScreen loginScreen = new LoginScreen(new AuthenticateAction(authenticationController));
        window.setContentPane(loginScreen.getPanel());
        window.setVisible(true);
    }

    public void showRegisterScreen() {
        clearScreen();
        RegisterScreen registerScreen = new RegisterScreen(new AuthenticateAction(authenticationController));
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

        authMenu.add(new JSeparator());

        JMenuItem logoutButton = new JMenuItem("Logout");
        logoutButton.addActionListener(new AuthenticateAction(authenticationController));
        logoutButton.setActionCommand("logout");
        authMenu.add(logoutButton);

        navbar.add(authMenu);
    }

    private void clearScreen() {
        window.getContentPane().removeAll();
    }
}
