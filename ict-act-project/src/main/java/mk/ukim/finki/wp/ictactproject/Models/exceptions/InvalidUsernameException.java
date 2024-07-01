package mk.ukim.finki.wp.ictactproject.Models.exceptions;

public class InvalidUsernameException extends RuntimeException {
    public InvalidUsernameException() {
        super("Insert valid email address!");
    }
}
