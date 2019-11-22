package xyz.fluxcore.AuctionHouse;

import xyz.fluxcore.AuctionHouse.controllers.AuctionHouseController;
import xyz.fluxcore.AuctionHouse.exceptions.SpaceException;
import xyz.fluxcore.AuctionHouse.exceptions.SpaceNotFoundException;
import xyz.fluxcore.AuctionHouse.exceptions.authentication.UserExistsException;
import xyz.fluxcore.AuctionHouse.ui.LoginScreen;

import javax.swing.*;


public class Main {
    //-Djava.security.policy=policy.all -Djava.rmi.server.useCodebaseOnly=false



    public static void main(String[] args) throws SpaceNotFoundException, SpaceException, UserExistsException {
        String hostname;
        if (args.length < 1) { hostname = "localhost"; }
        else { hostname = args[0]; }

        AuctionHouseController auctionHouseController = new AuctionHouseController(hostname);
        //auctionHouseController.registerAdministrator("root", "root");
        LoginScreen loginScreen = new LoginScreen(auctionHouseController);

        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle("Welcome to my Auction House!");
        window.setSize(400, 400);
        window.setVisible(true);
        window.setContentPane(loginScreen.getPanel());

    }

}
