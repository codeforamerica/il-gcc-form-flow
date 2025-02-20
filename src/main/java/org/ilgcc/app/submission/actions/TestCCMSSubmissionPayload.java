package org.ilgcc.app.submission.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.file.CloudFile;
import formflow.library.file.CloudFileRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.data.ccms.CCMSApiClient;
import org.ilgcc.app.data.ccms.CCMSTransaction;
import org.ilgcc.app.data.ccms.CCMSTransactionPayloadService;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TestCCMSSubmissionPayload implements Action {
    
    private final CCMSApiClient client;
    private final CCMSTransactionPayloadService payloadService;
    private CloudFileRepository cloudFileRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    public TestCCMSSubmissionPayload(CCMSApiClient client, CCMSTransactionPayloadService payloadService,
            CloudFileRepository cloudFileRepository) {
        this.client = client;
        this.payloadService = payloadService;
        this.cloudFileRepository = cloudFileRepository;
    }
    
    @SneakyThrows
    @Override
    public void run(Submission submission) {
        payloadService.generatePayloadAndUploadToS3(submission);
        CloudFile cloudFile = cloudFileRepository.get(submission.getInputData().get("ccmsPayloadPath").toString());
        CCMSTransaction ccmsTransactionAfter = objectMapper.readValue(cloudFile.getFileBytes(), CCMSTransaction.class);
        String appSubmissionResponse = client.sendRequest("appSubmission", ccmsTransactionAfter);
        log.info("Response from CCMS: {}", appSubmissionResponse);
    }
}
