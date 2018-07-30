package net.petrikainulainen.wiremock.stubbing;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * This class demonstrates how we can configure the returned HTTP status code
 * and message.
 */
@DisplayName("Configure the returned HTTP status")
class HttpStatusTest {

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
    @DisplayName("When we configure the returned HTTP status code manually")
    class WhenWeConfigureReturnedStatusCodeManually {

        @Test
        @DisplayName("Should return the HTTP status code OK")
        void shouldReturnHttpStatusCodeOk() {
            givenThat(get(urlEqualTo("/api/message")).willReturn(aResponse()
                    .withStatus(200)
            ));

            String serverUrl = buildApiMethodUrl();
            ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("When we configure the returned HTTP status code by using factory methods")
    class WhenWeConfigureReturnedStatusCodeWithFactoryMethods {

        @Test
        @DisplayName("Should return the HTTP status code OK")
        void shouldReturnHttpStatusCodeOk() {
            givenThat(get(urlEqualTo("/api/message")).willReturn(ok()));

            String serverUrl = buildApiMethodUrl();
            ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Should return the HTTP status code created")
        void shouldReturnHttpStatusCodeCreated() {
            givenThat(get(urlEqualTo("/api/message")).willReturn(created()));

            String serverUrl = buildApiMethodUrl();
            ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }

        @Test
        @DisplayName("Should return the HTTP status code no content")
        void shouldReturnHttpStatusCodeNoContent() {
            givenThat(get(urlEqualTo("/api/message")).willReturn(noContent()));

            String serverUrl = buildApiMethodUrl();
            ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("Should return the HTTP status code bad request")
        void shouldReturnHttpStatusCodeBadRequest() {
            givenThat(get(urlEqualTo("/api/message")).willReturn(badRequest()));

            String serverUrl = buildApiMethodUrl();
            Throwable thrown = catchThrowable(() -> restTemplate.getForEntity(serverUrl, String.class));
            assertThat(thrown)
                    .isExactlyInstanceOf(HttpClientErrorException.class)
                    .hasMessage("400 Bad Request");
        }

        @Test
        @DisplayName("Should return the HTTP status code forbidden")
        void shouldReturnHttpStatusCodeForbidden() {
            givenThat(get(urlEqualTo("/api/message")).willReturn(forbidden()));

            String serverUrl = buildApiMethodUrl();
            Throwable thrown = catchThrowable(() -> restTemplate.getForEntity(serverUrl, String.class));
            assertThat(thrown)
                    .isExactlyInstanceOf(HttpClientErrorException.class)
                    .hasMessage("403 Forbidden");
        }

        @Test
        @DisplayName("Should return the HTTP status code internal server error")
        void shouldReturnHttpStatusCodeServerError() {
            givenThat(get(urlEqualTo("/api/message")).willReturn(serverError()));

            String serverUrl = buildApiMethodUrl();
            Throwable thrown = catchThrowable(() -> restTemplate.getForEntity(serverUrl, String.class));
            assertThat(thrown)
                    .isExactlyInstanceOf(HttpServerErrorException.class)
                    .hasMessage("500 Server Error");
        }

        @Test
        @DisplayName("Should return the HTTP status code not found")
        void shouldReturnHttpStatusCodeNotFound() {
            givenThat(get(urlEqualTo("/api/message")).willReturn(notFound()));

            String serverUrl = buildApiMethodUrl();
            Throwable thrown = catchThrowable(() -> restTemplate.getForEntity(serverUrl, String.class));
            assertThat(thrown)
                    .isExactlyInstanceOf(HttpClientErrorException.class)
                    .hasMessage("404 Not Found");
        }

        @Test
        @DisplayName("Should return the HTTP status code service unavailable")
        void shouldReturnHttpStatusCodeServiceUnavailable() {
            givenThat(get(urlEqualTo("/api/message")).willReturn(serviceUnavailable()));

            String serverUrl = buildApiMethodUrl();
            Throwable thrown = catchThrowable(() -> restTemplate.getForEntity(serverUrl, String.class));
            assertThat(thrown)
                    .isExactlyInstanceOf(HttpServerErrorException.class)
                    .hasMessage("503 Service Unavailable");
        }

        @Test
        @DisplayName("Should return the HTTP status code unauthorized")
        void shouldReturnHttpStatusCodeUnauthorized() {
            givenThat(get(urlEqualTo("/api/message")).willReturn(unauthorized()));

            String serverUrl = buildApiMethodUrl();
            Throwable thrown = catchThrowable(() -> restTemplate.getForEntity(serverUrl, String.class));
            assertThat(thrown)
                    .isExactlyInstanceOf(HttpClientErrorException.class)
                    .hasMessage("401 Unauthorized");
        }

        @Test
        @DisplayName("Should return the HTTP status code unprocessable entity")
        void shouldReturnHttpStatusCodeUnprocessableEntity() {
            givenThat(get(urlEqualTo("/api/message")).willReturn(badRequestEntity()));

            String serverUrl = buildApiMethodUrl();
            Throwable thrown = catchThrowable(() -> restTemplate.getForEntity(serverUrl, String.class));
            assertThat(thrown)
                    .isExactlyInstanceOf(HttpClientErrorException.class)
                    .hasMessage("422 Unprocessable Entity");
        }
    }

    @Nested
    @DisplayName("When we configure the returned HTTP status code and status message")
    class WhenWeConfigureReturnedStatusCodeAndMessage {

        @Test
        @DisplayName("Should return the HTTP status code internal server error and the correct message")
        void shouldReturnHttpStatusCodeOk() {
            givenThat(get(urlEqualTo("/api/message")).willReturn(aResponse()
                    .withStatus(500)
                    .withStatusMessage("I am sorry")
            ));

            String serverUrl = buildApiMethodUrl();
            Throwable thrown = catchThrowable(() -> restTemplate.getForEntity(serverUrl, String.class));
            assertThat(thrown)
                    .isExactlyInstanceOf(HttpServerErrorException.class)
                    .hasMessage("500 I am sorry");
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
