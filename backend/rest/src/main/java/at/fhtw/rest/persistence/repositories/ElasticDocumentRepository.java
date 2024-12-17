package at.fhtw.rest.elasticsearch;

import at.fhtw.rest.persistence.entity.ElasticDocumentEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ElasticDocumentRepository extends ElasticsearchRepository<ElasticDocumentEntity, Long> {
    List<ElasticDocumentEntity> findByNameContaining(String name);
}
