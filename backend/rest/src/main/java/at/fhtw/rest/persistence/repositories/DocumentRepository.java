package at.fhtw.rest.persistence.repositories;

import at.fhtw.rest.persistence.entity.DocumentEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

@Repository
public interface DocumentRepository extends JpaRepository <DocumentEntity, Long>  {
}
