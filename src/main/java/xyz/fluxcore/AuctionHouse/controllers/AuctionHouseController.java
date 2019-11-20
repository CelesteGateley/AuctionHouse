package xyz.fluxcore.AuctionHouse.controllers;

import xyz.fluxcore.AuctionHouse.entries.User;
import xyz.fluxcore.AuctionHouse.exceptions.AuthenticationException;
import xyz.fluxcore.AuctionHouse.exceptions.SpaceException;
import xyz.fluxcore.AuctionHouse.exceptions.SpaceNotFoundException;
import xyz.fluxcore.AuctionHouse.exceptions.UserNotFoundException;

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

    public void register(String username, String password) throws SpaceException {
        User user = new User(username, password);
        this.currentUser = user;
        spaceController.put(user);
    }

    public void logout() {
        this.currentUser = null;
    }
}
