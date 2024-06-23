package mk.ukim.finki.wp.ictactproject.Models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
@NoArgsConstructor
@Entity
public class Member implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private String name;
    @Setter
    private String surname;

    @Setter
    private String institution;

    @Setter
    @OneToMany (mappedBy = "member")
    private List<Position> positions;

    @Setter
    private String email;

    @Setter
    private String password;

    @Enumerated(EnumType.STRING)
    private PositionType role;

    private String verificationCode;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus attendanceStatus;

    private boolean isAccountNonExpired = true;
    private boolean isAccountNonLocked = true;
    private boolean isCredentialsNonExpired = true;
    private boolean isEnabled = true;

    public Member(String email, String password, String name, String surname, String institution, PositionType role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.institution = institution;
        this.role = role;
        positions = new ArrayList<>();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(role);
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setRole(PositionType role) {
        this.role = role;
        isEnabled = role != PositionType.NEW_USER;
    }

    public static final Comparator<Member> SORT_BY_NAME = Comparator
            .comparing(Member::getName);


}
