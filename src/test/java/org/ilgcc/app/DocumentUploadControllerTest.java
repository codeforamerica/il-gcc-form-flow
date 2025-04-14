package org.ilgcc.app;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import formflow.library.FileController;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.data.UserFile;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.ClammitVirusScanner;
import formflow.library.file.CloudFileRepository;
import java.util.HashMap;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.ilgcc.app.data.Transaction;
import org.ilgcc.app.data.TransactionRepositoryService;
import org.ilgcc.app.utils.AbstractMockMvcTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
public class DocumentUploadControllerTest extends AbstractMockMvcTest {

    Submission submission;

    UUID submissionId;

    private final UUID fileId = UUID.randomUUID();

    private MockMvc mockMvc;

    @MockitoSpyBean
    private CloudFileRepository cloudFileRepository;

    @MockitoBean
    private SubmissionRepositoryService submissionRepositoryService;

    @MockitoBean
    private UserFileRepositoryService userFileRepositoryService;

    @Autowired
    private DocumentUploadController documentUploadController;

    @MockitoBean
    private FileController fileController;

    @MockitoBean
    private ClammitVirusScanner clammitVirusScanner;

    @MockitoSpyBean
    private TransactionRepositoryService transactionRepositoryService;

    @Override
    @BeforeEach
    public void setUp() {
        try {
            submissionId = UUID.randomUUID();
            mockMvc = MockMvcBuilders.standaloneSetup(documentUploadController).build();
            submission = Submission.builder().id(submissionId).build();
            submission.setInputData(new HashMap<>());
            
            when(clammitVirusScanner.virusDetected(any())).thenReturn(false);
            when(submissionRepositoryService.save(any())).thenReturn(submission);
            // Set the file ID on the UserFile since Mockito won't actually set one (it just returns what we tell it to)
            // It does not call the actual save method which is what sets the ID
            when(userFileRepositoryService.save(any())).thenAnswer(invocation -> {
                UserFile userFile = invocation.getArgument(0);
                userFile.setFileId(fileId);
                return userFile;
            });

            setFlowInfoInSession(session, "testFlow", submission.getId());
            super.setUp();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldReturn400IfTransactionExists() throws Exception {
        MockMultipartFile testImage = new MockMultipartFile("file", "testFileSizeImage.jpg", MediaType.IMAGE_JPEG_VALUE,
                new byte[(int) (FileUtils.ONE_MB + 1)]);

        when(fileController.findOrCreateSubmission(session, "testFlow")).thenReturn(submission);

        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setSubmissionId(submissionId);
        when(transactionRepositoryService.getBySubmissionId(submissionId)).thenReturn(transaction);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/doc-upload").file(testImage).param("flow", "testFlow")
                        .param("inputName", "dropZoneTestInstance").param("thumbDataURL", "base64string")
                        .param("screen", "testUploadScreen").session(session).contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value())).andExpect(
                        content().string("We weren't able to upload this file because your application has already been submitted."));
    }

    @Test
    void shouldReturn200IfTransactionDoesNotExist() throws Exception {
        MockMultipartFile testImage = new MockMultipartFile("file", "testFileSizeImage.jpg", MediaType.IMAGE_JPEG_VALUE,
                new byte[(int) (FileUtils.ONE_MB + 1)]);

        when(fileController.findOrCreateSubmission(session, "testFlow")).thenReturn(submission);

        when(transactionRepositoryService.getBySubmissionId(submissionId)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/doc-upload").file(testImage).param("flow", "testFlow")
                        .param("inputName", "dropZoneTestInstance").param("thumbDataURL", "base64string")
                        .param("screen", "testUploadScreen").session(session).contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().is(HttpStatus.OK.value()));
    }
}

