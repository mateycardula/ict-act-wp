package mk.ukim.finki.wp.ictactproject.Models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PositionType positionType;

    private LocalDate fromDate;

    private LocalDate toDate;

    public Position(PositionType positionType, LocalDate fromDate) {
        this.positionType = positionType;
        this.fromDate = fromDate;
        toDate = null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(positionType.name()).append(" (").append(fromDate).append(" - ");
        if (toDate != null) {
            sb.append(toDate);
        } else {
            sb.append(" ");
        }
        sb.append(")");
        return sb.toString();
    }
}
