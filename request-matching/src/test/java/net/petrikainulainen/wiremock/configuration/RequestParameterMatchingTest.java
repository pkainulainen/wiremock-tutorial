package net.petrikainulainen.wiremock.configuration;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class demonstrates how we can use request matchers that
 * compare the actual request parameter value with the expected
 * request parameter value.
 */
@DisplayName("Compare the actual request parameter value with the expected request parameter value")
class RequestParameterMatchingTest {

    private RestTemplate restTemplate;
    private WireMockServer wireMockServer;

    @BeforeEach
    void configureSystemUnderTest() {
        this.restTemplate = new RestTemplate();
        this.wireMockServer = new WireMockServer(options()
                .dynamicPort()
        );
        this.wireMockServer.start();
        configureFor("localhost", this.wireMockServer.port());
    }

    @Nested
    @DisplayName("When we want to ensure that the actual value is equal to the expected value")
    class WhenWeWantToEnsureThatActualValueIsEqualToExpectedValue {

        @Test
        @DisplayName("Should compare the actual value with the exact expected value")
        void shouldCompareActualUrlWithExactExpectedUrl() {
            givenThat(get(urlPathEqualTo("/api/message"))
                    .withQueryParam("searchTerm", equalTo("foobar"))
                    .willReturn(aResponse().withStatus(200))
            );

            String apiMethodUrl = buildApiMethodUrl("foobar");

            ResponseEntity<String> response = restTemplate.getForEntity(apiMethodUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("When we want to ignore case and ensure that the actual value is equal to the expected value")
    class WhenWeWantToIgnoreCaseEnsureThatActualValueIsEqualToExpectedValue {

        @Test
        @DisplayName("Should compare the actual value with the exact expected value")
        void shouldCompareActualUrlWithExactExpectedUrl() {
            givenThat(get(urlPathEqualTo("/api/message"))
                    .withQueryParam("searchTerm", equalToIgnoreCase("FOOBAR"))
                    .willReturn(aResponse().withStatus(200))
            );

            String apiMethodUrl = buildApiMethodUrl("foobar");

            ResponseEntity<String> response = restTemplate.getForEntity(apiMethodUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("When we want to ensure that the actual value contains the expected value")
    class WhenWeWantEnsureThatActualValueContainsExpectedValue {

        @Test
        @DisplayName("Should ensure that the actual value contains the expected value")
        void shouldEnsureThatActualValueContainsExpectedValue() {
            givenThat(get(urlPathEqualTo("/api/message"))
                    .withQueryParam("searchTerm", containing("oba"))
                    .willReturn(aResponse().withStatus(200))
            );

            String apiMethodUrl = buildApiMethodUrl("foobar");

            ResponseEntity<String> response = restTemplate.getForEntity(apiMethodUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("When we want to ensure that the actual value matches with the given regex")
    class WhenWeWantToEnsureThatActualValueMatchesWithRegex {

        @Test
        @DisplayName("Should ensure that the actual value matches with the given regex")
        void shouldCompareActualUrlWithExactExpectedUrl() {
            givenThat(get(urlPathEqualTo("/api/message"))
                    .withQueryParam("searchTerm", matching("fo([a-z]{3})r"))
                    .willReturn(aResponse().withStatus(200))
            );

            String apiMethodUrl = buildApiMethodUrl("foobar");

            ResponseEntity<String> response = restTemplate.getForEntity(apiMethodUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("When we want to ensure that the actual value doesn't match with the given regex")
    class WhenWeWantToEnsureThatActualValueDoesNotMatchWithRegex {

        @Test
        @DisplayName("Should ensure that the actual value doesn't match with the given regex")
        void shouldCompareActualUrlWithExactExpectedUrl() {
            givenThat(get(urlPathEqualTo("/api/message"))
                    .withQueryParam("searchTerm", notMatching("fo([a-z]{1})r"))
                    .willReturn(aResponse().withStatus(200))
            );

            String apiMethodUrl = buildApiMethodUrl("foobar");

            ResponseEntity<String> response = restTemplate.getForEntity(apiMethodUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    private String buildApiMethodUrl(String searchTerm) {
        return String.format("http://localhost:%d/api/message?searchTerm=%s", this.wireMockServer.port(), searchTerm);
    }

    @AfterEach
    void stopWireMockServer() {
        this.wireMockServer.stop();
    }
}
