package xyz.fluxinc.auctionhouse.entries.authentication;

import net.jini.core.entry.Entry;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

public class User1755082 implements Entry {

    public String username;
    public String password;
    public List<Integer> watchedLots;
    public String contactInfo;

     public User1755082() {}

     public User1755082(String username) {
         this.username = username;
     }

     public User1755082(String username, String password) {
         this.username = username;
         this.password = hashPassword(password);
         watchedLots = new ArrayList<>();
     }

     public User1755082(String username, String password, String contactInfo) {
         this(username, password);
         this.contactInfo = contactInfo;
     }

     private String hashPassword(String password) {
         return BCrypt.hashpw(password, BCrypt.gensalt());
     }

     public void setPassword(String password) { this.password = hashPassword(password); }

     public boolean checkPassword(String password) {
         return BCrypt.checkpw(password, this.password);
     }

     public void watchLot(int lotId) { if (!watchedLots.contains(lotId)) { watchedLots.add(lotId); }}

     public void unwatchLot(int lotId) { if (watchedLots.contains(lotId)) { watchedLots.remove(lotId); }}

     public List<Integer> getWatchedLots() { return this.watchedLots; }
}
