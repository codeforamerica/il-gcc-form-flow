package org.ilgcc.app.utils;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@ContextConfiguration()
public abstract class AbstractMockMvcTest {

  @MockBean
  protected Clock clock;

  @MockBean
  protected Submission submission;

  @SpyBean
  protected SubmissionRepositoryService submissionRepositoryService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  protected MockMvc mockMvc;

  protected MockHttpSession session = new MockHttpSession();

  @Autowired
  protected MessageSource messageSource;

  @BeforeEach
  protected void setUp() {
    session.setAttribute("submissionMap", new HashMap<>());
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(this.webApplicationContext)
        .apply(sharedHttpSession()) // use this session across requests
        .build();
  }

  protected MvcResult postAndGetRedirectUrl(String postUrl, Map<String, List<String>> params) throws Exception {
    return mockMvc.perform(post(postUrl)
        .with(csrf())
        .session(session)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .params(new LinkedMultiValueMap<>(params))
    ).andReturn();
  }

  protected ResultActions postToUrl(String postUrl, Map<String, List<String>> params) throws Exception {
    return mockMvc.perform(post(postUrl)
        .with(csrf())
        .session(session)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .params(new LinkedMultiValueMap<>(params))
    );
  }

  protected String getUrlForPageName(String pageName) {
    return "/flow/gcc/" + pageName;
  }

  protected ResultActions getFromUrl(String url) throws Exception {
    return mockMvc.perform(get(url)
        .session(session));
  }
}
