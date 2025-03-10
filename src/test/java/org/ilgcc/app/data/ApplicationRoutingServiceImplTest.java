package org.ilgcc.app.data;

import org.ilgcc.app.submission.router.ApplicationRoutingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ApplicationRoutingServiceImplTest {

  @Mock
  private CCMSDataService ccmsDataService;

  @InjectMocks
  private ApplicationRoutingServiceImpl applicationRoutingServiceImpl;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldReturnEmptyListWhenNoActiveCaseLoadCodes() {
    applicationRoutingServiceImpl.activeCaseLoadCodes = Collections.emptyList();

    List<County> result = applicationRoutingServiceImpl.getActiveCountiesByCaseLoadCodes();

    assertEquals(0, result.size());
  }

  @Test
  void shouldReturnEmptyListWhenNoCountiesFound() {
    applicationRoutingServiceImpl.activeCaseLoadCodes = List.of("CODE1");
    when(ccmsDataService.getCountiesByCaseloadCode("CODE1")).thenReturn(Collections.emptyList());

    List<County> result = applicationRoutingServiceImpl.getActiveCountiesByCaseLoadCodes();

    assertEquals(0, result.size());
  }

  @Test
  void shouldReturnUniqueAndSortedCounties() {
    applicationRoutingServiceImpl.activeCaseLoadCodes = List.of("CODE1", "CODE2");

    County countyA = new County(new BigInteger("12345"), "AlphaCity", "Alpha", 100, 200, "CODE1");
    County countyB = new County(new BigInteger("67890"), "BetaCity", "Beta", 101, 201, "CODE2");
    County countyC = new County(new BigInteger("11111"), "GammaCity", "Gamma", 102, 202, "CODE1");
    County duplicateCountyA = new County(new BigInteger("12345"), "AlphaCity", "Alpha", 100, 200, "CODE2"); // Duplicate entry

    when(ccmsDataService.getCountiesByCaseloadCode("CODE1")).thenReturn(Arrays.asList(countyC, countyA));
    when(ccmsDataService.getCountiesByCaseloadCode("CODE2")).thenReturn(Arrays.asList(countyB, duplicateCountyA));

    List<County> result = applicationRoutingServiceImpl.getActiveCountiesByCaseLoadCodes();

    assertEquals(3, result.size());
    assertEquals("Alpha", result.get(0).getCounty());
    assertEquals("Beta", result.get(1).getCounty());
    assertEquals("Gamma", result.get(2).getCounty());
  }
}
