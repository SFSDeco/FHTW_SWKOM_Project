package at.fhtw.rest.service.impl;
import lombok.extern.log4j.Log4j2;


import at.fhtw.rest.persistence.entity.DocumentEntity;
import at.fhtw.rest.persistence.repositories.DocumentRepository;
import at.fhtw.rest.service.DocumentService;
import at.fhtw.rest.service.dtos.DocumentDto;
import at.fhtw.rest.service.mapper.DocumentMapper;
import at.fhtw.rest.service.minio.MinIOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Log4j2
@Component
public class DocumentServiceImpl implements DocumentService {
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private DocumentMapper documentMapper;
    @Autowired
    private MinIOService minioService;

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);



    @Override
    public DocumentDto saveDocument(String documentName, MultipartFile file) {
        try {
            DocumentEntity documentEntity = DocumentEntity.builder()
                    .name(documentName)
                    .content("Placeholder") // Placeholder for file path, as upload happens later
                    .build();

            DocumentEntity savedEntity = documentRepository.save(documentEntity); // ID is generated here

            String fileName = savedEntity.getId() + "_" + documentName;

            String filePath = minioService.uploadDocument(fileName, file);

            savedEntity.setContent(filePath);

            return documentMapper.mapToDto(savedEntity);

        } catch (IOException e) {
            logger.error("Error uploading file to MinIO", e); // Use logger instead of printStackTrace
            throw new RuntimeException("Error uploading file to MinIO", e);
        }
    }
    @Override
    public ResponseEntity<byte[]> getFile(Long id){
        DocumentEntity document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        byte[] fileContent = minioService.downloadDocument(document.getContent()); // Lade Datei aus MinIO

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + document.getName())
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf") // Wichtig für den richtigen MIME-Typ
                .body(fileContent);
    }


    @Override
    public void deleteDocument(Long id) {
        // Finde das Dokument aus der Datenbank
        DocumentEntity document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        // Lösche die Datei aus MinIO
        try {
            minioService.deleteDocument(document.getContent()); // Verwendet den Pfad, der in der Datenbank gespeichert ist
        } catch (IOException e) {
            logger.error("Error deleting file from MinIO", e);
            throw new RuntimeException("Error deleting file from MinIO", e);
        }

        // Lösche das Dokument aus der Datenbank
        documentRepository.delete(document);
    }




    @Override
    public List<DocumentDto> getAllDocuments() {
        return documentMapper.mapToDto(documentRepository.findAll());
    }
}
