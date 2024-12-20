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
@CrossOrigin(origins = "http://localhost:8080")
public class ApiController {

    private final DocumentProducer documentProducer;

    private final DocumentService documentService;

    @Autowired
    public ApiController(DocumentProducer documentProducer, DocumentService documentService) {

        this.documentProducer = documentProducer;
        this.documentService = documentService;
    }


    @GetMapping("/all")
    public List<DocumentDto> getAllDocuments() {
        return documentService.getAllDocuments();
    }

    @PostMapping("/{document}")
    public ResponseEntity<DocumentDto> uploadDocument(@PathVariable("document") String documentName,
                                                         @RequestParam("file") MultipartFile file) {

        DocumentDto documentDto = documentService.saveDocument(documentName, file);

        documentProducer.sendDocumentEvent(documentDto);
        return new ResponseEntity<>(documentDto, HttpStatus.CREATED);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        return documentService.getFile(id);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateDocument(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        documentService.updateDocument(id, name, file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/file/{id}")
    public ResponseEntity<DocumentDto> getDocument(@PathVariable Long id) {
        return documentService.getDocument(id);
    }
}
