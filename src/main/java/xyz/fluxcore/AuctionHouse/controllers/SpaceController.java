package xyz.fluxcore.AuctionHouse.controllers;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;
import xyz.fluxcore.AuctionHouse.exceptions.SpaceException;
import xyz.fluxcore.AuctionHouse.exceptions.SpaceNotFoundException;

public class SpaceController {

    private String jsURL;
    private JavaSpace javaSpace;
    private TransactionManager transactionManager;

    public SpaceController(String url) throws SpaceNotFoundException, SpaceException {
        this.jsURL = "jini://" + url;
        if (System.getSecurityManager() == null) { System.setSecurityManager(new SecurityManager()); }
        javaSpace = getSpace();
        transactionManager = getManager();

        if (javaSpace == null) { throw new SpaceNotFoundException("A Space could not be found at the url: " + jsURL); }
    }

    private JavaSpace getSpace() throws SpaceException {
        JavaSpace space = null;
        try {
            LookupLocator l = new LookupLocator(jsURL);
            ServiceRegistrar sr = l.getRegistrar();

            Class[] classTemplate = {Class.forName("net.jini.space.JavaSpace")};
            space = (JavaSpace) sr.lookup(new ServiceTemplate(null, classTemplate, null));

        } catch (Exception e) { throw new SpaceException(e.getMessage()); }

        return space;
    }

    private TransactionManager getManager() throws SpaceException {
        TransactionManager tm = null;
        try {
            LookupLocator l = new LookupLocator(jsURL);
            ServiceRegistrar sr = l.getRegistrar();

            Class[] classTemplate = {Class.forName("net.jini.core.transaction.server.TransactionManager")};
            tm = (TransactionManager) sr.lookup(new ServiceTemplate(null, classTemplate, null));
        } catch (Exception e) { throw new SpaceException(e.getMessage()); }
        return tm;
    }



}
