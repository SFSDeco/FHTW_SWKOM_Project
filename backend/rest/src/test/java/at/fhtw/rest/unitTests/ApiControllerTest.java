package at.fhtw.rest.unitTests;

import at.fhtw.rest.api.ApiController;
import at.fhtw.rest.service.DocumentService;
import at.fhtw.rest.service.dtos.DocumentDto;
import at.fhtw.rest.service.rabbitmq.DocumentProducer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class ApiControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;  // Mock the DocumentService

    @MockBean
    private DocumentProducer documentProducer;  // Mock the DocumentProducer

    @Test
    public void testGetAllDocuments() throws Exception {
        // Arrange
        DocumentDto documentDto1 = new DocumentDto(1L, "Document 1", "minio/path/to/document1");
        DocumentDto documentDto2 = new DocumentDto(2L, "Document 2", "minio/path/to/document2");
        List<DocumentDto> documentDtos = List.of(documentDto1, documentDto2);

        // Mock the service call
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

        // Verify interaction with the mocked service
        verify(documentService, times(1)).getAllDocuments();
    }

    @Test
    public void testUploadDocument() throws Exception {
        // Arrange
        String documentName = "Test Document";
        MockMultipartFile file = new MockMultipartFile("file", "testfile.txt", "text/plain", "test content".getBytes());

        // Set up MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(new ApiController(documentProducer, documentService)).build();

        // Act & Assert
        mockMvc.perform(multipart("/document/{document}", documentName)
                        .file(file)
                        .param("file", "testfile.txt"))
                .andExpect(status().isCreated());

        // Verify interactions with the mocked service and producer
        verify(documentService, times(1)).saveDocument(eq(documentName), eq(file));
        verify(documentProducer, times(1)).sendDocumentEvent(eq("Document created: " + documentName));
    }
}
