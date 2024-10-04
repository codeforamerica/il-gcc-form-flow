package org.ilgcc.app.utils;

import formflow.library.data.Submission;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.config.DocumentTransferConfiguration;
import org.ilgcc.app.file_transfer.DocumentTransferRequest;
import org.ilgcc.app.file_transfer.DocumentTransferRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("selenium-test")
public class NoOpDocumentTransferRequestService implements DocumentTransferRequest {
    
        @Override
        public void sendDocumentTransferServiceRequest(String presignedUrl, Submission submission, String fileName, UUID transmissionId) {
            log.info("No-Op: Simulating document transfer for transmission ID: {}", transmissionId);
        }

        @Override
        public String createJsonRequestBody(String presignedUrl, Submission submission, String fileName) {
                return "Test Request Body";
        }
}
