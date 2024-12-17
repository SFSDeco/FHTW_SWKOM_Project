package at.fhtw.rest.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "documents")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ElasticDocumentEntity {
    @Id
    private Long id;

    private String name;
    private String content;
}
