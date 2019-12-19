package xyz.fluxinc.auctionhouse.controllers;

import net.jini.core.lease.Lease;
import xyz.fluxinc.auctionhouse.entries.authentication.User1755082;
import xyz.fluxinc.auctionhouse.exceptions.authentication.AuthenticationException;
import xyz.fluxinc.auctionhouse.exceptions.authentication.UserExistsException;
import xyz.fluxinc.auctionhouse.exceptions.authentication.UserNotFoundException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;

import java.rmi.server.ExportException;
import java.util.List;

public class AuthenticationController {

    private SpaceController spaceController;
    private User1755082 currentUser = null;
    private AuctionHouseController auctionHouseController = null;

    public AuthenticationController(SpaceController spaceController) {
        this.spaceController = spaceController;
    }

    public void login(String username, String password) throws SpaceException, UserNotFoundException, AuthenticationException {
        User1755082 expectedUser = fetchUser(username);
        if (expectedUser == null) { throw new UserNotFoundException("A User with that name was not found on the system."); }
        if (expectedUser.checkPassword(password)) { this.currentUser = expectedUser; }
        else { throw new AuthenticationException("The password specified does not match the expected password"); }
        if (auctionHouseController != null) {
            for (Integer auctionId : expectedUser.watchedLots) {
                try {
                    auctionHouseController.watchAuction(auctionId);
                } catch(ExportException e) { throw new SpaceException(e); }
            }
        }
    }

    public void assignAuctionHouse(AuctionHouseController auctionHouseController) { this.auctionHouseController = auctionHouseController; }

    private User1755082 fetchUser(String username) throws SpaceException { return spaceController.read(new User1755082(username)); }

    public User1755082 deleteUser(String username) throws SpaceException { return spaceController.take(new User1755082(username)); }

    public void register(String username, String password, String contactInfo) throws SpaceException, UserExistsException {
        if (fetchUser(username) != null) { throw new UserExistsException("A user with that name already exists."); }
        User1755082 user = new User1755082(username, password, contactInfo);
        spaceController.put(user, SpaceController.ONE_DAY * 7);
    }


    public void logout() { this.currentUser = null; }

    public List<User1755082> getAllUsers() throws SpaceException { return spaceController.readAll(new User1755082()); }

    public String getUsername() { return currentUser == null ? "anonymous" : currentUser.username; }

    public boolean isLoggedIn() { return currentUser != null; }

    public User1755082 getUser() { return this.currentUser; }
}
