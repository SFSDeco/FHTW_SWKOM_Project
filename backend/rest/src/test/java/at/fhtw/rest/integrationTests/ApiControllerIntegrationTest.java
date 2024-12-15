package at.fhtw.rest.integrationTests;

import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
public class ApiControllerIntegrationTest {

    @Container
    public static final GenericContainer<?> minio = new GenericContainer<>(DockerImageName.parse("minio/minio"))
            .withExposedPorts(9000, 9090)
            .withCommand("server --console-address \":9090\" /data");

    @Container
    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("mydatabase")
            .withUsername("myuser")
            .withPassword("secret");

    @Container
    public static final RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3-management")
            .withExposedPorts(5672, 15672);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentService documentService;

    @Test
    public void testGetAllDocuments() throws Exception {
        // Arrange
        DocumentDto documentDto1 = new DocumentDto(1L, "Document 1", "minio/path/to/document1");
        DocumentDto documentDto2 = new DocumentDto(2L, "Document 2", "minio/path/to/document2");

        // Mock service call
        when(documentService.getAllDocuments()).thenReturn(List.of(documentDto1, documentDto2));

        // Act & Assert
        mockMvc.perform(get("/document/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Document 1"))
                .andExpect(jsonPath("$[1].name").value("Document 2"));

        verify(documentService, times(1)).getAllDocuments();
    }

    @Test
    public void testUploadDocument() throws Exception {
        // Arrange
        String documentName = "Test Document";
        MockMultipartFile file = new MockMultipartFile("file", "testfile.txt", "text/plain", "test content".getBytes());

        // Act & Assert
        mockMvc.perform(multipart("/document/{document}", documentName)
                        .file(file)
                        .param("file", "testfile.txt"))
                .andExpect(status().isCreated());

        // Verify interaction with the service
        verify(documentService, times(1)).saveDocument(eq(documentName), eq(file));
    }
}
