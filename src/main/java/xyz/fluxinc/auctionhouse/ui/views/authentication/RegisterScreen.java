package xyz.fluxinc.auctionhouse.ui.views.authentication;

import xyz.fluxinc.auctionhouse.ui.actions.AuthenticateAction;
import xyz.fluxinc.auctionhouse.ui.views.Screen;

import javax.swing.*;
import java.awt.*;

public class RegisterScreen extends Screen {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private AuthenticateAction authenticateAction;
    private JTextField contactField;

    public RegisterScreen(AuthenticateAction authenticateAction) {
        this.authenticateAction = authenticateAction;
    }

    private void initializeFields() {
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        contactField = new JTextField();
    }

    @Override
    public void initialize() {
        getPanel().removeAll();
        getPanel().setLayout(new GridLayout(3,1));
        getPanel().add(new JLabel("Please Register to Continue!", SwingConstants.CENTER));

        initializeFields();
        authenticateAction.setUsernameField(usernameField);
        authenticateAction.setPasswordField(passwordField);
        authenticateAction.setContactField(contactField);

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new GridLayout(3, 2));
        subPanel.add(new JLabel("Username:", SwingConstants.LEFT));
        subPanel.add(usernameField);
        subPanel.add(new JLabel("Password:", SwingConstants.LEFT));
        subPanel.add(passwordField);
        subPanel.add(new JLabel("Contact info:", SwingConstants.LEFT));
        subPanel.add(contactField);
        getPanel().add(subPanel);

        JButton submitButton = new JButton("Register!");

        submitButton.addActionListener(authenticateAction);
        submitButton.setActionCommand("register");

        getPanel().add(submitButton);
    }
}
