package at.fhtw.rest.service;
import at.fhtw.rest.persistence.entity.DocumentEntity;
import at.fhtw.rest.persistence.repositories.DocumentRepository;
import at.fhtw.rest.service.dtos.DocumentDto;


public interface DocumentService {
    void saveDocument(String documentDto);
    String getAllDocuments();

}
