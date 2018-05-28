package net.petrikainulainen.wiremock.configuration;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class demonstrates how we can use request matchers that
 * compare the actual URL with the expected URL.
 */
@DisplayName("Compare the actual URL with the expected URL")
class UrlMatchingTest {

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
    @DisplayName("When we want to compare the full URL")
    class WhenWeWantToCompareFullUrl {

        @Test
        @DisplayName("Should compare the actual URL with the exact expected URL")
        void shouldCompareActualUrlWithExactExpectedUrl() {
            givenThat(get(urlEqualTo("/api/message?id=1")).willReturn(aResponse()
                    .withStatus(200))
            );

            String serverUrl = buildApiMethodUrl(1L);
            ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Should compare the actual URL with the expected URL regex")
        void shouldCompareActualUrlWithExpectedUrlRegex() {
            givenThat(get(urlMatching("/api/([a-z]*)\\?id=1")).willReturn(aResponse()
                    .withStatus(200))
            );

            String serverUrl = buildApiMethodUrl(1L);
            ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("When we want to compare the URL path")
    class WhenWeWantToCompareUrlPath {

        @Test
        @DisplayName("Should compare the actual URL path with the exact expected URL path")
        void shouldCompareActualUrlWithExactExpectedUrl() {
            givenThat(get(urlPathEqualTo("/api/message")).willReturn(aResponse()
                    .withStatus(200))
            );

            String serverUrl = buildApiMethodUrl(1L);
            ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Should compare the actual URL path with the expected URL path regex")
        void shouldCompareActualUrlWithExpectedUrlRegex() {
            givenThat(get(urlPathMatching("/api/([a-z]*)")).willReturn(aResponse()
                    .withStatus(200))
            );

            String serverUrl = buildApiMethodUrl(1L);
            ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    private String buildApiMethodUrl(Long messageId) {
        return String.format("http://localhost:%d/api/message?id=%d", this.wireMockServer.port(), messageId);
    }

    @AfterEach
    void stopWireMockServer() {
        this.wireMockServer.stop();
    }
}
