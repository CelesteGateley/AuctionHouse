package xyz.fluxinc.auctionhouse.controllers;

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
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceException;
import xyz.fluxinc.auctionhouse.exceptions.space.SpaceNotFoundException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public <T extends Entry> T take(Entry template) throws SpaceException { return take(template, null, ONE_SECOND*5); }

    public <T extends Entry> T take(Entry template, long timeout) throws SpaceException { return take(template, null, timeout); }

    public <T extends Entry> T take(Entry template, Transaction transaction, long timeout) throws SpaceException {
        try {
            return (T) javaSpace.take(template, transaction, timeout);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException e) {
            throw new SpaceException(e);
        }
    }

    public <T extends Entry> T read(Entry template) throws SpaceException { return read(template, null, ONE_SECOND*5); }

    public <T extends Entry> T read(Entry template, long timeout) throws SpaceException { return read(template, null, timeout); }

    public <T extends Entry> T read(Entry template, Transaction transaction, long timeout) throws SpaceException {
        try {
            return (T) javaSpace.read(template, transaction, timeout);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException e) {
            throw new SpaceException(e);
        }
    }

    public <T extends Entry> List<T> readAll(Entry template) throws SpaceException { return readAll(template, null, ONE_SECOND*5, 10); }

    public <T extends Entry> List<T> readAll(Entry template, int count) throws SpaceException { return readAll(template, null, ONE_SECOND*5, count); }

    public <T extends Entry> List<T> readAll(Entry template, long timeout) throws SpaceException { return readAll(template, null, timeout, 10); }

    public <T extends Entry> List<T> readAll(Entry template, Transaction transaction, long timeout) throws SpaceException { return readAll(template, transaction, timeout, 10); }

    public <T extends Entry> List<T> readAll(Entry template, Transaction transaction, long timeout, int count) throws SpaceException {
        Collection<Entry> templates = new ArrayList<>();
        List<T> results = new ArrayList<>();
        templates.add(template);
        try {
            MatchSet res = javaSpace05.contents(templates, transaction, timeout, count);
            T result = (T) res.next();
            while (result != null){
                results.add(result);
                result = (T) res.next();
            }
            return results;
        }
        catch (Exception e) { throw new SpaceException(e); }
    }

    public <T extends Entry> List<T> takeAll(Entry template) throws SpaceException { return takeAll(template, null, ONE_SECOND*5, 10); }

    public <T extends Entry> List<T> takeAll(Entry template, int count) throws SpaceException { return takeAll(template, null, ONE_SECOND*5, count); }

    public <T extends Entry> List<T> takeAll(Entry template, long timeout) throws SpaceException { return takeAll(template, null, timeout, 10); }

    public <T extends Entry> List<T>takeAll(Entry template, Transaction transaction, long timeout) throws SpaceException { return takeAll(template, transaction, timeout, 10); }

    public <T extends Entry> List<T> takeAll(Entry template, Transaction transaction, long timeout, int count) throws SpaceException {
        Collection<Entry> templates = new ArrayList<>();
        templates.add(template);
        try {
            List<T> results = new ArrayList<>();
            for (Entry entry : (Collection<Entry>) javaSpace05.take(templates, transaction, timeout, count)) { results.add((T) entry); }
            return results;
        } catch (Exception e) { throw new SpaceException(e); }
    }

    public Lease put(Entry template) throws SpaceException { return put(template, null, ONE_HOUR); }

    public Lease put(Entry template, long leaseTime) throws SpaceException { return put(template, null, leaseTime); }

    public Lease put(Entry template, Transaction transaction, long leaseTime) throws SpaceException {
        try {
            return javaSpace.write(template, transaction, leaseTime);
        } catch (TransactionException | RemoteException e) {
            throw new SpaceException(e);
        }
    }

}
