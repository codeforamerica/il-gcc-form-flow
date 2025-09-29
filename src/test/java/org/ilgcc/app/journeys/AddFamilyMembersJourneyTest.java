package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

public class AddFamilyMembersJourneyTest extends AbstractBasePageTest {


    @Test
    void MaxWarningAppearsWhenAddingMoreThan4ChildrenNeedingAssistance() {
        //children-info-intro
        testPage.navigateToFlowScreen("gcc/children-info-intro");

        saveSubmission(getSessionSubmissionTestBuilder().withValidSubmissionUpToStep4(List.of(child_1, child_2, child_3,
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

        Map<String, Object> child2_NoAssistance = Map.of("needFinancialAssistanceForChild", "false");
        child2_NoAssistance.putAll(child_2);

        Map<String, Object> child3_NoAssistance = Map.of("needFinancialAssistanceForChild", "false");
        child3_NoAssistance.putAll(child_3);

        Map<String, Object> child4_NoAssistance = Map.of("needFinancialAssistanceForChild", "false");
        child4_NoAssistance.putAll(child_4);

        Map<String, Object> child5_NoAssistance = Map.of("needFinancialAssistanceForChild", "false");
        child5_NoAssistance.putAll(child_5);

        Map<String, Object> child6_NoAssistance = Map.of("needFinancialAssistanceForChild", "false");
        child6_NoAssistance.putAll(child_6);

        Map<String, Object> child7_NoAssistance = Map.of("needFinancialAssistanceForChild", "false");
        child7_NoAssistance.putAll(child_7);

        Map<String, Object> child8_NoAssistance = Map.of("needFinancialAssistanceForChild", "false");
        child8_NoAssistance.putAll(child_8);

        saveSubmission(getSessionSubmissionTestBuilder().withValidSubmissionUpToStep4(List.of(child_1,
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

        saveSubmission(
                getSessionSubmissionTestBuilder().withDayCareProvider().withParentDetails().withChild("First", "Child", "false")
                        .withChild("Second", "Child", "false").build());

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
