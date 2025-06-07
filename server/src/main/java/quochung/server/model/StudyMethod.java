package quochung.server.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "study_methods")
@Data
@NoArgsConstructor
public class StudyMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String thumbnail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_type_id")
    private SubjectType type;

    @Column(columnDefinition = "TEXT")
    private String detail;
}
