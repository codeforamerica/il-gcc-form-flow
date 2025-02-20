package org.ilgcc.app.utils;

import static org.ilgcc.app.utils.constants.SessionKeys.SESSION_KEY_SUBMISSION_MAP;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@ContextConfiguration()
public abstract class AbstractMockMvcTest {

  @MockitoBean
  protected Clock clock;

  @MockitoBean
  protected Submission submission;

  @MockitoSpyBean
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
    session.setAttribute(SESSION_KEY_SUBMISSION_MAP, new HashMap<>());
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(this.webApplicationContext)
        .apply(sharedHttpSession()) // use this session across requests
        .build();
  }

  protected String postAndGetRedirectUrl(String postUrl, Map<String, List<String>> params) throws Exception {
    return mockMvc.perform(post(postUrl)
        .with(csrf())
        .session(session)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .params(new LinkedMultiValueMap<>(params))
    ).andReturn().getResponse().getRedirectedUrl();
  }

  protected ResultActions postAndGetRedirect(String postUrl, Map<String, List<String>> params) throws Exception {
    postToUrl(postUrl, params);
    String nextPage = postUrl + "/navigation";
    while (nextPage.contains("/navigation")) {
      // follow redirects
      nextPage = mockMvc.perform(get(nextPage).session(session))
          .andExpect(status().is3xxRedirection()).andReturn()
          .getResponse()
          .getRedirectedUrl();
    }
    return mockMvc.perform(get(nextPage));
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
