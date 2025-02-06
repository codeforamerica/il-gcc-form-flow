package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import jakarta.annotation.Resource;
import java.util.Map;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.CountyOptionUtils;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.ZipcodeOption;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.util.Assert;

@SpringBootTest(
        classes = IlGCCApplication.class
)

@TestPropertySource(properties = {"il-gcc.enable-sda15-providers=true"})
@ActiveProfiles("test")
public class CountyUtilTest {

    @Test
    public void getCountiesWithSda15ProvidersAsFalse() {
        assertThat(CountyOptionUtils.getActiveCountyOptions().size()).isEqualTo(18);
    }

}
