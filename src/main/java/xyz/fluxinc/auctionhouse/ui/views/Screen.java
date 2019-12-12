package xyz.fluxinc.auctionhouse.ui.views;

import javax.swing.*;

public class Screen {

    private JPanel mainPanel;

    public Screen() {
        mainPanel = new JPanel();
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public void setPanel(JPanel panel) {
        this.mainPanel = panel;
    }
}
