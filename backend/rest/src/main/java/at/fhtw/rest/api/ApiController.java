package at.fhtw.rest.api;


import at.fhtw.rest.persistence.entity.DocumentEntity;
import at.fhtw.rest.service.DocumentService;
import at.fhtw.rest.service.dtos.DocumentDto;
import at.fhtw.rest.service.rabbitmq.DocumentProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<DocumentEntity> uploadDocument(@PathVariable("document") String documentName,
                                                         @RequestParam("file") MultipartFile file) {

        documentService.saveDocument(documentName, file);
        documentProducer.sendDocumentEvent("Document created: " + documentName);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
