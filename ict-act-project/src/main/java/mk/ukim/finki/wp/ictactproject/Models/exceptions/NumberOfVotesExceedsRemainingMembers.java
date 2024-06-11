package mk.ukim.finki.wp.ictactproject.Models.exceptions;

public class NumberOfVotesExceedsRemainingMembers extends RuntimeException {
    public NumberOfVotesExceedsRemainingMembers() {
        super("The number of votes you've entered exceeds the number or members that haven't voted.");
    }
}
