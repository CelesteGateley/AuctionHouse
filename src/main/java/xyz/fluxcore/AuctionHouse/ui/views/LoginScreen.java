package xyz.fluxcore.AuctionHouse.ui.views;

import xyz.fluxcore.AuctionHouse.controllers.AuctionHouseController;
import xyz.fluxcore.AuctionHouse.exceptions.SpaceException;
import xyz.fluxcore.AuctionHouse.exceptions.authentication.AuthenticationException;
import xyz.fluxcore.AuctionHouse.exceptions.authentication.UserNotFoundException;
import xyz.fluxcore.AuctionHouse.ui.actions.LoginAction;

import javax.swing.*;
import java.awt.*;

public class LoginScreen {

    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private AuctionHouseController auctionHouseController;

    public LoginScreen(AuctionHouseController houseController) {
        this.auctionHouseController = houseController;

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3,1));
        mainPanel.add(new JLabel("Please Login to Continue!", SwingConstants.CENTER));

        initializeFields();

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new GridLayout(2, 2));
        subPanel.add(new JLabel("Username:", SwingConstants.LEFT));
        subPanel.add(usernameField);
        subPanel.add(new JLabel("Password:", SwingConstants.LEFT));
        subPanel.add(passwordField);
        mainPanel.add(subPanel);

        JButton submitButton = new JButton("Login");

        submitButton.addActionListener(new LoginAction(houseController, usernameField, passwordField));

        mainPanel.add(submitButton);
    }

    private void initializeFields() {
        usernameField = new JTextField();
        passwordField = new JPasswordField();
    }

    public JPanel getPanel() { return mainPanel; }
}
