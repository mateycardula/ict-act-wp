package mk.ukim.finki.wp.ictactproject.Models.exceptions;

public class InvalidUserCredentialsException extends RuntimeException {
    public InvalidUserCredentialsException() {
        super("Invalid user credentials! Please try again.");
    }
}
