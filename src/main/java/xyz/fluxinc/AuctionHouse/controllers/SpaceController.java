package xyz.fluxinc.AuctionHouse.controllers;

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
import net.jini.space.MatchSet;
import xyz.fluxinc.AuctionHouse.exceptions.SpaceException;
import xyz.fluxinc.AuctionHouse.exceptions.SpaceNotFoundException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("unchecked")
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


    }

    private void initialize() throws SpaceException, SpaceNotFoundException {
        JavaSpace space;
        TransactionManager tm;
        try {
            LookupLocator l = new LookupLocator(jsURL);
            ServiceRegistrar sr = l.getRegistrar();

            Class[] spaceTemplate = {Class.forName("net.jini.space.JavaSpace")};
            space = (JavaSpace) sr.lookup(new ServiceTemplate(null, spaceTemplate, null));

            Class[] tmTemplate = {Class.forName("net.jini.core.transaction.server.TransactionManager")};
            tm = (TransactionManager) sr.lookup(new ServiceTemplate(null, tmTemplate, null));

        } catch (Exception e) { throw new SpaceException(e); }

        this.javaSpace = space;
        this.transactionManager = tm;

        if (javaSpace == null) { throw new SpaceNotFoundException("A Space could not be found at the url: " + jsURL); }
        if (transactionManager == null) { throw new SpaceException("A TransactionManager could not be created"); }
        this.javaSpace05 = (JavaSpace05) javaSpace;
    }

    public JavaSpace getSpace() { return this.javaSpace; }

    public TransactionManager getManager() { return this.transactionManager; }

    public void changeSpaceUrl(String url) throws SpaceException, SpaceNotFoundException {
        jsURL = url;
        initialize();
    }

    public Entry take(Entry template) throws SpaceException {
        return take(template, null, ONE_SECOND*5);
    }

    public Entry take(Entry template, long timeout) throws SpaceException {
        return take(template, null, timeout);
    }

    public Entry take(Entry template, Transaction transaction, long timeout) throws SpaceException {
        try { return javaSpace.take(template, transaction, timeout); }
        catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException e) { throw new SpaceException(e); }
    }

    public Entry read(Entry template) throws SpaceException {
        return read(template, null, ONE_SECOND*5);
    }

    public Entry read(Entry template, long timeout) throws SpaceException {
        return read(template, null, timeout);
    }

    public Entry read(Entry template, Transaction transaction, long timeout) throws SpaceException {
        try { return javaSpace.read(template, transaction, timeout); }
        catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException e) { throw new SpaceException(e); }
    }

    public Collection<Entry> readAll(Entry template) throws SpaceException {
        return readAll(template, null, ONE_SECOND*5, 10);
    }

    public Collection<Entry> readAll(Entry template, int count) throws SpaceException {
        return readAll(template, null, ONE_SECOND*5, count);
    }

    public Collection<Entry> readAll(Entry template, long timeout) throws SpaceException {
        return readAll(template, null, timeout, 10);
    }

    public Collection<Entry> readAll(Entry template, Transaction transaction, long timeout) throws SpaceException {
        return readAll(template, transaction, timeout, 10);
    }

    public Collection<Entry> readAll(Entry template, Transaction transaction, long timeout, int count) throws SpaceException {
        Collection<Entry> templates = new ArrayList<>();
        Collection<Entry> results = new ArrayList<>();
        templates.add(template);
        try {
            MatchSet res = javaSpace05.contents(templates, transaction, timeout, count);


            Entry result = res.next();
            while (result != null){
                results.add(result);
                result = res.next();
            }
            return results;
        }
        catch (Exception e) { throw new SpaceException(e); }
    }

    public Collection<Entry> takeAll(Entry template) throws SpaceException {
        return takeAll(template, null, ONE_SECOND*5, 10);
    }

    public Collection<Entry> takeAll(Entry template, int count) throws SpaceException {
        return takeAll(template, null, ONE_SECOND*5, count);
    }

    public Collection<Entry> takeAll(Entry template, long timeout) throws SpaceException {
        return takeAll(template, null, timeout, 10);
    }

    public Collection<Entry> takeAll(Entry template, Transaction transaction, long timeout) throws SpaceException {
        return takeAll(template, transaction, timeout, 10);
    }

    public Collection<Entry> takeAll(Entry template, Transaction transaction, long timeout, int count) throws SpaceException {
        Collection<Entry> templates = new ArrayList<>();
        templates.add(template);
        try { return (Collection<Entry>) javaSpace05.take(templates, transaction, timeout, count); }
        catch (Exception e) { throw new SpaceException(e); }
    }

    public Lease put(Entry template) throws SpaceException {
        return put(template, null, ONE_HOUR);
    }

    public Lease put(Entry template, long leaseTime) throws SpaceException {
        return put(template, null, leaseTime);
    }

    public Lease put(Entry template, Transaction transaction, long leaseTime) throws SpaceException {
        try { return javaSpace.write(template, transaction, leaseTime); }
        catch (TransactionException | RemoteException e) { throw new SpaceException(e); }
    }

}
