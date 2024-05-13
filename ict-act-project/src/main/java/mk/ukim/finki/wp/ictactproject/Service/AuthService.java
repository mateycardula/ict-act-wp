package mk.ukim.finki.wp.ictactproject.Service;

import mk.ukim.finki.wp.ictactproject.Models.Member;

public interface AuthService {
    Member login(String username, String password);
}
