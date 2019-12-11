package xyz.fluxinc.AuctionHouse.listeners;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import xyz.fluxinc.AuctionHouse.controllers.SpaceController;

import java.rmi.RemoteException;

public class AuctionListener implements RemoteEventListener {

   private SpaceController spaceController;

    public AuctionListener(SpaceController spaceController) {
        this.spaceController = spaceController;
    }

    @Override
    public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {

    }
}
