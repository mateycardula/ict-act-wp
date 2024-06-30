package mk.ukim.finki.wp.ictactproject.Models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Comparator;

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

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public Position(PositionType positionType, LocalDate fromDate) {
        this.positionType = positionType;
        this.fromDate = fromDate;
    }

    public Position(PositionType positionType, LocalDate fromDate, Member member) {
        this.positionType = positionType;
        this.fromDate = fromDate;
        this.member = member;
    }

    public Position(PositionType positionType, LocalDate dateFrom, LocalDate dateTo, Member member) {
        this.positionType = positionType;
        this.fromDate = dateFrom;
        this.toDate = dateTo;
        this.member = member;
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

    public static final Comparator<Position> COMPARATOR = Comparator
            .comparing(Position::getFromDate);
}
