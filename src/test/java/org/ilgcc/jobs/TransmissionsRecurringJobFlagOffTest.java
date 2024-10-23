package org.ilgcc.jobs;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.data.TransmissionRepositoryService;
import org.ilgcc.app.file_transfer.S3PresignService;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.OffsetDateTime;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(
        classes = IlGCCApplication.class,
        properties = {"il-gcc.dts.expand-existing-provider-flow=false"}
)
@ActiveProfiles("test")
public class TransmissionsRecurringJobFlagOffTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void transmissionRecurringJobDisabledWhenFlagIsOff() {
        assertFalse(context.containsBean("transmissionsRecurringJob"));
    }
}
