package xyz.fluxinc.AuctionHouse.ui.actions;

import xyz.fluxinc.AuctionHouse.controllers.AuctionHouseController;
import xyz.fluxinc.AuctionHouse.controllers.AuthenticationController;
import xyz.fluxinc.AuctionHouse.exceptions.SpaceException;
import xyz.fluxinc.AuctionHouse.exceptions.authentication.AuthenticationException;
import xyz.fluxinc.AuctionHouse.exceptions.authentication.UserExistsException;
import xyz.fluxinc.AuctionHouse.exceptions.authentication.UserNotFoundException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthenticateAction implements ActionListener {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private AuthenticationController authenticationController;

    public AuthenticateAction(AuthenticationController authenticationController) {
        this.authenticationController = authenticationController;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "logout":
                authenticationController.logout();
                break;
            case "login":
            case "register":
                String username = usernameField.getText();
                String password = String.copyValueOf(passwordField.getPassword());
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No username was input!");
                    return;
                }
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No password was input!");
                    return;
                }
                try {
                    if (e.getActionCommand() == "login") {
                        authenticationController.login(username, password);
                        JOptionPane.showMessageDialog(null, "Logged in successfully!");
                    } else if (e.getActionCommand() == "register") {
                        authenticationController.register(username, password);
                        JOptionPane.showMessageDialog(null, "Registered successfully!");
                    }
                } catch (SpaceException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                    ex.printStackTrace();
                } catch (UserNotFoundException | UserExistsException | AuthenticationException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            default:
                break;
        }
    }

    public void setUsernameField(JTextField usernameField) {
        this.usernameField = usernameField;
    }

    public void setPasswordField(JPasswordField passwordField) {
        this.passwordField = passwordField;
    }
}
