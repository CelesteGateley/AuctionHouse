package xyz.fluxinc.auctionhouse.controllers;

import xyz.fluxinc.auctionhouse.entries.authentication.User;
import xyz.fluxinc.auctionhouse.entries.auction.Auction;
import xyz.fluxinc.auctionhouse.entries.notifications.Notification;
import xyz.fluxinc.auctionhouse.exceptions.auction.AuctionNotFoundException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;
import xyz.fluxinc.auctionhouse.ui.actions.AuctionAction;
import xyz.fluxinc.auctionhouse.ui.actions.AuthenticateAction;
import xyz.fluxinc.auctionhouse.ui.actions.NavbarAction;
import xyz.fluxinc.auctionhouse.ui.views.Screen;
import xyz.fluxinc.auctionhouse.ui.views.auction.AllAuctionsScreen;
import xyz.fluxinc.auctionhouse.ui.views.auction.AuctionScreen;
import xyz.fluxinc.auctionhouse.ui.views.auction.PlaceAuctionScreen;
import xyz.fluxinc.auctionhouse.ui.views.authentication.LoginScreen;
import xyz.fluxinc.auctionhouse.ui.views.authentication.RegisterScreen;

import java.util.*;
import java.util.Timer;

import javax.swing.*;

public class UserInterfaceController {

    private AuctionHouseController auctionHouseController;
    private AuthenticationController authenticationController;
    private JFrame window;
    private JMenuBar navbar;
    private List<JPanel> backLog;

    private JMenuItem registerButton;
    private JMenuItem loginButton;
    private JMenuItem logoutButton;
    private JMenuItem showAllAuctions;
    private JMenuItem placeAuction;
    private JMenuItem notificationButton;

    public UserInterfaceController(AuthenticationController authenticationController, AuctionHouseController auctionHouseController) {
        this.authenticationController = authenticationController;
        this.auctionHouseController = auctionHouseController;
        backLog = new ArrayList<>();
        window = new JFrame();
        navbar = new JMenuBar();
        initializeNavbar();
        window.setJMenuBar(navbar);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle("Welcome to my Auction House!");
        window.setSize(400, 400);

        Timer notificationTimer = new Timer();
        notificationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                List<Notification> notifications = auctionHouseController.getNotifications();
                int counter = 0;
                for (Notification notification : notifications) { if (!notification.isRead()) counter++; }
                notificationButton.setText("Notifications (" + counter + ")");
            }
        }, SpaceController.ONE_SECOND, SpaceController.ONE_SECOND * 5);

        //window.setVisible(true);
    }


    public void showLoginScreen() {
        clearScreen();
        authenticationController.logout();
        LoginScreen loginScreen = new LoginScreen(new AuthenticateAction(this, authenticationController));
        addToBacklog(loginScreen);
        window.setContentPane(loginScreen.getPanel());
        window.setVisible(true);
    }

    public void showRegisterScreen() {
        clearScreen();
        authenticationController.logout();
        RegisterScreen registerScreen = new RegisterScreen(new AuthenticateAction(this, authenticationController));
        addToBacklog(registerScreen);
        window.setContentPane(registerScreen.getPanel());
        window.setVisible(true);
    }

    public void showAuctions() throws SpaceException {
        clearScreen();
        AllAuctionsScreen allAuctionsScreen = new AllAuctionsScreen(auctionHouseController, this);
        addToBacklog(allAuctionsScreen);
        window.setContentPane(allAuctionsScreen.getPanel());
        window.setVisible(true);
    }

    public void showAuction(Auction auction) throws AuctionNotFoundException, SpaceException {
        clearScreen();
        AuctionScreen auctionScreen = new AuctionScreen(auction, auctionHouseController, new AuctionAction(this, auctionHouseController, auction.auctionId));
        addToBacklog(auctionScreen);
        window.setContentPane(auctionScreen.getPanel());
        window.setVisible(true);
    }

    public void showMakeAuction() {
        clearScreen();
        PlaceAuctionScreen placeAuctionScreen = new PlaceAuctionScreen(auctionHouseController, this);
        addToBacklog(placeAuctionScreen);
        window.setContentPane(placeAuctionScreen.getPanel());
        window.setVisible(true);
    }

    public void showAccount() {
        User user = authenticationController.getUser();
        clearScreen();
        // TODO Implement
    }

    public void showPreviousScreen() {
        System.out.println(backLog.size());
        if (backLog.size() > 1) {
            clearScreen();
            backLog.remove(backLog.size() - 1);
            JPanel screen = backLog.get(backLog.size()-1);
            window.setContentPane(screen);
            window.setVisible(true);
        }
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

        navbar.add(authMenu);

        JMenu navigation = new JMenu("Navigation");

        placeAuction = new JMenuItem("Place Auction");
        placeAuction.setActionCommand("place-auction");
        placeAuction.addActionListener(navbarAction);
        navigation.add(placeAuction);

        showAllAuctions = new JMenuItem("Show all Auctions");
        showAllAuctions.setActionCommand("show-all-auctions");
        showAllAuctions.addActionListener(navbarAction);
        navigation.add(showAllAuctions);

        JMenuItem backButton = new JMenuItem("Back");
        backButton.addActionListener(navbarAction);
        backButton.setActionCommand("back");
        navigation.add(backButton);

        navbar.add(navigation);

        notificationButton = new JMenuItem("Notifications (0)");
        notificationButton.addActionListener(navbarAction);
        notificationButton.setActionCommand("view-notifications");
        notificationButton.setMaximumSize(notificationButton.getPreferredSize());
        navbar.add(notificationButton);

        verifyAuthButtons();
    }

    private void clearScreen() {
        window.getContentPane().removeAll();
        verifyAuthButtons();
    }

    private void verifyAuthButtons() {
        registerButton.setEnabled(!authenticationController.isLoggedIn());
        loginButton.setEnabled(!authenticationController.isLoggedIn());
        showAllAuctions.setEnabled(authenticationController.isLoggedIn());
        placeAuction.setEnabled(authenticationController.isLoggedIn());
        logoutButton.setEnabled(authenticationController.isLoggedIn());
    }

    private void addToBacklog(Screen screen) {
        backLog.add(screen.getPanel());
    }

    public JFrame getWindow() { return window; }

    public void invalidateLastScreen() {
        backLog.remove(backLog.size()-1);
    }

    public void showAuction(int auctionId) throws SpaceException, AuctionNotFoundException {
        showAuction(auctionHouseController.readAuction(auctionId));
    }
}
