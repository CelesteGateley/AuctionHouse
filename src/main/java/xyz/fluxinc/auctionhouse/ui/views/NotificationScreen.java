package xyz.fluxinc.auctionhouse.ui.views;

import xyz.fluxinc.auctionhouse.controllers.AuctionHouseController;
import xyz.fluxinc.auctionhouse.controllers.UserInterfaceController;
import xyz.fluxinc.auctionhouse.entries.notifications.Notification;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public class NotificationScreen extends Screen implements ListSelectionListener {

    private AuctionHouseController auctionHouseController;
    private UserInterfaceController userInterfaceController;

    private JList<Notification> list;

    public NotificationScreen(AuctionHouseController auctionHouseController, UserInterfaceController userInterfaceController) {
        this.auctionHouseController = auctionHouseController;
        this.userInterfaceController = userInterfaceController;
    }

    @Override
    public void initialize() {
        getPanel().removeAll();
        DefaultListModel defaultListModel = new DefaultListModel();
        List<Notification> notifications = auctionHouseController.getNotifications();
        Collections.reverse(notifications);
        for (Notification notification : notifications) { defaultListModel.addElement(notification); }

        list = new JList(defaultListModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);
        JScrollPane scrollPane = new JScrollPane(list);
        getPanel().setLayout(new GridLayout(1,1));
        getPanel().add(scrollPane);
    }


    @Override
    public void valueChanged(ListSelectionEvent e) {
        try {
            userInterfaceController.showAuction(auctionHouseController.getAuction(list.getSelectedValue().getAuctionId()));
        } catch (SpaceException ignored) {}
    }
}
