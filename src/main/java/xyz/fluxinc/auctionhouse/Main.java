package xyz.fluxinc.auctionhouse;

import xyz.fluxinc.auctionhouse.controllers.SystemController;
import xyz.fluxinc.auctionhouse.exceptions.authentication.UserExistsException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceNotFoundException;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;


public class Main {
    //-Djava.security.policy=policy.all -Djava.rmi.server.useCodebaseOnly=false

    private static final String DEFAULT_HOST = "localhost";

    public static void main(String[] args) {
        List<String> arguments = Arrays.asList(args);
        boolean addRoot = arguments.contains("-add-root");
        boolean anonymous = arguments.contains("-anonymous");
        boolean help = arguments.contains("-help");
        String hostname = getHostName(arguments);

        if (help) {
            System.out.println("---------------------------------------------------------------------------------------------------------------------------");
            System.out.println("|                                           Welcome to the Auction House System!                                          |");
            System.out.println("|                                   This system was developed by u1755082 Kieran Gateley                                  |");
            System.out.println("|                        This system has a few arguments to make running and using the system easier                      |");
            System.out.println("---------------------------------------------------------------------------------------------------------------------------");
            System.out.println("| -anonymous - Runs the system without logging in. No changes can be made but the system can be viewed                    |");
            System.out.println("| -add-root - Adds the root administrator account (root, root) which can edit the entire system                           |");
            System.out.println("| -host (hostname) - Sets the hostname for the system to connect to. If this isn't set, then " + DEFAULT_HOST + " is used            |");
            System.out.println("---------------------------------------------------------------------------------------------------------------------------");
        } else {
            SystemController systemController = null;
            try {
                systemController = new SystemController(hostname);
            }  catch (SpaceNotFoundException e) {
                JOptionPane.showMessageDialog(null, "A Space was not found at the specified hostname", "An Error Occurred During Initialization", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            } catch (SpaceException ignored) { System.exit(0); }

            if (addRoot) {
                try { systemController.getAuthenticationController().registerAdministrator("root", "root"); }
                catch(UserExistsException | SpaceException ignored) {}
            }

            systemController.getUserInterfaceController().showLoginScreen();
        }

        //System.exit(0);

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

}
