package xyz.fluxcore.AuctionHouse.controllers;

import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
import xyz.fluxcore.AuctionHouse.entries.u1755082User;
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
    private u1755082User currentUser;

    public AuctionHouseController(String spaceUrl) throws SpaceException, SpaceNotFoundException {
        this.currentUser = null;
        this.spaceController = new SpaceController(spaceUrl);
    }

    public void login(String username, String password) throws SpaceException, UserNotFoundException, AuthenticationException {
        u1755082User expectedUser = fetchUser(username);
        if (expectedUser == null) { throw new UserNotFoundException("A User with that name was not found on the system."); }
        if (expectedUser.checkPassword(password)) {
            this.currentUser = expectedUser;
        } else { throw new AuthenticationException("The password specified does not match the expected password"); }
    }

    private u1755082User fetchUser(String username) throws SpaceException {
        u1755082User template = new u1755082User();
        template.username = username;
        return (u1755082User) spaceController.read(template);
    }

    public void register(String username, String password) throws SpaceException, UserExistsException {
        if (fetchUser(username) != null) { throw new UserExistsException("A user with that name already exists."); }
        u1755082User user = new u1755082User(username, password);
        this.currentUser = user;
        spaceController.put(user, ONE_DAY * 7);
    }

    public void registerAdministrator(String username, String password) throws SpaceException, UserExistsException {
        if (fetchUser(username) != null) { throw new UserExistsException("A user with that name already exists."); }
        u1755082User user = new u1755082User(username, password, true);
        this.currentUser = user;
        spaceController.put(user, Lease.FOREVER);
        System.out.println("Successfully registered as: " + currentUser.username);
    }

    public void logout() {
        this.currentUser = null;
    }

    public Collection<u1755082User> getAllUsers() throws SpaceException {
        Collection<Entry> entries = spaceController.readAll(new u1755082User());
        Collection<u1755082User> users = new ArrayList<>();
        for (Entry entry : entries) { users.add((u1755082User) entry); }
        return users;
    }
}
