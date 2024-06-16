package mk.ukim.finki.wp.ictactproject.Models.exceptions;

public class DiscussionPointNotVotable extends RuntimeException{
    public DiscussionPointNotVotable(){
        super("This discussion point is not votable");
    }
}
