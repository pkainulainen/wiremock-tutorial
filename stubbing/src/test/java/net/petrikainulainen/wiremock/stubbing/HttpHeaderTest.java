package net.petrikainulainen.wiremock.stubbing;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class demonstrates how we can configure the HTTP headers
 * of the returned HTTP response
 */
@DisplayName("Configure the returned HTTP headers")
class HttpHeaderTest {

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
    @DisplayName("When we return one HTTP header")
    class WhenWeReturnOneHeader {

        @Test
        @DisplayName("Should return the configured HTTP header")
        void shouldReturnHttpStatusCodeOk() {
            givenThat(get(urlEqualTo("/api/message")).willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Name", "Petri Kainulainen")
            ));

            String serverUrl = buildApiMethodUrl();
            ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getHeaders().get("Name").get(0)).isEqualTo("Petri Kainulainen");
        }
    }

    @Nested
    @DisplayName("When we return multiple HTTP headers")
    class WhenWeReturnMultipleHttpHeaders {

        @Nested
        @DisplayName("When we configure HTTP headers individually")
        class WhenWeConfigureHttpHeadersIndividually {

            @Test
            @DisplayName("Should return the configured HTTP headers")
            void shouldReturnHttpStatusCodeOk() {
                givenThat(get(urlEqualTo("/api/message")).willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Name", "Petri Kainulainen")
                        .withHeader("Occupation", "Software Developer")
                ));

                String serverUrl = buildApiMethodUrl();
                ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getHeaders().get("Name").get(0)).isEqualTo("Petri Kainulainen");
                assertThat(response.getHeaders().get("Occupation").get(0)).isEqualTo("Software Developer");
            }
        }

        @Nested
        @DisplayName("When we configure HTTP headers at the same time")
        class WhenWeConfigureHttpHeadersAtSameTime {

            @Test
            @DisplayName("Should return the configured HTTP headers")
            void shouldReturnHttpStatusCodeOk() {
                HttpHeaders headers = new HttpHeaders(new HttpHeader("Name", "Petri Kainulainen"),
                        new HttpHeader("Occupation", "Software Developer")
                );

                givenThat(get(urlEqualTo("/api/message")).willReturn(aResponse()
                        .withStatus(200)
                        .withHeaders(headers)
                ));

                String serverUrl = buildApiMethodUrl();
                ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getHeaders().get("Name").get(0)).isEqualTo("Petri Kainulainen");
                assertThat(response.getHeaders().get("Occupation").get(0)).isEqualTo("Software Developer");
            }
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
