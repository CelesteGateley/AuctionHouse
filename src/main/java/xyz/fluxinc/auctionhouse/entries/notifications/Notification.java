package xyz.fluxinc.auctionhouse.entries.notifications;

public class Notification<T> {

    private T target;
    private NotificationType type;
    private boolean read;

    public Notification(NotificationType type, T target) {
        this.type = type;
        this.target = target;
        this.read = false;
    }

    public NotificationType getType() { return type; }

    public T getAuction() { return target; }

    public boolean isRead() { return read; }

    public void setRead(boolean read) { this.read = read; }
}

