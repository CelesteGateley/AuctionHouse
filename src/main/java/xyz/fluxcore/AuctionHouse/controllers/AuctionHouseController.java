package xyz.fluxcore.AuctionHouse.controllers;

import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import net.jini.entry.UnusableEntriesException;
import net.jini.space.JavaSpace05;
import xyz.fluxcore.AuctionHouse.entries.User;
import xyz.fluxcore.AuctionHouse.exceptions.authentication.AuthenticationException;
import xyz.fluxcore.AuctionHouse.exceptions.SpaceException;
import xyz.fluxcore.AuctionHouse.exceptions.SpaceNotFoundException;
import xyz.fluxcore.AuctionHouse.exceptions.authentication.UserExistsException;
import xyz.fluxcore.AuctionHouse.exceptions.authentication.UserNotFoundException;

import java.rmi.RemoteException;
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
        System.out.println("Successfully logged in as: " + currentUser.username);
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
        System.out.println("Successfully registered as: " + currentUser.username);
    }

    public void logout() {
        this.currentUser = null;
    }

    public Collection<User> getAllUsers() {
        Collection<User> templates = new ArrayList<>();
        Collection<User> users = new ArrayList<>();
        User template = new User();
        templates.add(template);
        JavaSpace05 space05 = (JavaSpace05) spaceController.getSpace();
        try {
            Collection<?> results = (Collection<?>) space05.take(templates, null, 5000, 10);
            for (Object result : results) {
                users.add((User) result);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (UnusableEntriesException e) {
            e.printStackTrace();
        } catch (TransactionException e) {
            e.printStackTrace();
        }
        return users;
    }
}
