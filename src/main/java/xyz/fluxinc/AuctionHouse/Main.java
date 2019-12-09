package xyz.fluxinc.AuctionHouse;

import net.jini.core.entry.Entry;
import xyz.fluxinc.AuctionHouse.controllers.AuctionHouseController;
import xyz.fluxinc.AuctionHouse.controllers.SystemController;
import xyz.fluxinc.AuctionHouse.entries.Auction1755082;
import xyz.fluxinc.AuctionHouse.entries.AuctionHouse1755082;
import xyz.fluxinc.AuctionHouse.entries.AuctionHouseLock1755082;
import xyz.fluxinc.AuctionHouse.exceptions.space.SpaceException;
import xyz.fluxinc.AuctionHouse.exceptions.space.SpaceNotFoundException;
import xyz.fluxinc.AuctionHouse.exceptions.authentication.UserExistsException;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class Main {
    //-Djava.security.policy=policy.all -Djava.rmi.server.useCodebaseOnly=false

    public static void main(String[] args) throws SpaceNotFoundException, SpaceException, UserExistsException {
        List<String> arguments = Arrays.asList(args);
        boolean addRoot = arguments.contains("-add-root");
        boolean anonymous = arguments.contains("-anonymous");
        String hostname = getHostName(arguments);

        SystemController systemController = new SystemController(hostname);
        if (addRoot) {
            try { systemController.getAuthenticationController().registerAdministrator("root", "root"); }
            catch(UserExistsException ignored) {}
        }

        //systemController.getUserInterfaceController().showLoginScreen();

        Auction1755082 auction = systemController.getAuctionHouseController().placeAuction("Test", 1);
        AuctionHouse1755082 haus = systemController.getAuctionHouseController().getAuctionHouse();
        System.out.println(haus.currentCount);

        System.out.println(auction.name);
        System.out.println("Finished Processing...");
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
