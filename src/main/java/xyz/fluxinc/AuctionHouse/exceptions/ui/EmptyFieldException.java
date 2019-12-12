package xyz.fluxinc.AuctionHouse.exceptions.ui;

public class EmptyFieldException extends Exception {

    public EmptyFieldException(String e) {
        super(e);
    }

    public EmptyFieldException(Exception e) {
        super(e);
    }
}
