package org.ilgcc.app.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ilgcc.app.data.importer.FakeResourceOrganizationAndCountyData.FOUR_C_TEST_DATA;
import static org.ilgcc.app.data.importer.FakeResourceOrganizationAndCountyData.PROJECT_CHILD_TEST_DATA;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.ilgcc.app.data.ResourceOrganizationTransaction;

import org.ilgcc.app.email.templates.DailyNewApplicationsProviderEmailTemplate;
import org.ilgcc.jobs.DailyNewApplicationsProviderEmailRecurringJob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles("test")
@SpringBootTest
public class SendAutomatedProviderOutreachEmailTest {

    @Autowired
    DailyNewApplicationsProviderEmailRecurringJob dailyNewApplicationsProviderEmailRecurringJob;

    @Autowired
    MessageSource messageSource;
    OffsetDateTime currentDate = OffsetDateTime.of(2025, 10, 14, 2, 0, 0, 0, ZoneOffset.UTC);
    OffsetDateTime transactionsAsOfDate = currentDate.minusHours(24);

    Locale locale = Locale.ENGLISH;
    DailyNewApplicationsProviderEmailTemplate dailyNewApplicationsFourcEmailTemplate;

    DailyNewApplicationsProviderEmailTemplate dailyNewApplicationstransactionlessOrgTemplate;

    Map<String, Object> fourcEmailData;

    List<String> fourcShortCodes;

    ILGCCEmailTemplate fourcEmailTemplate;

    Map<String, Object> transactionlessOrgEmailData;

    ILGCCEmailTemplate transactionlessOrgEmailTemplate;

    @BeforeEach
    public void setUp() {
        fourcShortCodes = List.of("A12345", "B12345", "C12345", "D12345", "E12345", "F12345");
        List<ResourceOrganizationTransaction> resourceOrganizationTransactions = new ArrayList<>();

        for (int i = 0; i < fourcShortCodes.size(); i++) {
            resourceOrganizationTransactions.add(
                    new ResourceOrganizationTransaction("12345678901234", OffsetDateTime.now(), fourcShortCodes.get(i),
                            String.format("W0000%s", i)));
        }

        fourcEmailData = dailyNewApplicationsProviderEmailRecurringJob.generateEmailData(
                Optional.of(Map.of("12345678901234", resourceOrganizationTransactions)),
                FOUR_C_TEST_DATA, transactionsAsOfDate, currentDate);

        dailyNewApplicationsFourcEmailTemplate = new DailyNewApplicationsProviderEmailTemplate(fourcEmailData, messageSource);

        fourcEmailTemplate = dailyNewApplicationsFourcEmailTemplate.createTemplate();

        transactionlessOrgEmailData = dailyNewApplicationsProviderEmailRecurringJob.generateEmailData(
                Optional.of(Map.of("12345678901235", List.of())),
                PROJECT_CHILD_TEST_DATA, transactionsAsOfDate, currentDate);

        dailyNewApplicationstransactionlessOrgTemplate = new DailyNewApplicationsProviderEmailTemplate(
                transactionlessOrgEmailData, messageSource);

        transactionlessOrgEmailTemplate = dailyNewApplicationstransactionlessOrgTemplate.createTemplate();
    }

    @Test
    void correctlySetsEmailSubject() {
        assertThat(fourcEmailTemplate.getSubject()).isEqualTo(messageSource.getMessage("email.automated-new-applications.subject",
                new Object[]{fourcEmailData.get("processingOrgName").toString(), fourcEmailData.get("currentEmailDate").toString()}, null));
    }

    @Test
    void correctlySetsBodyCopyWithTransactions() {
        String emailCopy = fourcEmailTemplate.getBody().getValue();

        assertThat(emailCopy).contains(
                messageSource.getMessage("email.automated-new-applications.header1", new Object[]{FOUR_C_TEST_DATA.getName()},
                        locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.automated-new-applications.body1", new Object[]{"October 13, 2025"},
                        locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.automated-new-applications.header2", null,
                        locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.automated-new-applications.body2", new Object[]{"6", FOUR_C_TEST_DATA.getName()},
                        locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.automated-new-applications.header3", null,
                        locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.automated-new-application.cta", null,
                        locale));

        fourcShortCodes.forEach(c -> {
            assertThat(emailCopy).contains(c);
        });

    }

    @Test
    void correctlySetsBodyCopyWithoutTransactions() {
        String emailCopy = transactionlessOrgEmailTemplate.getBody().getValue();

        assertThat(emailCopy).contains(
                messageSource.getMessage("email.automated-new-applications.header1",
                        new Object[]{PROJECT_CHILD_TEST_DATA.getName()},
                        locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.automated-new-applications.body1", new Object[]{"October 13, 2025"},
                        locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.automated-new-applications.header2", null,
                        locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.automated-new-applications.body2",
                        new Object[]{"0", PROJECT_CHILD_TEST_DATA.getName()},
                        locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.automated-new-applications.header3", null,
                        locale));
        assertThat(emailCopy).contains(
                messageSource.getMessage("email.automated-new-application.cta", null,
                        locale));
    }

}