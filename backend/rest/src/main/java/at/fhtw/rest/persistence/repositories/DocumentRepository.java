package at.fhtw.rest.persistence.repositories;

import at.fhtw.rest.persistence.entity.Document;
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

    private final Map<Long, Document> documents = new HashMap<>();
    public Document saveDocument(Document document){
        documents.put(document.getId(), document);
        return document;
    }
    public Map<Long, Document> findAll() {return documents;}

}
