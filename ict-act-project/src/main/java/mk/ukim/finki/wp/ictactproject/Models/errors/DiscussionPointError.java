package mk.ukim.finki.wp.ictactproject.Models.errors;

import mk.ukim.finki.wp.ictactproject.Models.DiscussionPoint;

public class DiscussionPointError {
    public Long discussionPoint;
    public String errorMessage;
    public String type;

    public DiscussionPointError(Long discussionPoint, String errorMessage, String type) {
        this.discussionPoint = discussionPoint;
        this.errorMessage = errorMessage;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getDiscussionPoint() {
        return discussionPoint;
    }

    public void setDiscussionPoint(Long discussionPoint) {
        this.discussionPoint = discussionPoint;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
