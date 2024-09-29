package at.fhtw.rest.persistence.repositories;

import at.fhtw.rest.persistence.entity.DocumentEntity;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class DocumentRepository {
    private static Long nextId= 0L;

    public Long nextId(){
        Long currentId = nextId;

        nextId = nextId+1;

        return currentId;
    }

    private final List<DocumentEntity> documents = new ArrayList<>();
    public DocumentEntity saveDocument(DocumentEntity documentEntity){
        documents.add(documentEntity);
        return documentEntity;
    }
    public List<DocumentEntity> findAll() {return documents;}

}
