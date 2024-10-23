package org.ilgcc.jobs;

import org.ilgcc.app.IlGCCApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(
        classes = IlGCCApplication.class,
        properties = "il-gcc.dts.expand-existing-provider-flow=false"
)
@ActiveProfiles("test")
public class TransmissionsRecurringJobFlagOffTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void transmissionRecurringJobDisabledWhenFlagIsOff() {
        assertFalse(context.containsBean("transmissionsRecurringJob"));
    }
}
