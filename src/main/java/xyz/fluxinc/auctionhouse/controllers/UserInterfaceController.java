package xyz.fluxinc.auctionhouse.controllers;

import xyz.fluxinc.auctionhouse.entries.User;
import xyz.fluxinc.auctionhouse.entries.auction.Auction;
import xyz.fluxinc.auctionhouse.entries.notifications.Notification;
import xyz.fluxinc.auctionhouse.ui.actions.AuthenticateAction;
import xyz.fluxinc.auctionhouse.ui.actions.NavbarAction;
import xyz.fluxinc.auctionhouse.ui.views.authentication.LoginScreen;
import xyz.fluxinc.auctionhouse.ui.views.authentication.RegisterScreen;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;

public class UserInterfaceController {

    private AuctionHouseController auctionHouseController;
    private AuthenticationController authenticationController;
    private JFrame window;
    private JMenuBar navbar;
    private JMenuItem notificationButton;
    private Timer notificationTimer;

    private JMenuItem registerButton;
    private JMenuItem loginButton;
    private JMenuItem logoutButton;

    public UserInterfaceController(AuthenticationController authenticationController, AuctionHouseController auctionHouseController) {
        this.authenticationController = authenticationController;
        this.auctionHouseController = auctionHouseController;
        window = new JFrame();
        navbar = new JMenuBar();
        initializeNavbar();
        window.setJMenuBar(navbar);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle("Welcome to my Auction House!");
        window.setSize(400, 400);

        notificationTimer = new Timer();
        notificationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                List<Notification> notifications = auctionHouseController.getNotifications();
                int counter = 0;
                for (Notification notification : notifications) { if (!notification.isRead()) counter++; }
                notificationButton.setText("Notifications (" + counter + ")");
            }
        }, SpaceController.ONE_SECOND, SpaceController.ONE_SECOND * 5);

        window.setVisible(true);
    }


    public void showLoginScreen() {
        clearScreen();
        LoginScreen loginScreen = new LoginScreen(new AuthenticateAction(this, authenticationController));
        window.setContentPane(loginScreen.getPanel());
        window.setVisible(true);
    }

    public void showRegisterScreen() {
        clearScreen();
        RegisterScreen registerScreen = new RegisterScreen(new AuthenticateAction(this, authenticationController));
        window.setContentPane(registerScreen.getPanel());
        window.setVisible(true);
    }

    public void showAuctions() {
        clearScreen();
        // TODO: Implement
    }

    public void showAuction(Auction auction) {
        clearScreen();
        // TODO: Implement
    }

    public void showAccount() {
        User user = authenticationController.getUser();
        clearScreen();
        // TODO Implement
    }

    private void initializeNavbar() {
        // Auth Menu
        JMenu authMenu = new JMenu("Authentication");

        // Login Button
        NavbarAction navbarAction = new NavbarAction(this);
        loginButton = new JMenuItem("Login");
        loginButton.addActionListener(navbarAction);
        loginButton.setActionCommand("login");
        authMenu.add(loginButton);

        // Login Button
        registerButton = new JMenuItem("Register");
        registerButton.addActionListener(navbarAction);
        registerButton.setActionCommand("register");
        authMenu.add(registerButton);

        authMenu.add(new JSeparator());

        logoutButton = new JMenuItem("Logout");
        logoutButton.addActionListener(new AuthenticateAction(this, authenticationController));
        logoutButton.setActionCommand("logout");
        authMenu.add(logoutButton);

        verifyAuthButtons();

        navbar.add(authMenu);

        notificationButton = new JMenuItem("Notifications (0)");
        notificationButton.addActionListener(navbarAction);
        notificationButton.setActionCommand("view-notifications");
        navbar.add(notificationButton);
    }

    private void clearScreen() {
        window.getContentPane().removeAll();
        verifyAuthButtons();
    }

    private void verifyAuthButtons() {
        registerButton.setEnabled(!authenticationController.isLoggedIn());
        loginButton.setEnabled(!authenticationController.isLoggedIn());
        logoutButton.setEnabled(authenticationController.isLoggedIn());
    }
}
