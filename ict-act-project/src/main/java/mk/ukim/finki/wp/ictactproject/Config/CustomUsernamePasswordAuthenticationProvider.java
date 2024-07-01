package mk.ukim.finki.wp.ictactproject.Config;

import mk.ukim.finki.wp.ictactproject.Service.MemberService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomUsernamePasswordAuthenticationProvider implements AuthenticationProvider {
    private final MemberService userService;
    private final PasswordEncoder passwordEncoder;

    public CustomUsernamePasswordAuthenticationProvider(MemberService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (username.isEmpty() || password.isEmpty()) {
            throw new BadCredentialsException("Empty credentials!");
        }

        UserDetails userDetails;
        try {
            userDetails = this.userService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("Username not found!");
        }

        if (!userDetails.isEnabled()) {
            throw new BadCredentialsException("Your account isn't approved yet!");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Password is incorrect!");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
