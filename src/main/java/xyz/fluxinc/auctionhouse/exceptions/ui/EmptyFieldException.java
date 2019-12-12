package xyz.fluxinc.auctionhouse.exceptions.ui;

public class EmptyFieldException extends Exception {

    public EmptyFieldException(String e) {
        super(e);
    }

    public EmptyFieldException(Exception e) {
        super(e);
    }
}
