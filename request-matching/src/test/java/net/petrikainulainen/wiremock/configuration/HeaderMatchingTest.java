package net.petrikainulainen.wiremock.configuration;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class demonstrates how we can use request matchers that
 * compare the actual header value with the expected header value.
 */
@DisplayName("Compare the actual header value with the expected header value")
class HeaderMatchingTest {

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
            givenThat(get(urlEqualTo("/api/message?id=1"))
                    .withHeader("Accept", equalTo(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .willReturn(aResponse().withStatus(200))
            );

            String apiMethodUrl = buildApiMethodUrl(1L);
            HttpEntity<String> httpRequest = createHttpRequest();

            ResponseEntity<String> response = restTemplate.exchange(apiMethodUrl,
                    HttpMethod.GET,
                    httpRequest,
                    String.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("When we want to ignore case and ensure that the actual value is equal to the expected value")
    class WhenWeWantToIgnoreCaseEnsureThatActualValueIsEqualToExpectedValue {

        @Test
        @DisplayName("Should compare the actual value with the exact expected value")
        void shouldCompareActualUrlWithExactExpectedUrl() {
            givenThat(get(urlEqualTo("/api/message?id=1"))
                    .withHeader("Accept", equalToIgnoreCase(MediaType.APPLICATION_JSON_UTF8_VALUE.toUpperCase()))
                    .willReturn(aResponse().withStatus(200))
            );

            String apiMethodUrl = buildApiMethodUrl(1L);
            HttpEntity<String> httpRequest = createHttpRequest();

            ResponseEntity<String> response = restTemplate.exchange(apiMethodUrl,
                    HttpMethod.GET,
                    httpRequest,
                    String.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("When we want to ensure that the actual value contains the expected value")
    class WhenWeWantEnsureThatActualValueContainsExpectedValue {

        @Test
        @DisplayName("Should ensure that the actual value contains the expected value")
        void shouldEnsureThatActualValueContainsExpectedValue() {
            givenThat(get(urlEqualTo("/api/message?id=1"))
                    .withHeader("Accept", containing("/json"))
                    .willReturn(aResponse().withStatus(200))
            );

            String apiMethodUrl = buildApiMethodUrl(1L);
            HttpEntity<String> httpRequest = createHttpRequest();

            ResponseEntity<String> response = restTemplate.exchange(apiMethodUrl,
                    HttpMethod.GET,
                    httpRequest,
                    String.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("When we want to ensure that the actual value matches with the given regex")
    class WhenWeWantToEnsureThatActualValueMatchesWithRegex {

        @Test
        @DisplayName("Should ensure that the actual value matches with the given regex")
        void shouldCompareActualUrlWithExactExpectedUrl() {
            givenThat(get(urlEqualTo("/api/message?id=1"))
                    .withHeader("Accept", matching("application/([a-z]{4});charset=UTF-8"))
                    .willReturn(aResponse().withStatus(200))
            );

            String apiMethodUrl = buildApiMethodUrl(1L);
            HttpEntity<String> httpRequest = createHttpRequest();

            ResponseEntity<String> response = restTemplate.exchange(apiMethodUrl,
                    HttpMethod.GET,
                    httpRequest,
                    String.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("When we want to ensure that the actual value doesn't match with the given regex")
    class WhenWeWantToEnsureThatActualValueDoesNotMatchWithRegex {

        @Test
        @DisplayName("Should ensure that the actual value doesn't match with the given regex")
        void shouldCompareActualUrlWithExactExpectedUrl() {
            givenThat(get(urlEqualTo("/api/message?id=1"))
                    .withHeader("Accept", notMatching("application/([a-z]{1});charset=UTF-8"))
                    .willReturn(aResponse().withStatus(200))
            );

            String apiMethodUrl = buildApiMethodUrl(1L);
            HttpEntity<String> httpRequest = createHttpRequest();

            ResponseEntity<String> response = restTemplate.exchange(apiMethodUrl,
                    HttpMethod.GET,
                    httpRequest,
                    String.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    private HttpEntity<String> createHttpRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));

        return new HttpEntity<>(headers);
    }

    private String buildApiMethodUrl(Long messageId) {
        return String.format("http://localhost:%d/api/message?id=%d", this.wireMockServer.port(), messageId);
    }

    @AfterEach
    void stopWireMockServer() {
        this.wireMockServer.stop();
    }
}
