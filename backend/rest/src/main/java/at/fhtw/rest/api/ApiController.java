package at.fhtw.rest.api;


import at.fhtw.rest.persistence.entity.DocumentEntity;
import at.fhtw.rest.persistence.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping(path = "document")
@CrossOrigin(origins = "*")
public class ApiController {

    @Autowired
    private DocumentRepository repository;


    @GetMapping("/all")
    public List<DocumentEntity> getAllDocuments() {
        return repository.findAll();
    }

    @PostMapping("/{document}")
    public ResponseEntity<DocumentEntity> uploadDocument(@PathVariable String document){
        DocumentEntity inserted = repository.saveDocument(DocumentEntity.builder()
                .id(repository.nextId())
                .name(document)
                .build());

        return new ResponseEntity<>(inserted, HttpStatus.OK);
    }
}
