package at.fhtw.rest.unitTests;

import at.fhtw.rest.api.ApiController;
import at.fhtw.rest.service.DocumentService;
import at.fhtw.rest.service.dtos.DocumentDto;
import at.fhtw.rest.service.rabbitmq.DocumentProducer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiController.class)
public class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @MockBean
    private DocumentProducer documentProducer;

    @Test
    public void testGetAllDocuments() throws Exception {
        // Arrange
        DocumentDto documentDto1 = new DocumentDto(1L, "Document 1", "minio/path/to/document1");
        DocumentDto documentDto2 = new DocumentDto(2L, "Document 2", "minio/path/to/document2");
        List<DocumentDto> documentDtos = List.of(documentDto1, documentDto2);

        when(documentService.getAllDocuments()).thenReturn(documentDtos);

        // Set up MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(new ApiController(documentProducer, documentService)).build();

        // Act & Assert
        mockMvc.perform(get("/document/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Document 1"))
                .andExpect(jsonPath("$[0].content").value("minio/path/to/document1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Document 2"))
                .andExpect(jsonPath("$[1].content").value("minio/path/to/document2"));

        verify(documentService, times(1)).getAllDocuments();
    }

    @Test
    public void testUploadDocument() throws Exception {
        // Arrange
        String documentName = "Test Document";
        MockMultipartFile file = new MockMultipartFile("file", "testfile.txt", "text/plain", "test content".getBytes());

        // Simulate the service returning a DocumentDto
        DocumentDto documentDto = new DocumentDto(1L, documentName, "minio/path/to/1_Test Document");

        when(documentService.saveDocument(any(), any())).thenReturn(documentDto);

        // Capture the DTO sent to the producer
        ArgumentCaptor<DocumentDto> dtoCaptor = ArgumentCaptor.forClass(DocumentDto.class);

        // Set up MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(new ApiController(documentProducer, documentService)).build();

        // Act & Assert
        MvcResult result = mockMvc.perform(multipart("/document/{document}", documentName)
                        .file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Document"))
                .andExpect(jsonPath("$.content").value("minio/path/to/1_Test Document"))
                .andReturn();

        // Print the response body for debugging
        System.out.println(result.getResponse().getContentAsString());

        // Verify service and producer interactions
        verify(documentService, times(1)).saveDocument(eq(documentName), eq(file));
        verify(documentProducer, times(1)).sendDocumentEvent(dtoCaptor.capture());

        // Validate the DocumentDto sent to RabbitMQ
        DocumentDto capturedDto = dtoCaptor.getValue();
        assertThat(capturedDto.getId()).isEqualTo(1L);
        assertThat(capturedDto.getName()).isEqualTo(documentName);
        assertThat(capturedDto.getContent()).isEqualTo("minio/path/to/1_Test Document");
    }

}
