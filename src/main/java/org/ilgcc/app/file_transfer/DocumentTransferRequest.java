package org.ilgcc.app.file_transfer;

import formflow.library.data.Submission;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.UUID;

public interface DocumentTransferRequest {
    
    void sendDocumentTransferServiceRequest(String presignedUrl, Submission submission, String fileName, UUID transmissionId)
            throws IOException, URISyntaxException;
    String createJsonRequestBody(String presignedUrl, Submission submission, String fileName);
    
}

