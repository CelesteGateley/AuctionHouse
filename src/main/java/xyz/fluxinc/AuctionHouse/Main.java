package xyz.fluxinc.AuctionHouse;

import xyz.fluxinc.AuctionHouse.controllers.AuctionHouseController;
import xyz.fluxinc.AuctionHouse.controllers.AuthenticationController;
import xyz.fluxinc.AuctionHouse.controllers.SystemController;
import xyz.fluxinc.AuctionHouse.controllers.UserInterfaceController;
import xyz.fluxinc.AuctionHouse.exceptions.SpaceException;
import xyz.fluxinc.AuctionHouse.exceptions.SpaceNotFoundException;
import xyz.fluxinc.AuctionHouse.exceptions.authentication.UserExistsException;

import java.util.Arrays;
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

        systemController.getUserInterfaceController().showLoginScreen();
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
