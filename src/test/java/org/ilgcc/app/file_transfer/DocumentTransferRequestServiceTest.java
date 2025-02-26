package org.ilgcc.app.file_transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import formflow.library.data.Submission;
import java.time.OffsetDateTime;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.enums.CCRRSlug;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = IlGCCApplication.class)
@ActiveProfiles("test")
class DocumentTransferRequestServiceTest {

    @Autowired
    DocumentTransferRequestService service;

    @Mock
    Environment environment;
    private String presignedUrl = "testURL";
    private Submission submission;
    private String fileName = "file_name.png";

    String[] profiles = new String[1];

    @Test
    void noOrganizationIdSuccessfullyRoutesTo4cInNonProd() {
        profiles[0] = "test";
        when(environment.getActiveProfiles()).thenReturn(profiles);

        submission = new SubmissionTestBuilder().withFlow("gcc").withSubmittedAtDate(OffsetDateTime.now()).withParentDetails()
                .build();
        String requestBody = service.createJsonRequestBody(presignedUrl, submission, fileName);
        assertThat(extractPathFromRequestBody(requestBody)).containsIgnoringCase("4c-ccap-apps-testing");
    }

    @Test
    void successfullyRoutesToProjectChildInNonProd() {
        profiles[0] = "test";
        when(environment.getActiveProfiles()).thenReturn(profiles);
        submission = new SubmissionTestBuilder().withFlow("gcc").withSubmittedAtDate(OffsetDateTime.now()).with("organizationId",
                CCRRSlug.PROJECT_CHILD.getOrgId()).withParentDetails()
                .build();
        String requestBody = service.createJsonRequestBody(presignedUrl, submission, fileName);
        assertThat(extractPathFromRequestBody(requestBody)).containsIgnoringCase("project-child-ccap-apps-testing");
    }

    @Test
    void successfullyRoutesToIllinoisActionInNonProd() {
        profiles[0] = "test";
        when(environment.getActiveProfiles()).thenReturn(profiles);
        submission = new SubmissionTestBuilder().withFlow("gcc").withSubmittedAtDate(OffsetDateTime.now()).with("organizationId",
                        CCRRSlug.ILLINOIS_ACTION.getOrgId()).withParentDetails()
                .build();
        String requestBody = service.createJsonRequestBody(presignedUrl, submission, fileName);
        assertThat(extractPathFromRequestBody(requestBody)).containsIgnoringCase(
                "illinois-action-for-children-ccap-apps-testing");
    }

    @Test
    void successfullyRoutesTo4cInNonProd() {
        profiles[0] = "test";
        when(environment.getActiveProfiles()).thenReturn(profiles);
        submission = new SubmissionTestBuilder().withFlow("gcc").withSubmittedAtDate(OffsetDateTime.now()).with("organizationId",
                        CCRRSlug.PROJECT_CHILD).withParentDetails()
                .build();
        String requestBody = service.createJsonRequestBody(presignedUrl, submission, fileName);
        assertThat(extractPathFromRequestBody(requestBody)).containsIgnoringCase("4c-ccap-apps-testing");
    }

    @Test
    void noOrganizationIdSuccessfullyRoutesTo4cInProd() {
        profiles[0] = "production";
        when(environment.getActiveProfiles()).thenReturn(profiles);
        submission = new SubmissionTestBuilder().withFlow("gcc").withSubmittedAtDate(OffsetDateTime.now()).withParentDetails()
                .build();
        String requestBody = service.createJsonRequestBody(presignedUrl, submission, fileName);
        assertThat(extractPathFromRequestBody(requestBody)).containsIgnoringCase("4c-ccap-apps");
    }

    @Test
    void successfullyRoutesToProjectChildInProd() {
        profiles[0] = "production";
        when(environment.getActiveProfiles()).thenReturn(profiles);
        submission = new SubmissionTestBuilder().withFlow("gcc").withSubmittedAtDate(OffsetDateTime.now()).with("organizationId",
                CCRRSlug.PROJECT_CHILD.getOrgId()).withParentDetails()
                .build();
        String requestBody = service.createJsonRequestBody(presignedUrl, submission, fileName);
        assertThat(extractPathFromRequestBody(requestBody)).containsIgnoringCase("project-child-ccap-apps");
    }

    @Test
    void successfullyRoutesToIllinoisActionInProd() {
        profiles[0] = "production";
        when(environment.getActiveProfiles()).thenReturn(profiles);
        submission = new SubmissionTestBuilder().withFlow("gcc").withSubmittedAtDate(OffsetDateTime.now()).with("organizationId",
                        CCRRSlug.ILLINOIS_ACTION.getOrgId()).withParentDetails()
                .build();
        String requestBody = service.createJsonRequestBody(presignedUrl, submission, fileName);
        assertThat(extractPathFromRequestBody(requestBody)).containsIgnoringCase("illinois-action-for-children-ccap-apps");
    }

    @Test
    void successfullyRoutesTo4cInProd() {
        profiles[0] = "production";
        when(environment.getActiveProfiles()).thenReturn(profiles);
        submission = new SubmissionTestBuilder().withFlow("gcc").withSubmittedAtDate(OffsetDateTime.now()).with("organizationId",
                        CCRRSlug.FOUR_C.getOrgId()).withParentDetails()
                .build();
        String requestBody = service.createJsonRequestBody(presignedUrl, submission, fileName);
        assertThat(extractPathFromRequestBody(requestBody)).containsIgnoringCase("4c-ccap-apps");
    }

    private String extractPathFromRequestBody(String requestBody) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(requestBody, JsonObject.class);

        return jsonObject
                .getAsJsonObject("destination")
                .get("path")
                .getAsString();

    }

}