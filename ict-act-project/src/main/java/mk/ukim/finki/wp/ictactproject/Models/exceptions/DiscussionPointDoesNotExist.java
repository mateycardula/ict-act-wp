package mk.ukim.finki.wp.ictactproject.Models.exceptions;

public class DiscussionPointDoesNotExist extends RuntimeException{
    public DiscussionPointDoesNotExist() {
        super("Discussion point doesn't exist");
    }
}
