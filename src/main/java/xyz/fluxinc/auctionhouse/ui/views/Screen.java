package xyz.fluxinc.auctionhouse.ui.views;

import javax.swing.*;

public abstract class Screen {

    private JPanel mainPanel;

    protected Screen() {
        mainPanel = new JPanel();
    }

    protected abstract void initialize();

    protected JPanel getPanel() {
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
