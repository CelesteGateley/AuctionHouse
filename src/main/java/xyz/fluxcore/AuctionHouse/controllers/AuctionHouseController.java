package xyz.fluxcore.AuctionHouse.controllers;

import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
import xyz.fluxcore.AuctionHouse.entries.User;
import xyz.fluxcore.AuctionHouse.exceptions.SpaceException;
import xyz.fluxcore.AuctionHouse.exceptions.SpaceNotFoundException;
import xyz.fluxcore.AuctionHouse.exceptions.authentication.AuthenticationException;
import xyz.fluxcore.AuctionHouse.exceptions.authentication.UserExistsException;
import xyz.fluxcore.AuctionHouse.exceptions.authentication.UserNotFoundException;

import java.util.ArrayList;
import java.util.Collection;

import static xyz.fluxcore.AuctionHouse.controllers.SpaceController.ONE_DAY;

public class AuctionHouseController {

    private SpaceController spaceController;
    private User currentUser;

    public AuctionHouseController(String spaceUrl) throws SpaceException, SpaceNotFoundException {
        this.currentUser = null;
        this.spaceController = new SpaceController(spaceUrl);
    }

    public void login(String username, String password) throws SpaceException, UserNotFoundException, AuthenticationException {
        User expectedUser = fetchUser(username);
        if (expectedUser == null) { throw new UserNotFoundException("A User with that name was not found on the system."); }
        if (expectedUser.checkPassword(password)) {
            this.currentUser = expectedUser;
        } else { throw new AuthenticationException("The password specified does not match the expected password"); }
    }

    private User fetchUser(String username) throws SpaceException {
        User template = new User();
        template.username = username;
        return (User) spaceController.read(template);
    }

    public void register(String username, String password) throws SpaceException, UserExistsException {
        if (fetchUser(username) != null) { throw new UserExistsException("A user with that name already exists."); }
        User user = new User(username, password);
        this.currentUser = user;
        spaceController.put(user, ONE_DAY * 7);
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

    public Collection<User> getAllUsers() throws SpaceException {
        Collection<Entry> entries = spaceController.readAll(new User());
        Collection<User> users = new ArrayList<>();
        for (Entry entry : entries) { users.add((User) entry); }
        return users;
    }
}
