package xyz.fluxinc.AuctionHouse.controllers;

import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
import xyz.fluxinc.AuctionHouse.entries.User;
import xyz.fluxinc.AuctionHouse.exceptions.space.SpaceException;
import xyz.fluxinc.AuctionHouse.exceptions.authentication.AuthenticationException;
import xyz.fluxinc.AuctionHouse.exceptions.authentication.UserExistsException;
import xyz.fluxinc.AuctionHouse.exceptions.authentication.UserNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static xyz.fluxinc.AuctionHouse.controllers.SpaceController.degenerifyCollection;

public class AuthenticationController {

    private SpaceController spaceController;
    private User currentUser = null;

    public AuthenticationController(SpaceController spaceController) {
        this.spaceController = spaceController;
    }

    public void login(String username, String password) throws SpaceException, UserNotFoundException, AuthenticationException {
        User expectedUser = fetchUser(username);
        if (expectedUser == null) { throw new UserNotFoundException("A User with that name was not found on the system."); }
        if (expectedUser.checkPassword(password)) {
            this.currentUser = expectedUser;
        } else { throw new AuthenticationException("The password specified does not match the expected password"); }
    }

    private User fetchUser(String username) throws SpaceException {
        User template = new User(username);
        return (User) spaceController.read(template);
    }

    public User deleteUser(String username) throws SpaceException {
        User template = new User(username);
        return (User) spaceController.take(template);
    }

    public void register(String username, String password) throws SpaceException, UserExistsException {
        if (fetchUser(username) != null) { throw new UserExistsException("A user with that name already exists."); }
        User user = new User(username, password);
        this.currentUser = user;
        spaceController.put(user, SpaceController.ONE_DAY * 7);
    }

    public void registerAdministrator(String username, String password) throws SpaceException, UserExistsException {
        if (fetchUser(username) != null) { throw new UserExistsException("A user with that name already exists."); }
        User user = new User(username, password, true);
        this.currentUser = user;
        spaceController.put(user, Lease.FOREVER);
        System.out.println("Successfully registered as: " + currentUser.username);
    }

    public void logout() {
        this.currentUser = null;
    }

    public List<User> getAllUsers() throws SpaceException {
        Collection<Entry> entries = spaceController.readAll(new User());
        return degenerifyCollection(entries);
    }

    public String getUsername() {
        return currentUser == null ? "anonymous" : currentUser.username;
    }

}
