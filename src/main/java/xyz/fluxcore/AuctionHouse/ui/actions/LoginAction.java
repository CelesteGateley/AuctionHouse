package xyz.fluxcore.AuctionHouse.ui.actions;

import xyz.fluxcore.AuctionHouse.controllers.AuctionHouseController;
import xyz.fluxcore.AuctionHouse.exceptions.SpaceException;
import xyz.fluxcore.AuctionHouse.exceptions.authentication.AuthenticationException;
import xyz.fluxcore.AuctionHouse.exceptions.authentication.UserNotFoundException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginAction implements ActionListener {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private AuctionHouseController auctionHouseController;

    public LoginAction(AuctionHouseController auctionHouseController) {
        this.auctionHouseController = auctionHouseController;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
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
    }

    public void setUsernameField(JTextField usernameField) {
        this.usernameField = usernameField;
    }

    public void setPasswordField(JPasswordField passwordField) {
        this.passwordField = passwordField;
    }
}
