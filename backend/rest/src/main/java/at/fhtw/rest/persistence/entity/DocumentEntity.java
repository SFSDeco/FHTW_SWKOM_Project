package at.fhtw.rest.persistence.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Entity
@Table(name = "documents")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "Document has to have a name")
    private String name;

    @Column(name = "content")
    @NotBlank(message = "Document has to have some content")
    private String content;
}
