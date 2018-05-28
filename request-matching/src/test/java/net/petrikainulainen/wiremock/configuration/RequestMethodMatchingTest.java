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
 * compare the actual HTTP request method with the expected request method.
 */
@DisplayName("Compare the actual HTTP request with the expected request method")
class RequestMethodMatchingTest {

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
    @DisplayName("When we want to specify the request method")
    class WhenWeWantToSpecifyRequestMethod {

        @Test
        @DisplayName("Should compare the actual request method with the expected request method")
        void shouldCompareActualRequestMethodWithExpectedRequestMethod() {
            givenThat(get(anyUrl()).willReturn(aResponse()
                    .withStatus(200))
            );

            String serverUrl = buildApiMethodUrl(1L);
            ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("When we don't are about the request method")
    class WhenWeDoNotCareAboutRequestMethod {

        @Test
        @DisplayName("Should ignore request method")
        void shouldIgnoreRequestMethod() {
            givenThat(any(anyUrl()).willReturn(aResponse()
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
