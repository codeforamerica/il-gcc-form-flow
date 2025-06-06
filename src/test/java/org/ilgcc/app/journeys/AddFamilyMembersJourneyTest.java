package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

public class AddFamilyMembersJourneyTest extends AbstractBasePageTest {

    @Test
    void MaxWarningAppearsWhenAddingMoreThan4ChildrenNeedingAssistance() {
        //children-info-intro
        testPage.navigateToFlowScreen("gcc/children-info-intro");

        saveSubmission(
                getSessionSubmissionTestBuilder().withDayCareProvider().withParentDetails().withChild("First", "Child", "true")
                        .withChild("Second", "Child", "true").withChild("Third", "Child", "true")
                        .withChild("Fourth", "Child", "true").build());

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

        saveSubmission(
                getSessionSubmissionTestBuilder().withDayCareProvider().withParentDetails().withChild("First", "Child", "true")
                        .withChild("Second", "Child", "false").withChild("Third", "Child", "false").withChild("Fourth", "Child", "false")
                        .withChild("Fifth", "Child", "false").withChild("Sixth", "Child", "false").withChild("Seventh", "Child", "false")
                        .withChild("Eight", "Child", "false").build());

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
