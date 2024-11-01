package at.fhtw.rest.service.mapper;

import at.fhtw.rest.persistence.entity.DocumentEntity;
import at.fhtw.rest.service.dtos.DocumentDto;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper extends AbstractMapper<DocumentEntity, DocumentDto>{

    @Override
    public DocumentDto mapToDto(DocumentEntity source) {
        return DocumentDto.builder()
                .id(source.getId())
                .name(source.getName())
                .content(source.getContent())
                .build();
    }
}
