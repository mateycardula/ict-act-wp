package mk.ukim.finki.wp.ictactproject.Models;

import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

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

    public static List<PositionType> excludeRoles(List<PositionType> roles) {
        List<PositionType> list = new ArrayList<PositionType>(List.of(PositionType.values()));
        list.removeAll(roles);
        return list;
    }
}
