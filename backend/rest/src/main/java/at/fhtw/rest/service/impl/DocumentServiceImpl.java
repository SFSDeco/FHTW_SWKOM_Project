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
    public void saveDocument(String documentDto, MultipartFile file) {
        try {
            // Lade das Dokument in MinIO hoch
            String filePath = minioService.uploadDocument(documentDto, file);


            // Speichere das Dokument in der Datenbank
            DocumentEntity documentEntity = DocumentEntity.builder()
                    .name(documentDto)
                    .content(filePath)  // Hier ggf. den Dateipfad oder andere Informationen speichern
                    .build();
            documentMapper.mapToDto(documentRepository.save(documentEntity));

        } catch (IOException e) {
            logger.error("Error uploading file to MinIO", e); // Use logger instead of printStackTrace
            throw new RuntimeException("Error uploading file to MinIO", e);
        }
    }

    @Override
    public List<DocumentDto> getAllDocuments() {
        return documentMapper.mapToDto(documentRepository.findAll());
    }
}
