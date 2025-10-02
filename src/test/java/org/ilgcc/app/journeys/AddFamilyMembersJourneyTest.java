package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.SubmissionRepository;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"il-gcc.enable-multiple-providers=true"})
public class AddFamilyMembersJourneyTest extends AbstractBasePageTest {

    @Autowired
    SubmissionRepository submissionRepository;

    Map<String, Object> child2_NoAssistance = Map.of("firstName", "childSecond", "lastName", "childLast",
            "needFinancialAssistanceForChild", "false", "iterationIsComplete", true);

    Map<String, Object> child3_NoAssistance = Map.of("firstName", "childThird", "lastName", "childLast",
            "needFinancialAssistanceForChild", "false", "iterationIsComplete", true);

    Map<String, Object> child4_NoAssistance = Map.of("firstName", "childFourth", "lastName", "childLast",
            "needFinancialAssistanceForChild", "false", "iterationIsComplete", true);

    Map<String, Object> child5_NoAssistance = Map.of("firstName", "childFive", "lastName", "childLast",
            "needFinancialAssistanceForChild", "false", "iterationIsComplete", true);

    Map<String, Object> child6_NoAssistance = Map.of("firstName", "childSixth", "lastName", "childLast",
            "needFinancialAssistanceForChild", "false", "iterationIsComplete", true);

    Map<String, Object> child7_NoAssistance = Map.of("firstName", "childSeventh", "lastName", "childLast",
            "needFinancialAssistanceForChild", "false", "iterationIsComplete", true);

    Map<String, Object> child8_NoAssistance = Map.of("firstName", "childEigth", "lastName", "childLast",
            "needFinancialAssistanceForChild", "false", "iterationIsComplete", true);

    @AfterEach
    public void tearDown() {
        submissionRepository.deleteAll();
    }

    @Test
    void MaxWarningAppearsWhenAddingMoreThan4ChildrenNeedingAssistance() {
        //children-info-intro
        testPage.navigateToFlowScreen("gcc/children-info-intro");

        // Using test builder with providers data to so that child data can be preloaded
        saveSubmission(getSessionSubmissionTestBuilder().withValidSubmissionUpTo4ProvidersInfo(List.of(child_1, child_2, child_3,
                child_4)).build());

        testPage.clickContinue();

        // children-add
        testPage.clickButton(getEnMessage("children-add.add-button"));

        testPage.enter("childFirstName", "Fifth");
        testPage.enter("childLastName", "Child Needing Assistance");
        testPage.enter("childDateOfBirthMonth", "1");
        testPage.enter("childDateOfBirthDay", "1");
        testPage.enter("childDateOfBirthYear", "2022");
        testPage.selectFromDropdown("childRelationship", getEnMessage("general.relationship-option.child"));
        testPage.clickContinue();

        // children-info-assistance
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-info-assistance.title"));
        testPage.clickYes();

        // children-ccap-max-number
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-ccap-max-number.title"));
        testPage.clickContinue();

        // children-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-add.title"));
    }

    @Test
    void AddChildrenButtonIsDisabledAt9Children() {
        //children-info-intro
        testPage.navigateToFlowScreen("gcc/children-info-intro");

        // Using test builder with providers data to so that child data can be preloaded
        saveSubmission(getSessionSubmissionTestBuilder().withValidSubmissionUpTo4ProvidersInfo(List.of(child_1,
                child2_NoAssistance, child3_NoAssistance, child4_NoAssistance, child5_NoAssistance, child6_NoAssistance,
                child7_NoAssistance, child8_NoAssistance)).build());

        testPage.clickContinue();

        // children-add
        testPage.clickButton(getEnMessage("children-add.add-button"));

        testPage.enter("childFirstName", "Ninth");
        testPage.enter("childLastName", "Child");
        testPage.enter("childDateOfBirthMonth", "1");
        testPage.enter("childDateOfBirthDay", "1");
        testPage.enter("childDateOfBirthYear", "2022");
        testPage.selectFromDropdown("childRelationship", getEnMessage("general.relationship-option.child"));
        testPage.clickContinue();

        // children-info-assistance
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-info-assistance.title"));
        testPage.clickNo();

        // children-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-add.title"));
        assertThat(testPage.findElementById("add-children").getAttribute("class").contains("disabled")).isTrue();
    }

    @Test
    void NoCCAPChildrenDisplaysWarningAndRemovesButton() {
        //children-info-intro
        testPage.navigateToFlowScreen("gcc/children-info-intro");

        // Using test builder with providers data to so that child data can be preloaded
        saveSubmission(getSessionSubmissionTestBuilder().withValidSubmissionUpTo4ProvidersInfo(List.of(child2_NoAssistance,
                child3_NoAssistance)).build());

        testPage.clickContinue();

        // children-add
        testPage.clickButton(getEnMessage("children-add.add-button"));

        testPage.enter("childFirstName", "Third");
        testPage.enter("childLastName", "Child");
        testPage.enter("childDateOfBirthMonth", "1");
        testPage.enter("childDateOfBirthDay", "1");
        testPage.enter("childDateOfBirthYear", "2022");
        testPage.selectFromDropdown("childRelationship", getEnMessage("general.relationship-option.child"));
        testPage.clickContinue();

        // children-info-assistance
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-info-assistance.title"));
        testPage.clickNo();

        // children-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-add.title"));
        assertThat(testPage.findElementTextById("no-children-warning")).isEqualTo(
                getEnMessage("children-add.no-children-warning"));

        assertThat(testPage.elementDoesNotExistById("continue-link")).isTrue();
    }
}
