package xyz.fluxinc.AuctionHouse;

import xyz.fluxinc.AuctionHouse.controllers.SpaceController;
import xyz.fluxinc.AuctionHouse.controllers.SystemController;
import xyz.fluxinc.AuctionHouse.entries.Auction;
import xyz.fluxinc.AuctionHouse.entries.AuctionHouse;
import xyz.fluxinc.AuctionHouse.entries.Bid;
import xyz.fluxinc.AuctionHouse.exceptions.auction.AuctionNotFoundException;
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

    public static void main(String[] args) throws SpaceNotFoundException, SpaceException, UserExistsException, UserNotFoundException, AuthenticationException, AuctionNotFoundException {
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

        systemController.getAuthenticationController().login("root", "root");

        systemController.getAuctionHouseController().placeAuction("Test 1", 1, 1);

        for (int i = 0; i < 10; i++) {
            systemController.getAuctionHouseController().placeBid(0, i);
        }

        List<Bid> bids = systemController.getAuctionHouseController().getBids(0);
        for (Bid bid : bids) {
            System.out.println(bid.bidAmount);
        }

        System.exit(0);

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
