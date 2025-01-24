package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.ilgcc.app.utils.enums.ProviderLanguagesOffered.*;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = IlGCCApplication.class,
        properties = "il-gcc.dts.expand-existing-provider-flow=true"
)
@ActiveProfiles("test")
public class ProviderLanguagePreparerTest {


    @Autowired
    private ProviderLanguagesPreparer preparer;

    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;
    private Submission familySubmission;
    private Submission providerSubmission;

    @Test
    public void doesNotSetProviderLanguageIfProviderLanguagesOfferedIsNone() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerLanguagesOffered[]", List.of("None"))
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerLanguageEnglish")).isEqualTo(null);
        assertThat(result.get("providerLanguageSpanish")).isEqualTo(null);
        assertThat(result.get("providerLanguagePolish")).isEqualTo(null);
        assertThat(result.get("providerLanguageChinese")).isEqualTo(null);
        assertThat(result.get("providerLanguageOther")).isEqualTo(null);
        assertThat(result.get("providerLanguageOtherDetail")).isEqualTo(null);
    }


    @Test
    public void doesNotSetProviderLanguageIfProviderLanguagesOfferedIsNotPresent() {
        providerSubmission = new SubmissionTestBuilder()
            .withFlow("providerresponse")
            .withProviderSubmissionData()
            .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
            .withFlow("gcc")
            .with("providerResponseSubmissionId", providerSubmission.getId())
            .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerLanguageEnglish")).isEqualTo(null);
        assertThat(result.get("providerLanguageSpanish")).isEqualTo(null);
        assertThat(result.get("providerLanguagePolish")).isEqualTo(null);
        assertThat(result.get("providerLanguageChinese")).isEqualTo(null);
        assertThat(result.get("providerLanguageOther")).isEqualTo(null);
        assertThat(result.get("providerLanguageOtherDetail")).isEqualTo(null);
    }
    @Test
    public void selectsProviderLanguageCheckboxesWhenProviderLanguagesOfferedIncludesSupportedLanguages() {
        //English, Spanish, Polish, and Chinese languages have Provider Language checkboxes that can be selected
        //Any other language that is included in the providerLanguages offered array results in the PROVIDER_LANGUAGE_OTHER field being set to true
        providerSubmission = new SubmissionTestBuilder()
            .withFlow("providerresponse")
            .withProviderSubmissionData()
            .with("providerLanguagesOffered[]", List.of("English", "Spanish", "Polish", "Chinese"))
            .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
            .withFlow("gcc")
            .with("providerResponseSubmissionId", providerSubmission.getId())
            .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerLanguageEnglish")).isEqualTo(new SingleField("providerLanguageEnglish", "true", null));
        assertThat(result.get("providerLanguageSpanish")).isEqualTo(new SingleField("providerLanguageSpanish", "true", null));
        assertThat(result.get("providerLanguagePolish")).isEqualTo(new SingleField("providerLanguagePolish", "true", null));
        assertThat(result.get("providerLanguageChinese")).isEqualTo(new SingleField("providerLanguageChinese", "true", null));
        assertThat(result.get("providerLanguageOther")).isEqualTo(null);
        assertThat(result.get("providerLanguageOtherDetail")).isEqualTo(null);
    }
    @Test
    public void selectOtherCheckboxAndListLanguagesInOtherDetailWhenProviderLanguagesValueIsOther() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerLanguagesOffered[]", List.of("Tagalog", "Hindi"))
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerLanguageOther")).isEqualTo(new SingleField("providerLanguageOther", "true", null));
        assertThat(result.get("providerLanguageOtherDetail")).isEqualTo(new SingleField("providerLanguageOtherDetail",
            String.format("%s, %s", TAGALOG.getProviderLanguageOtherDetailPdfFieldValue(), HINDI.getProviderLanguageOtherDetailPdfFieldValue()), null));
    }

    @Test
    public void whenOtherIsIncludedAsProviderLanguageOfferedSelectOtherCheckboxAndAddDetails() {
        String providerLanguagesOfferedOtherString = "test, double test, gibberish";
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerLanguagesOffered[]", List.of("other"))
                .with("providerLanguagesOffered_other", providerLanguagesOfferedOtherString)
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerLanguageOther")).isEqualTo(new SingleField("providerLanguageOther", "true", null));
        assertThat(result.get("providerLanguageOtherDetail")).isEqualTo(new SingleField("providerLanguageOtherDetail", providerLanguagesOfferedOtherString, null));
    }

    @Test
    public void shouldSelectOtherCheckboxAndAllOtherLanguagesWhenOtherProviderLanguagesAreSelected() {
        String providerLanguagesOfferedOtherString = "test, double gibberish";
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerLanguagesOffered[]", List.of("Tagalog", "other"))
                .with("providerLanguagesOffered_other", providerLanguagesOfferedOtherString)
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerLanguageOther")).isEqualTo(new SingleField("providerLanguageOther", "true", null));
        assertThat(result.get("providerLanguageOtherDetail")).isEqualTo(new SingleField("providerLanguageOtherDetail",
            String.format("%s, %s", TAGALOG.getProviderLanguageOtherDetailPdfFieldValue(), providerLanguagesOfferedOtherString) , null));
    }

    @Test
    public void shouldSelectLanguagesWithCheckboxesAndOtherLanguagesWhenOtherProviderLanguagesAreSelected() {
        String providerLanguagesOfferedOtherString = "test, double gibberish";
        providerSubmission = new SubmissionTestBuilder()
            .withFlow("providerresponse")
            .withProviderSubmissionData()
            .with("providerLanguagesOffered[]", List.of("English", "Tagalog", "Spanish", "Hindi","other"))
            .with("providerLanguagesOffered_other", providerLanguagesOfferedOtherString)
            .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerLanguageEnglish")).isEqualTo(new SingleField("providerLanguageEnglish", "true", null));
        assertThat(result.get("providerLanguageSpanish")).isEqualTo(new SingleField("providerLanguageSpanish", "true", null));
        assertThat(result.get("providerLanguageOther")).isEqualTo(new SingleField("providerLanguageOther", "true", null));
        assertThat(result.get("providerLanguageOtherDetail")).isEqualTo(new SingleField("providerLanguageOtherDetail",
            String.format("%s, %s, %s", TAGALOG.getProviderLanguageOtherDetailPdfFieldValue(), HINDI.getProviderLanguageOtherDetailPdfFieldValue(), providerLanguagesOfferedOtherString), null));
        assertThat(result.get("providerLanguageChinese")).isNotEqualTo(new SingleField("providerLanguageChinese", "true", null));
    }
}
