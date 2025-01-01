package quochung.server.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @ManyToOne
    @JoinColumn(name = "subject_type_id")
    @JsonIgnore
    private SubjectType type;

    @Column(columnDefinition = "TEXT")
    private String detail;

    @ManyToMany(mappedBy = "studyMethods", fetch = FetchType.LAZY)
    private List<Event> events = new ArrayList<>();
}
