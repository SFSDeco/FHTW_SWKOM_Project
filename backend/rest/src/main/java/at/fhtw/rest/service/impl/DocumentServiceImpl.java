package at.fhtw.rest.service.impl;


import at.fhtw.rest.persistence.entity.DocumentEntity;
import at.fhtw.rest.persistence.repositories.DocumentRepository;
import at.fhtw.rest.service.DocumentService;
import at.fhtw.rest.service.dtos.DocumentDto;
import at.fhtw.rest.service.mapper.DocumentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DocumentServiceImpl implements DocumentService {
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private DocumentMapper documentMapper;

    @Override
    public void saveDocument(String documentDto) {
        DocumentEntity documentEntity = DocumentEntity.builder()
                .name(documentDto)
                .build();
        documentMapper.mapToDto(documentRepository.save(documentEntity));
    }

    @Override
    public String getAllDocuments() {
        return documentRepository.findAll().toString();
    }
}
