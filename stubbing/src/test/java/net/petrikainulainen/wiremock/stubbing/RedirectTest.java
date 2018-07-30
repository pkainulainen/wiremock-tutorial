package net.petrikainulainen.wiremock.stubbing;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class demonstrates how we can configure a redirect with WireMock.
 */
@DisplayName("Configure a redirect with wiremock")
class RedirectTest {

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
    @DisplayName("When we create a permanent redirect")
    class WhenWeCreatePermanentRedirect {

        @Test
        @DisplayName("Should return a permanent redirect")
        void shouldReturnPermanentRedirect() {
            givenThat(get(urlEqualTo("/api/message")).willReturn(
                    permanentRedirect("https://www.testwithspring.com")
            ));

            String serverUrl = buildApiMethodUrl();
            ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.MOVED_PERMANENTLY);
            assertThat(response.getHeaders().getLocation().toString()).isEqualTo("https://www.testwithspring.com");
        }
    }

    @Nested
    @DisplayName("When we create a temporary redirect")
    class WhenWeCreateTemporaryRedirect {

        @Test
        @DisplayName("Should return a temporary redirect")
        void shouldReturnTemporaryRedirect() {
            givenThat(get(urlEqualTo("/api/message")).willReturn(
                    temporaryRedirect("https://www.testwithspring.com")
            ));

            String serverUrl = buildApiMethodUrl();
            ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
            assertThat(response.getHeaders().getLocation().toString()).isEqualTo("https://www.testwithspring.com");
        }
    }

    @Nested
    @DisplayName("When we create a see other redirect")
    class WhenWeCreateSeeOtherRedirect {

        @Test
        @DisplayName("Should return a see other redirect")
        void shouldReturnSeeOtherRedirect() {
            givenThat(get(urlEqualTo("/api/message")).willReturn(
                    seeOther("https://www.testwithspring.com")
            ));

            String serverUrl = buildApiMethodUrl();
            ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SEE_OTHER);
            assertThat(response.getHeaders().getLocation().toString()).isEqualTo("https://www.testwithspring.com");
        }
    }

    private String buildApiMethodUrl() {
        return String.format("http://localhost:%d/api/message", this.wireMockServer.port());
    }

    @AfterEach
    void stopWireMockServer() {
        this.wireMockServer.stop();
    }
}
