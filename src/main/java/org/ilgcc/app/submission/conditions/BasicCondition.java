package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;

public class BasicCondition implements Condition {

    /**
     * Return true if the value corresponding to the key in submission data is equal to the given value.
     */
    public Boolean run(Submission submission, String key, String value) {
        var inputData = submission.getInputData();
        if (inputData.containsKey(key)) {
            return submission.getInputData().get(key).equals(value);
        }
        return false;
    }
}