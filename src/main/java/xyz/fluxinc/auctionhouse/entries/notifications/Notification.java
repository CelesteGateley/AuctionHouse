package xyz.fluxinc.auctionhouse.entries.notifications;

public class Notification {

    private String auctionName;
    private int auctionId;
    private NotificationType type;
    private boolean read;

    public Notification(NotificationType type, int auctionId, String auctionName) {
        this.type = type;
        this.auctionId = auctionId;
        this.read = false;
        this.auctionName = auctionName;
    }

    public NotificationType getType() { return type; }

    public int getAuctionId() { return auctionId; }

    public boolean isRead() { return read; }

    public void setRead(boolean read) { this.read = read; }

    @Override
    public String toString() {
        switch (type) {
            case AUCTION_CLOSED:
                return "The auction \"" + auctionName + " \" was closed.";
            case AUCTION_LOST:
                return "The auction \"" + auctionName + " \" was lost.";
            case AUCTION_WON:
                return "The auction \"" + auctionName + " \" was won by you!";
            case AUCTION_PURCHASED:
                return "The auction \"" + auctionName + " \" was bought out.";
            case AUCTION_ADDED:
                return "An auction with ID " + auctionId + " was added to the system.";
            case BID_PLACED_OWNED:
                return "A bid was placed on owned auction \"" + auctionName + "\".";
            case BID_PLACED_WATCHED:
                return "A bid was placed on watched auction \"" + auctionName + "\".";
            case BID_ACCEPTED:
                return "A bid was accepted on watched auction \"" + auctionName + "\".";
            default:
                return "A notification has been received";
        }
    }
}

