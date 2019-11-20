package xyz.fluxcore.AuctionHouse.controllers;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;
import net.jini.space.JavaSpace05;
import xyz.fluxcore.AuctionHouse.exceptions.SpaceException;
import xyz.fluxcore.AuctionHouse.exceptions.SpaceNotFoundException;

import java.rmi.RemoteException;

public class SpaceController {

    private String jsURL;
    private JavaSpace javaSpace;
    private JavaSpace05 javaSpace05;
    private TransactionManager transactionManager;

    public static final long ONE_SECOND = 1000;
    public static final long ONE_MINUTE = ONE_SECOND*60;
    public static final long ONE_HOUR = ONE_MINUTE*60;
    public static final long ONE_DAY = ONE_HOUR*24;

    public SpaceController(String url) throws SpaceNotFoundException, SpaceException {
        this.jsURL = "jini://" + url;
        if (System.getSecurityManager() == null) { System.setSecurityManager(new SecurityManager()); }
        initialize();

        if (javaSpace == null) { throw new SpaceNotFoundException("A Space could not be found at the url: " + jsURL); }
        if (transactionManager == null) { throw new SpaceException("A TransactionManager could not be created"); }
        this.javaSpace05 = (JavaSpace05) javaSpace;
    }

    private void initialize() throws SpaceException {
        JavaSpace space;
        TransactionManager tm;
        try {
            LookupLocator l = new LookupLocator(jsURL);
            ServiceRegistrar sr = l.getRegistrar();

            Class[] spaceTemplate = {Class.forName("net.jini.space.JavaSpace")};
            space = (JavaSpace) sr.lookup(new ServiceTemplate(null, spaceTemplate, null));

            Class[] tmTemplate = {Class.forName("net.jini.core.transaction.server.TransactionManager")};
            tm = (TransactionManager) sr.lookup(new ServiceTemplate(null, tmTemplate, null));

        } catch (Exception e) { throw new SpaceException(e.getMessage()); }

        this.javaSpace = space;
        this.transactionManager = tm;
    }

    public JavaSpace getSpace() { return this.javaSpace; }

    public TransactionManager getManager() { return this.transactionManager; }

    public Entry take(Entry template) throws SpaceException {
        try { return javaSpace.take(template, null, ONE_SECOND*5); }
        catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException e) { throw new SpaceException(e.getLocalizedMessage()); }
    }

    public Entry take(Entry template, long timeout) throws SpaceException {
        try { return javaSpace.readIfExists(template, null, timeout); }
        catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException e) { throw new SpaceException(e.getLocalizedMessage()); }
    }

    public Entry take(Entry template, Transaction transaction, long timeout) throws SpaceException {
        try { return javaSpace.readIfExists(template, transaction, timeout); }
        catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException e) { throw new SpaceException(e.getLocalizedMessage()); }
    }

    public Entry read(Entry template) throws SpaceException {
        try { return javaSpace.read(template, null, ONE_SECOND*5); }
        catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException e) { throw new SpaceException(e.getLocalizedMessage()); }
    }

    public Entry read(Entry template, long timeout) throws SpaceException {
        try { return javaSpace.read(template, null, timeout); }
        catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException e) { throw new SpaceException(e.getLocalizedMessage()); }
    }

    public Entry read(Entry template, Transaction transaction, long timeout) throws SpaceException {
        try { return javaSpace.read(template, transaction, timeout); }
        catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException e) { throw new SpaceException(e.getLocalizedMessage()); }
    }

    public Lease put(Entry template) throws SpaceException {
        try { return javaSpace.write(template, null, ONE_HOUR); }
        catch (TransactionException | RemoteException e) { throw new SpaceException(e.getLocalizedMessage()); }
    }

    public Lease put(Entry template, long leaseTime) throws SpaceException {
        try { return javaSpace.write(template, null, leaseTime); }
        catch (TransactionException | RemoteException e) { throw new SpaceException(e.getLocalizedMessage()); }
    }

    public Lease put(Entry template, Transaction transaction, long leaseTime) throws SpaceException {
        try { return javaSpace.write(template, transaction, leaseTime); }
        catch (TransactionException | RemoteException e) { throw new SpaceException(e.getLocalizedMessage()); }
    }

}
