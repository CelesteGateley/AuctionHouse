package xyz.fluxinc.AuctionHouse.entries;

import net.jini.core.entry.Entry;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

public class u1755082User implements Entry {

    public String username;
    public String password;
    public List<Integer> watchedLots;
    public Boolean isAdministrator;

     public u1755082User() {}

     public u1755082User(String username) {
         this.username = username;
     }

     public u1755082User(String username, String password) {
         this.username = username;
         this.password = hashPassword(password);
         watchedLots = new ArrayList<>();
     }

     public u1755082User(String username, String password, boolean isAdministrator) {
         this(username, password);
         this.isAdministrator = isAdministrator;
     }

     private String hashPassword(String password) {
         return BCrypt.hashpw(password, BCrypt.gensalt());
     }

     public String getPassword() { return this.password; }

     public boolean checkPassword(String password) {
         return BCrypt.checkpw(password, this.password);
     }

     public void watchLot(int lotId) { if (!watchedLots.contains(lotId)) { watchedLots.add(lotId); }}

     public void unwatchLot(int lotId) { if (watchedLots.contains(lotId)) { watchedLots.remove(lotId); }}

     public List<Integer> getWatchedLots() { return this.watchedLots; }
}
