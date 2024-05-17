package mk.ukim.finki.wp.ictactproject.Models.exceptions;

public class PasswordDoNotMatchException extends RuntimeException {
    public PasswordDoNotMatchException() {
        super("Password do not match");
    }
}
