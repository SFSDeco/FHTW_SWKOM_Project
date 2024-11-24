package at.fhtw.rest.service;
import at.fhtw.rest.persistence.entity.DocumentEntity;
import at.fhtw.rest.persistence.repositories.DocumentRepository;
import at.fhtw.rest.service.dtos.DocumentDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface DocumentService {
    void saveDocument(String documentDto, MultipartFile file); //für file ändern
    List<DocumentDto> getAllDocuments();

}
