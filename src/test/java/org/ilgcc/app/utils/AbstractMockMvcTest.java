package org.ilgcc.app.utils;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Clock;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static formflow.library.FormFlowController.SUBMISSION_MAP_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
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

  protected void setFlowInfoInSession(MockHttpSession mockHttpSession, Object... flowInfo) {
    if (flowInfo.length % 2 != 0) {
      throw new IllegalArgumentException("Arguments should be paired flowName -> submission id (UUID).");
    }
    Iterator<Object> iterator = Arrays.stream(flowInfo).iterator();
    Map<String, Object> flowMap = new HashMap<>();
    while (iterator.hasNext()) {
      String flowName = (String) iterator.next();
      UUID submissionId = (UUID) iterator.next();
      flowMap.put(flowName, submissionId);
    }
    mockHttpSession.setAttribute(SUBMISSION_MAP_NAME, flowMap);
  }

  protected Map<String, byte[]> getZipContentsMap(byte[] zipBytes) throws IOException {
    Map<String, byte[]> contents = new HashMap<>();

    try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
      ZipEntry entry;

      while ((entry = zis.getNextEntry()) != null) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;

        while ((len = zis.read(buffer)) > 0) {
          outputStream.write(buffer, 0, len);
        }

        contents.put(entry.getName(), outputStream.toByteArray());
        zis.closeEntry();
      }
    }

    return contents;
  }

  @Deprecated // assumes `pageName` is within `testFlow` config
  protected ResultActions postExpectingSuccess(String pageName, Map<String, List<String>> params) throws Exception {
    String postUrl = getUrlForPageName(pageName);
    return postToUrlExpectingSuccess(postUrl, postUrl + "/navigation", params);
  }

  // Appends the id to the post URL
  protected ResultActions postToUrlExpectingSuccess(String postUrl, String redirectUrl,
                                                    Map<String, List<String>> params, String uuid) throws Exception {
    String uuidString = uuid == null ? "" : '/' + uuid;
    return postToUrlExpectingSuccess(postUrl + uuidString, redirectUrl, params);
  }


  protected MvcResult postAndGetRedirectUrl(String postUrl, Map<String, List<String>> params) throws Exception {
    return mockMvc.perform(post(postUrl)
        .with(csrf())
        .session(session)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .params(new LinkedMultiValueMap<>(params))
    ).andReturn();
  }

  protected ResultActions postToUrlExpectingSuccess(String postUrl, String redirectUrl,
                                                    Map<String, List<String>> params) throws Exception {
    return mockMvc.perform(post(postUrl)
        .with(csrf())
        .session(session)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .params(new LinkedMultiValueMap<>(params))
    ).andExpect(redirectedUrl(redirectUrl));
  }

  // Same as postToUrlExpectingSuccess, but expecting a pattern instead of an exact match
  protected ResultActions postToUrlExpectingSuccessRedirectPattern(String postUrl, String redirectUrlPattern,
                                                                   Map<String, List<String>> params) throws Exception {
    return mockMvc.perform(post(postUrl)
        .with(csrf())
        .session(session)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .params(new LinkedMultiValueMap<>(params))
    ).andExpect(redirectedUrlPattern(redirectUrlPattern));
  }

  protected String getUrlExpectingSuccessRedirectPattern(String getUrl) throws Exception {
    return mockMvc.perform(get(getUrl).session(session))
        .andExpect(status().is3xxRedirection()).andReturn()
        .getResponse()
        .getRedirectedUrl();
  }

  protected void postExpectingNextPageTitle(String pageName,
                                            String inputName,
                                            String value,
                                            String nextPageTitle) throws Exception {
    String postUrl = getUrlForPageName(pageName);
    postToUrlExpectingSuccess(postUrl, postUrl + "/navigation", Map.of(inputName, List.of(value)));
    FormScreen nextPage = followRedirectsForPageName("testFlow", pageName);
    assertThat(nextPage.getTitle()).isEqualTo(nextPageTitle);
  }

  protected void continueExpectingNextPageTitle(String flow, String currentPageName, String nextPageTitle)
      throws Exception {
    var nextPage = followRedirectsForPageName(flow, currentPageName);
    assertThat(nextPage.getTitle()).isEqualTo(nextPageTitle);
  }

  protected ResultActions postExpectingFailure(String pageName, String inputName, String value)
      throws Exception {
    String postUrl = getUrlForPageName(pageName);
    return mockMvc.perform(
        post(postUrl)
            .session(session)
            .with(csrf())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .param(inputName, value)
    ).andExpect(redirectedUrl(postUrl));
  }

  protected ResultActions postToUrlExpectingFailure(String postUrl, Map<String, List<String>> params)
      throws Exception {
    return mockMvc.perform(
        post(postUrl)
            .session(session)
            .with(csrf())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .params(new LinkedMultiValueMap<>(params))
    );
  }

  protected ResultActions postExpectingFailure(String pageName, Map<String, List<String>> params,
                                               String redirectUrl) throws Exception {

    String postUrl = getUrlForPageName(pageName);
    MockHttpServletRequestBuilder post = post(postUrl)
        .session(session)
        .with(csrf())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .params(new LinkedMultiValueMap<>(params));

    return mockMvc.perform(post).andExpect(redirectedUrl(getUrlForPageName(redirectUrl)));
  }

  protected ResultActions postExpectingFailures(String pageName, Map<String, String> params)
      throws Exception {

    String postUrl = getUrlForPageName(pageName);
    MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
    params.forEach(multiValueMap::add);
    return mockMvc.perform(
        post(postUrl)
            .with(csrf())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .params(multiValueMap)
    ).andExpect(redirectedUrl(postUrl));
  }

  protected void postExpectingFailureAndAssertErrorDisplaysForThatInput(String pageName,
                                                                        String inputName,
                                                                        String value, String errorMessage) throws Exception {
    postExpectingFailure(pageName, inputName, value);
    assertPageHasInputError(pageName, inputName, errorMessage);
  }

  protected void postExpectingFailureAndAssertErrorsDisplaysForThatInput(String pageName,
                                                                         String inputName,
                                                                         String value, Integer numOfErrors) throws Exception {
    postExpectingFailure(pageName, inputName, value);
    assertEquals(numOfErrors, new FormScreen(getPage(pageName)).getInputErrors(inputName).size());
  }

  protected void postExpectingFailureAndAssertErrorsDisplayForThatInput(String pageName,
                                                                        String inputName,
                                                                        String value, List<String> errorMessages) throws Exception {
    postExpectingFailure(pageName, inputName, value);
    assertInputHasErrors(pageName, inputName, errorMessages);
  }

  protected void postExpectingFailureAndAssertInputErrorMessages(String pageName,
                                                                 Map<String, String> inputParams,
                                                                 Map<String, List<String>> expectedErrorMessages) throws Exception {
    postExpectingFailures(pageName, inputParams);
    for (String inputName : inputParams.keySet()) {
      assertInputHasErrors(pageName, inputName, expectedErrorMessages.get(inputName));
    }
  }

  protected ResultActions postWithoutData(String pageName) throws Exception {
    String postUrl = getUrlForPageName(pageName);
    return mockMvc.perform(
        post(postUrl)
            .with(csrf())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    );
  }

  protected String getUrlForPageName(String pageName) {
    return "/flow/gcc/" + pageName;
  }

  protected void assertPageHasInputError(String pageName, String inputName, String errorMessage)
      throws Exception {
    var page = new FormScreen(getPage(pageName));
    assertEquals(errorMessage, page.getInputError(inputName).text());
  }

  protected void assertInputHasErrors(String pageName, String inputName, List<String> errorMessages)
      throws Exception {
    var page = new FormScreen(getPage(pageName));
    // make sure there are errors returned
    assertFalse(page.getInputErrors(inputName).isEmpty(), "Expected errors on page, but there were none");
    // assert equal amount
    assertEquals(errorMessages.size(), page.getInputErrors(inputName).size(),
        "The page has a different number of errors than expected");
    // now check if they match the expected ones.
    assertTrue(errorMessages.containsAll(page.getInputErrors(inputName).stream().map(Element::ownText).toList()),
        "The error messages expected do match what was returned");
  }

  @NotNull
  protected ResultActions getPage(String pageName) throws Exception {
    MockHttpServletRequestBuilder get = get("/flow/gcc/" + pageName).session(session);
    return mockMvc.perform(get);
  }

  protected ResultActions getUrl(String url) throws Exception {
    MockHttpServletRequestBuilder get = get(url).session(session);
    return mockMvc.perform(get);
  }

  @NotNull
  protected ResultActions getPageExpectingSuccess(String pageName) throws Exception {
    return getPage(pageName).andExpect(status().isOk());
  }

  /**
   * Accepts the page you are on and follows the redirects to get the next page
   *
   * @param currentPageName the page
   * @return a form page that can be asserted against
   */
  protected FormScreen followRedirectsForPageName(String flow, String currentPageName) throws Exception {
    var nextPage = "/flow/%s/%s/navigation".formatted(flow, currentPageName);
    while (Objects.requireNonNull(nextPage).contains("/navigation")) {
      // follow redirects
      nextPage = mockMvc.perform(get(nextPage).session(session))
          .andExpect(status().is3xxRedirection()).andReturn()
          .getResponse()
          .getRedirectedUrl();
    }
    return new FormScreen(mockMvc.perform(get(nextPage)));
  }

  protected String followRedirectsForUrl(String currentPageUrl) throws Exception {
    var nextPage = currentPageUrl;
    while (Objects.requireNonNull(nextPage).contains("/navigation")) {
      // follow redirects
      nextPage = mockMvc.perform(get(nextPage).session(session))
          .andExpect(status().is3xxRedirection()).andReturn()
          .getResponse()
          .getRedirectedUrl();
    }
    return nextPage;
  }

  protected FormScreen postAndFollowRedirect(String pageName, Map<String, List<String>> params) throws Exception {
    postExpectingSuccess(pageName, params);
    return followRedirectsForPageName("gcc", pageName);
  }

  protected Map<String, Object> getMostRecentlyCreatedIterationData(MockHttpSession session, String flow, String subflow) {
    UUID testSubflowLogicUUID = ((Map<String, UUID>) session.getAttribute(SUBMISSION_MAP_NAME)).get(flow);
    Submission submission = submissionRepositoryService.findById(testSubflowLogicUUID).get();
    List<Map<String, Object>> iterationsAfterFirstPost = (List<Map<String, Object>>) submission.getInputData().get(subflow);
    return iterationsAfterFirstPost.get(iterationsAfterFirstPost.size() - 1);
  }
}
