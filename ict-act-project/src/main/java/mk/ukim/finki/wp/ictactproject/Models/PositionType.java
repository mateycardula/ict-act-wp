package mk.ukim.finki.wp.ictactproject.Models;

import org.springframework.security.core.GrantedAuthority;

public enum PositionType implements GrantedAuthority {
    PRESIDENT,
    VICE_PRESIDENT,
    BOARD_MEMBER,
    FINANCE_BOARD,
    MEMBER,
    GUEST,
    NEW_USER;


    @Override
    public String toString() {
        return name();
    }

    @Override
    public String getAuthority() {
        return name();
    }
}
