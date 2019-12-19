package xyz.fluxinc.auctionhouse.ui.actions;

import xyz.fluxinc.auctionhouse.controllers.AuthenticationController;
import xyz.fluxinc.auctionhouse.controllers.UserInterfaceController;
import xyz.fluxinc.auctionhouse.exceptions.authentication.AuthenticationException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NavbarAction implements ActionListener {

    private AuthenticationController authenticationController;
    private UserInterfaceController uiController;

    public NavbarAction(UserInterfaceController uiController, AuthenticationController authenticationController) {
        this.uiController = uiController;
        this.authenticationController = authenticationController;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        switch(actionEvent.getActionCommand()) {
            case "login":
                uiController.showLoginScreen();
                break;
            case "register":
                uiController.showRegisterScreen();
                break;
            case "back":
                uiController.showPreviousScreen();
                break;
            case "show-all-auctions":
                try {
                    uiController.showAuctions();
                } catch (SpaceException e) { e.printStackTrace(); }
                break;
            case "place-auction":
                uiController.showMakeAuction();
                break;
            case "change-contact-info":
                String contactInfo = JOptionPane.showInputDialog(uiController.getWindow(), "Enter the new contact information");
                if (contactInfo.isEmpty()) {
                    JOptionPane.showMessageDialog(uiController.getWindow(), "You must have contact information");
                    break;
                }
                try {
                    authenticationController.changeContactInfo(contactInfo);
                } catch (SpaceException e) {
                    e.printStackTrace();
                }
                break;
            case "change-password":
                String oldPassword = JOptionPane.showInputDialog(uiController.getWindow(), "Enter your current password");
                String newPassword = JOptionPane.showInputDialog(uiController.getWindow(), "Enter your new password");
                String confirmPassword = JOptionPane.showInputDialog(uiController.getWindow(), "Re-enter your new password");

                if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(uiController.getWindow(), "You must enter something into each box");
                }
                if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(uiController.getWindow(), "Passwords do not match");
                }

                try {
                    authenticationController.changePassword(oldPassword, newPassword);
                } catch (AuthenticationException ex) {
                    JOptionPane.showMessageDialog(uiController.getWindow(), "Incorrect Password Entered");
                } catch (SpaceException ignored) { }

            default:
                break;
        }
    }
}
