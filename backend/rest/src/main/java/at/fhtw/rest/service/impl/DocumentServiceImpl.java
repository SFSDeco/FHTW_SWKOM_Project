package at.fhtw.rest.service.impl;


import at.fhtw.rest.persistence.entity.DocumentEntity;
import at.fhtw.rest.persistence.repositories.DocumentRepository;
import at.fhtw.rest.service.DocumentService;
import at.fhtw.rest.service.dtos.DocumentDto;
import at.fhtw.rest.service.mapper.DocumentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
public class DocumentServiceImpl implements DocumentService {
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private DocumentMapper documentMapper;

    @Override
    public void saveDocument(String documentDto) { //file dann noch hinzufügen
        DocumentEntity documentEntity = DocumentEntity.builder()
                .name(documentDto)
                .content("TestContent")
                .build();
        documentMapper.mapToDto(documentRepository.save(documentEntity));
    }

    @Override
    public List<DocumentDto> getAllDocuments() {
        return documentMapper.mapToDto(documentRepository.findAll());
    }
}
