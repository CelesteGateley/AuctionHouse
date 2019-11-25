package xyz.fluxcore.AuctionHouse.ui.views;

import javax.swing.*;
import java.awt.*;

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
