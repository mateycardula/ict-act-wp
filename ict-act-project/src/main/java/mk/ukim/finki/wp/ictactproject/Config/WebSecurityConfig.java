package mk.ukim.finki.wp.ictactproject.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final CustomUsernamePasswordAuthenticationProvider authProvider;

    public WebSecurityConfig(PasswordEncoder passwordEncoder, CustomUsernamePasswordAuthenticationProvider authProvider) {
        this.passwordEncoder = passwordEncoder;
        this.authProvider = authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/meetings")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/register")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/members")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/members/positions/*")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/h2/**")).permitAll()//todo: bazata ne treba da e javna

                        .requestMatchers(new AntPathRequestMatcher("/members/**"))
                        .hasAnyAuthority("PRESIDENT","VICE_PRESIDENT")
                        .requestMatchers(new AntPathRequestMatcher("/meetings/delete/**"))
                        .hasAnyAuthority("PRESIDENT","VICE_PRESIDENT")
                        .requestMatchers(new AntPathRequestMatcher("/meetings/edit/*"))
                        .hasAnyAuthority("PRESIDENT","VICE_PRESIDENT")
                        .requestMatchers(new AntPathRequestMatcher("/meetings/panel/*"))
                        .hasAnyAuthority("PRESIDENT","VICE_PRESIDENT")
                        .requestMatchers(new AntPathRequestMatcher("/meetings/finish/*"))
                        .hasAnyAuthority("PRESIDENT","VICE_PRESIDENT")
                        .requestMatchers(new AntPathRequestMatcher("/meetings/add-attendees"))
                        .hasAnyAuthority("PRESIDENT","VICE_PRESIDENT")
                        .requestMatchers(new AntPathRequestMatcher("/meetings/add-attendees/*"))
                        .hasAnyAuthority("PRESIDENT","VICE_PRESIDENT")
                        .requestMatchers(new AntPathRequestMatcher("/discussion-point/download/*")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/discussion-point/**"))
                        .hasAnyAuthority("PRESIDENT","VICE_PRESIDENT")

                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        // .loginPage("/login")
                        .permitAll()
                        .failureUrl("/login?error=BadCredentials")
                        .defaultSuccessUrl("/meetings", true)//todo: redirect to home page
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/login")
                )
                .exceptionHandling((ex) -> ex
                        .accessDeniedPage("/access_denied")
                );

        return http.build();
    }

    // In Memory Authentication
//    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user1 = User.builder()
                .username("bojan")
                .password(passwordEncoder.encode("b"))
                .roles("MEMBER")
                .build();

        return new InMemoryUserDetailsManager(user1);
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider);
        return authenticationManagerBuilder.build();
    }


}
