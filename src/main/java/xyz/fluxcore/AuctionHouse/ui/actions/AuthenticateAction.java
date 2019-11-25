package xyz.fluxcore.AuctionHouse.ui.actions;

import xyz.fluxcore.AuctionHouse.controllers.AuctionHouseController;
import xyz.fluxcore.AuctionHouse.exceptions.SpaceException;
import xyz.fluxcore.AuctionHouse.exceptions.authentication.AuthenticationException;
import xyz.fluxcore.AuctionHouse.exceptions.authentication.UserExistsException;
import xyz.fluxcore.AuctionHouse.exceptions.authentication.UserNotFoundException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthenticateAction implements ActionListener {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private AuctionHouseController auctionHouseController;

    public AuthenticateAction(AuctionHouseController auctionHouseController) {
        this.auctionHouseController = auctionHouseController;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "logout":
                auctionHouseController.logout();
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
                        auctionHouseController.login(username, password);
                        JOptionPane.showMessageDialog(null, "Logged in successfully!");
                    } else if (e.getActionCommand() == "register") {
                        auctionHouseController.register(username, password);
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
