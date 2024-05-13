package mk.ukim.finki.wp.ictactproject.Models.exceptions;

public class InvalidEmailOrPasswordException extends RuntimeException {
    public InvalidEmailOrPasswordException() {super("Invalid Username Or Password! Please try again.");
    }
}
