package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;

public class AddFamilyMembersJourneyTest extends AbstractBasePageTest {

    @Test
    void MaxWarningAppearsWhenAddingMoreThan4ChildrenNeedingAssistance() {
        //children-info-intro
        testPage.navigateToFlowScreen("gcc/children-info-intro");

        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider().withParentDetails()
            .withChild("First", "Child", "Yes")
            .withChild("Second", "Child", "Yes")
            .withChild("Third", "Child", "Yes")
            .withChild("Fourth", "Child", "Yes")
            .build());

        testPage.clickContinue();

        // children-add
        testPage.clickButton(getEnMessage("children-add.add-button"));

        testPage.enter("childFirstName", "Fifth");
        testPage.enter("childLastName", "Child Needing Assistance");
        testPage.enter("childDateOfBirthMonth", "1");
        testPage.enter("childDateOfBirthDay", "1");
        testPage.enter("childDateOfBirthYear", "2022");
        testPage.selectFromDropdown("childRelationship", getEnMessage("children-ccap-info.relationship-option.child"));
        testPage.selectRadio("needFinancialAssistanceForChild", "Yes");
        testPage.clickContinue();

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

        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider().withParentDetails()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "No")
                .withChild("Third", "Child", "No")
                .withChild("Fourth", "Child", "No")
                .withChild("Fifth", "Child", "No")
                .withChild("Sixth", "Child", "No")
                .withChild("Seventh", "Child", "No")
                .withChild("Eight", "Child", "No")
                .build());

        testPage.clickContinue();

        // children-add
        testPage.clickButton(getEnMessage("children-add.add-button"));

        testPage.enter("childFirstName", "Ninth");
        testPage.enter("childLastName", "Child");
        testPage.enter("childDateOfBirthMonth", "1");
        testPage.enter("childDateOfBirthDay", "1");
        testPage.enter("childDateOfBirthYear", "2022");
        testPage.selectFromDropdown("childRelationship", getEnMessage("children-ccap-info.relationship-option.child"));
        testPage.selectRadio("needFinancialAssistanceForChild", "No");
        testPage.clickContinue();

        // children-add
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("children-add.title"));
        assertThat(testPage.findElementById("add-children").getAttribute("class").contains("disabled")).isTrue();
    }
}
