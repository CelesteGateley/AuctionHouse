package xyz.fluxinc.auctionhouse.ui.actions;

import xyz.fluxinc.auctionhouse.controllers.AuthenticationController;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;
import xyz.fluxinc.auctionhouse.exceptions.authentication.AuthenticationException;
import xyz.fluxinc.auctionhouse.exceptions.authentication.UserExistsException;
import xyz.fluxinc.auctionhouse.exceptions.authentication.UserNotFoundException;
import xyz.fluxinc.auctionhouse.exceptions.ui.EmptyFieldException;

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
                try {
                    String[] values = getDetails();
                    authenticationController.login(values[0], values[1]);
                } catch (EmptyFieldException | AuthenticationException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                    break;
                } catch (SpaceException ex) {
                    JOptionPane.showMessageDialog(null, "An error has occurred whilst interacting with the space");
                    ex.printStackTrace();
                    break;
                } catch (UserNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "A user with that name was not found in the system");
                    break;
                }
            case "register":
                try {
                    String[] values = getDetails();
                    authenticationController.register(values[0], values[1]);
                } catch (EmptyFieldException | UserExistsException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                    break;
                } catch (SpaceException ex) {
                    JOptionPane.showMessageDialog(null, "An error has occurred whilst interacting with the space");
                    ex.printStackTrace();
                    break;
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

    private String[] getDetails() throws EmptyFieldException {
        String username = usernameField.getText();
        String password = String.copyValueOf(passwordField.getPassword());
        if (username.isEmpty()) { throw new EmptyFieldException("No username was input!"); }
        if (password.isEmpty()) { throw new EmptyFieldException("No password was input!"); }
        String[] values = new String[2];
        values[0] = username;
        values[1] = password;
        return values;
    }
}
