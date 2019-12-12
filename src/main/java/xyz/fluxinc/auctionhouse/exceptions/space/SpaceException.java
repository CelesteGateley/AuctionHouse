package xyz.fluxinc.auctionhouse.exceptions.space;

import javax.swing.*;

public class SpaceException extends Exception {

    public SpaceException(Exception error) {
        super(error);
        JOptionPane.showMessageDialog(null, this.getMessage(), "An Error Occurred within the Space", JOptionPane.ERROR_MESSAGE);
        this.printStackTrace();
    }

    public SpaceException(String error) {
        super(error);
        JOptionPane.showMessageDialog(null, this.getMessage(), "An Error Occurred within the Space", JOptionPane.ERROR_MESSAGE);
        this.printStackTrace();
    }
}
