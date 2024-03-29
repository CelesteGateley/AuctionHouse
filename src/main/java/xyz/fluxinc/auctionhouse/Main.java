package xyz.fluxinc.auctionhouse;

import net.jini.core.entry.Entry;
import xyz.fluxinc.auctionhouse.controllers.AuctionHouseController;
import xyz.fluxinc.auctionhouse.controllers.SpaceController;
import xyz.fluxinc.auctionhouse.controllers.SystemController;
import xyz.fluxinc.auctionhouse.entries.auction.Auction1755082;
import xyz.fluxinc.auctionhouse.entries.auction.Bid1755082;
import xyz.fluxinc.auctionhouse.entries.auctionhouse.AuctionHouse1755082;
import xyz.fluxinc.auctionhouse.entries.auctionhouse.AuctionHouseLock1755082;
import xyz.fluxinc.auctionhouse.entries.authentication.User1755082;
import xyz.fluxinc.auctionhouse.exceptions.auction.AuctionNotFoundException;
import xyz.fluxinc.auctionhouse.exceptions.bid.BidTooLowException;
import xyz.fluxinc.auctionhouse.exceptions.authentication.AuthenticationException;
import xyz.fluxinc.auctionhouse.exceptions.authentication.UserExistsException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceNotFoundException;

import javax.swing.*;
import java.rmi.server.ExportException;
import java.util.Arrays;
import java.util.List;


public class Main {
    //-Djava.security.policy=policy.all -Djava.rmi.server.useCodebaseOnly=false

    private static final String DEFAULT_HOST = "localhost";

    public static void main(String[] args) throws SpaceException, AuctionNotFoundException, AuthenticationException, BidTooLowException {
        List<String> arguments = Arrays.asList(args);
        boolean help = arguments.contains("-help");
        boolean clearData = arguments.contains("-clear");
        boolean addDemoData = arguments.contains("-add-demo-data");
        boolean addRoot = arguments.contains("-add-root") || addDemoData;
        String hostname = getHostName(arguments);

        if (help) {
            printHelp();
        } else {
            SystemController systemController = null;
            try {
                systemController = new SystemController(hostname);
            }  catch (SpaceNotFoundException e) {
                JOptionPane.showMessageDialog(null, "A Space was not found at the specified hostname", "An Error Occurred During Initialization", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            } catch (SpaceException ignored) {
                System.exit(-1);
            } catch (ExportException e) {
                e.printStackTrace();
                System.exit(-1);
            }

            if (clearData) {
                System.out.println("Warning: Clearing the system may take a long time. Please Stand By");
                clearAll(new Auction1755082(), "Auction", systemController.getSpaceController());
                clearAll(new AuctionHouse1755082(), "House", systemController.getSpaceController());
                clearAll(new AuctionHouseLock1755082(), "Lock", systemController.getSpaceController());
                clearAll(new User1755082(), "User", systemController.getSpaceController());
                clearAll(new Bid1755082(), "Bid", systemController.getSpaceController());
                System.out.println("Cleared all data from system!");
                System.exit(0);
            } else {
                if (addRoot) {
                    try { systemController.getAuthenticationController().register("root", "root", "818-555-1423"); }
                    catch (UserExistsException | SpaceException ignored) {}
                }

                if (addDemoData) { registerDemoData(systemController.getAuctionHouseController()); }

                systemController.getUserInterfaceController().showLoginScreen();
            }
        }
    }

    private static String getHostName(List<String> arguments) {
        if (arguments.contains("-host")) {
            int index = arguments.indexOf("-host");
            try {
                return arguments.get(index + 1);
            } catch (IndexOutOfBoundsException e) {
                return DEFAULT_HOST;
            }
        } else {
            return DEFAULT_HOST;
        }
    }

    private static void registerDemoData(AuctionHouseController aHC) throws SpaceException, AuctionNotFoundException {
        for (int i = 1; i <= 20; i++) {
            Auction1755082 auction = aHC.placeAuction("Test" + i, 1, 1, "root");
            for (int j = 2; j <= 20 +1; j++) {
                aHC.placeBid(auction.auctionId, j, "anon.ymous");
            }
        }
    }

    private static <T extends Entry> void clearAll(T template, String type, SpaceController sC) throws SpaceException {
        List<T> entries;
        do {
            entries = sC.takeAll(template, 100);
            System.out.println(type + ": " + entries.size());
        } while (!entries.isEmpty());
    }

    private static void printHelp() {
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");
        System.out.println("|                                           Welcome to the Auction House System!                                          |");
        System.out.println("|                                   This system was developed by u1755082 Kieran Gateley                                  |");
        System.out.println("|                        This system has a few arguments to make running and using the system easier                      |");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");
        System.out.println("| -clear - Clears all class objects from the system and exits                                                             |");
        System.out.println("| -add-root - Adds the root administrator account (root, root) which can edit the entire system                           |");
        System.out.println("| -add-demo-data - Adds some demonstration data, comprising of bids and auctions                                          |");
        System.out.println("| -host (hostname) - Sets the hostname for the system to connect to. If this isn't set, then " + DEFAULT_HOST + " is used            |");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------");
    }

}
