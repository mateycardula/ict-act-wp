package mk.ukim.finki.wp.ictactproject.Models.exceptions;

public class MemberDoesNotExist extends RuntimeException {
    public MemberDoesNotExist() {
        super("Member doesn't exist.");
    }
}
