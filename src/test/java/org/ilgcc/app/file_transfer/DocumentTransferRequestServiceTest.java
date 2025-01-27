package org.ilgcc.app.file_transfer;

import static org.junit.jupiter.api.Assertions.*;

import org.ilgcc.app.IlGCCApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = IlGCCApplication.class)

class DocumentTransferRequestServiceTest {

    @Autowired
    DocumentTransferRequestService service;


}