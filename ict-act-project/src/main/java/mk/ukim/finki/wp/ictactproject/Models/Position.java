package mk.ukim.finki.wp.ictactproject.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PositionType positionType;

    private LocalDate from;

    private LocalDate to;
}
