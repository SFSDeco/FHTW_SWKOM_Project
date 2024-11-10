package at.fhtw.rest.api;


import at.fhtw.rest.persistence.entity.DocumentEntity;
import at.fhtw.rest.persistence.repositories.DocumentRepository;
import at.fhtw.rest.service.DocumentService;
import at.fhtw.rest.service.dtos.DocumentDto;
import at.fhtw.rest.service.rabbitmq.DocumentProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping(path = "document")
@CrossOrigin(origins = "*")
public class ApiController {

    private final DocumentProducer documentProducer;

    @Autowired
    public ApiController(DocumentProducer documentProducer){
        this.documentProducer = documentProducer;
    }

    @Autowired
    private DocumentService documentService;


    @GetMapping("/all")
    public List<DocumentDto> getAllDocuments() {
        return documentService.getAllDocuments();
    }

    @PostMapping("/{document}")
    public ResponseEntity<DocumentEntity> uploadDocument(@PathVariable String document) {
        documentService.saveDocument(document);
        documentProducer.sendDocumentEvent("Document created: " + document);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /*@PostMapping("/{document}")
    public ResponseEntity<DocumentEntity> uploadDocument(@PathVariable String document){
        DocumentEntity inserted = repository.saveDocument(DocumentEntity.builder()
                .id(repository.nextId())
                .name(document)
                .build());

        return new ResponseEntity<>(inserted, HttpStatus.OK);
    }*/
}
