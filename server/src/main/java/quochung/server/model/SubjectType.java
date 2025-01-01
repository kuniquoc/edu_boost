package quochung.server.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subject_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
