package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;

import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.CountyOptionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = IlGCCApplication.class
)

@ActiveProfiles("test")
public class CountyUtilTest {

//    @Test
//    public void getCountiesWithSda15ProvidersAsFalse() {
//        assertThat(CountyOptionUtils.getActiveCounties().size()).isEqualTo(18);
//    }
}
