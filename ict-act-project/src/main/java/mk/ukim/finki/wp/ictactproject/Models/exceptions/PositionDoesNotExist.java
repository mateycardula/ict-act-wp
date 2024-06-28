package mk.ukim.finki.wp.ictactproject.Models.exceptions;

public class PositionDoesNotExist extends RuntimeException {
    public PositionDoesNotExist() {
        super("Position doesn't exist");
    }
}
