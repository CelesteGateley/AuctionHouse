package xyz.fluxcore.AuctionHouse.ui;

import xyz.fluxcore.AuctionHouse.controllers.AuctionHouseController;
import xyz.fluxcore.AuctionHouse.exceptions.SpaceException;
import xyz.fluxcore.AuctionHouse.exceptions.authentication.AuthenticationException;
import xyz.fluxcore.AuctionHouse.exceptions.authentication.UserNotFoundException;

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
        submitButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = String.copyValueOf(passwordField.getPassword());
            if (username.isEmpty()) { JOptionPane.showMessageDialog(null, "No username was input!"); return; }
            if (password.isEmpty()) { JOptionPane.showMessageDialog(null, "No password was input!"); return; }
            try {
                auctionHouseController.login(username, password);
                JOptionPane.showMessageDialog(null, "Logged in successfully!");
            } catch (SpaceException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
                ex.printStackTrace();
            } catch (UserNotFoundException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            } catch (AuthenticationException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });
        mainPanel.add(submitButton);
    }

    private void initializeFields() {
        usernameField = new JTextField();
        passwordField = new JPasswordField();
    }

    public JPanel getPanel() { return mainPanel; }
}
