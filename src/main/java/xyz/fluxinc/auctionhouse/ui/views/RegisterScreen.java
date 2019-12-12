package xyz.fluxinc.auctionhouse.ui.views;

import xyz.fluxinc.auctionhouse.ui.actions.AuthenticateAction;

import javax.swing.*;
import java.awt.*;

public class RegisterScreen extends Screen {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public RegisterScreen(AuthenticateAction authenticateAction) {
        getPanel().setLayout(new GridLayout(3,1));
        getPanel().add(new JLabel("Please Register to Continue!", SwingConstants.CENTER));

        initializeFields();
        authenticateAction.setUsernameField(usernameField);
        authenticateAction.setPasswordField(passwordField);

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new GridLayout(2, 2));
        subPanel.add(new JLabel("Username:", SwingConstants.LEFT));
        subPanel.add(usernameField);
        subPanel.add(new JLabel("Password:", SwingConstants.LEFT));
        subPanel.add(passwordField);
        getPanel().add(subPanel);

        JButton submitButton = new JButton("Register!");

        submitButton.addActionListener(authenticateAction);
        submitButton.setActionCommand("register");

        getPanel().add(submitButton);
    }

    private void initializeFields() {
        usernameField = new JTextField();
        passwordField = new JPasswordField();
    }

}
