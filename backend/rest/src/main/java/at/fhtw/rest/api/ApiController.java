package at.fhtw.rest.api;


import at.fhtw.rest.persistence.entity.Document;
import at.fhtw.rest.persistence.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "document")
@CrossOrigin(origins = "*")
public class ApiController {

    @Autowired
    private DocumentRepository repository;


    @GetMapping("/all")
    public Map<Long, Document> getAllDocuments() {
        return repository.findAll();
    }

    @PostMapping("/{document}")
    public ResponseEntity<Document> uploadDocument(@PathVariable String document){
        Document inserted = repository.saveDocument(Document.builder()
                .id(repository.nextId())
                .name(document)
                .build());

        return new ResponseEntity<>(inserted, HttpStatus.OK);
    }
}
