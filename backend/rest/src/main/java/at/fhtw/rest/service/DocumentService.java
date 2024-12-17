package at.fhtw.rest.service;
import at.fhtw.rest.persistence.entity.DocumentEntity;
import at.fhtw.rest.persistence.repositories.DocumentRepository;
import at.fhtw.rest.service.dtos.DocumentDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface DocumentService {
    DocumentDto saveDocument(String documentDto, MultipartFile file); //für file ändern
    List<DocumentDto> getAllDocuments();
    ResponseEntity<byte[]> getFile(Long id);
    void deleteDocument(Long i);


}