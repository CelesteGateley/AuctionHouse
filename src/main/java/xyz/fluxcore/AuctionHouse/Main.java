package xyz.fluxcore.AuctionHouse;

import xyz.fluxcore.AuctionHouse.controllers.AuctionHouseController;
import xyz.fluxcore.AuctionHouse.controllers.UserInterfaceController;
import xyz.fluxcore.AuctionHouse.exceptions.SpaceException;
import xyz.fluxcore.AuctionHouse.exceptions.SpaceNotFoundException;
import xyz.fluxcore.AuctionHouse.exceptions.authentication.UserExistsException;

import java.util.Arrays;
import java.util.List;


public class Main {
    //-Djava.security.policy=policy.all -Djava.rmi.server.useCodebaseOnly=false

    public static void main(String[] args) throws SpaceNotFoundException, SpaceException, UserExistsException {
        List<String> arguments = Arrays.asList(args);
        boolean addRoot = arguments.contains("-add-root");
        boolean anonymous = arguments.contains("-anonymous");
        String hostname = getHostName(arguments);

        AuctionHouseController auctionHouseController = new AuctionHouseController(hostname);
        if (addRoot) { auctionHouseController.registerAdministrator("root", "root"); }
        UserInterfaceController userInterfaceController = new UserInterfaceController(auctionHouseController);
        userInterfaceController.showLoginScreen();
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
