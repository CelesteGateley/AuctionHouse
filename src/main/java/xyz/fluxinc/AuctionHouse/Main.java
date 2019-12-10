package xyz.fluxinc.AuctionHouse;

import xyz.fluxinc.AuctionHouse.controllers.SpaceController;
import xyz.fluxinc.AuctionHouse.controllers.SystemController;
import xyz.fluxinc.AuctionHouse.entries.Auction;
import xyz.fluxinc.AuctionHouse.entries.AuctionHouse;
import xyz.fluxinc.AuctionHouse.exceptions.authentication.AuthenticationException;
import xyz.fluxinc.AuctionHouse.exceptions.authentication.UserNotFoundException;
import xyz.fluxinc.AuctionHouse.exceptions.space.SpaceException;
import xyz.fluxinc.AuctionHouse.exceptions.space.SpaceNotFoundException;
import xyz.fluxinc.AuctionHouse.exceptions.authentication.UserExistsException;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class Main {
    //-Djava.security.policy=policy.all -Djava.rmi.server.useCodebaseOnly=false

    public static void main(String[] args) throws SpaceNotFoundException, SpaceException, UserExistsException, UserNotFoundException, AuthenticationException {
        List<String> arguments = Arrays.asList(args);
        boolean addRoot = arguments.contains("-add-root");
        boolean anonymous = arguments.contains("-anonymous");
        String hostname = getHostName(arguments);
        SpaceController spTemp = new SpaceController(hostname);
        spTemp.takeAll(new AuctionHouse());
        spTemp = null;

        SystemController systemController = new SystemController(hostname);
        if (addRoot) {
            try { systemController.getAuthenticationController().registerAdministrator("root", "root"); }
            catch(UserExistsException ignored) {}
        }

        //systemController.getUserInterfaceController().showLoginScreen();

        systemController.getAuthenticationController().login("root", "root");

        systemController.getAuctionHouseController().placeAuction("Test 1", 1, 1);
        systemController.getAuctionHouseController().placeAuction("Test 2", 1, 1);
        systemController.getAuctionHouseController().placeAuction("Test 3", 1, 1);
        systemController.getAuctionHouseController().placeAuction("Test 4", 1, 1);
        systemController.getAuctionHouseController().placeAuction("Test 5", 1, 1);
        systemController.getAuctionHouseController().placeAuction("Test 6", 1, 1);

        Collection<Auction> auctions = systemController.getAuctionHouseController().readAllAuctions();
        systemController.getSpaceController().takeAll(new Auction());

        for (Auction auction : auctions) {
            System.out.println(auction.auctionId + ": " + auction.name);
        }


    }

    private static String getHostName(List<String> arguments) {
        if (arguments.contains("-host")) {
            int index = arguments.indexOf("-host");
            try {
                return arguments.get(index + 1);
            } catch (IndexOutOfBoundsException e) {
                return "localhost";
            }
        } else {
            return "localhost";
        }
    }

}
