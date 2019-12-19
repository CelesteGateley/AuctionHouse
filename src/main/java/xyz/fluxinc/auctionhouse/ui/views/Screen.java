package xyz.fluxinc.auctionhouse.ui.views;

import xyz.fluxinc.auctionhouse.exceptions.auction.AuctionNotFoundException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;

import javax.swing.*;

public abstract class Screen {

    private JPanel mainPanel;

    public Screen() {
        mainPanel = new JPanel();
    }

    public abstract void initialize();

    public JPanel getPanel() {
        return mainPanel;
    }

    public JPanel showPanel() {
        initialize();
        return mainPanel;
    }

    public void setPanel(JPanel panel) {
        this.mainPanel = panel;
    }
}
