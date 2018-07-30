package net.petrikainulainen.wiremock.stubbing;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * This class demonstrates how we can configure the body of the
 * returned HTTP response.
 */
@DisplayName("Configure the body of the returned HTTP response")
class HttpResponseBodyTest {

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
    @DisplayName("When we configure the response body, content type, and status code manually")
    class WhenWeConfigureHttpResponseManually {

        @Nested
        @DisplayName("When we return a JSON document")
        class WhenWeReturnJsonDocument {

            @Test
            @DisplayName("Should return the configured HTTP response")
            void shouldReturnHttpConfiguredHttpResponse() {
                givenThat(get(urlEqualTo("/api/message")).willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json;charset=UTF-8")
                        .withBody("{ \"message\": \"Hello World!\" }")
                ));

                String serverUrl = buildApiMethodUrl();
                ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
                assertThat(response.getBody()).isEqualTo("{ \"message\": \"Hello World!\" }");
            }
        }

        @Nested
        @DisplayName("When we return an XML document")
        class WhenWeReturnXmlDocument {

            @Test
            @DisplayName("Should return the configured HTTP response")
            void shouldReturnHttpConfiguredHttpResponse() {
                givenThat(get(urlEqualTo("/api/message")).willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBody("<message>Hello World!</message>")
                ));

                String serverUrl = buildApiMethodUrl();
                ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_XML);
                assertThat(response.getBody()).isEqualTo("<message>Hello World!</message>");
            }
        }

        @Nested
        @DisplayName("When we return a plain text string")
        class WhenWeReturnPlainTextString {

            @Test
            @DisplayName("Should return the configured HTTP response")
            void shouldReturnHttpConfiguredHttpResponse() {
                givenThat(get(urlEqualTo("/api/message")).willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody("Hello World!")
                ));

                String serverUrl = buildApiMethodUrl();
                ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.TEXT_PLAIN);
                assertThat(response.getBody()).isEqualTo("Hello World!");
            }
        }
    }

    @Nested
    @DisplayName("When we configure the response body, content type, and status code by using factory methods")
    class WhenWeConfigureHttpResponseByUsingFactoryMethods {

        @Test
        @DisplayName("Should return OK response for JSON")
        void shouldReturnOkResponseForJson() {
            givenThat(get(urlEqualTo("/api/message")).willReturn(
                    okJson("{ \"message\": \"Hello World!\" }")
            ));

            String serverUrl = buildApiMethodUrl();
            ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
            assertThat(response.getBody()).isEqualTo("{ \"message\": \"Hello World!\" }");
        }

        @Test
        @DisplayName("Should return OK response for XML")
        void shouldReturnOkResponseForXml() {
            givenThat(get(urlEqualTo("/api/message")).willReturn(
                    okXml("<message>Hello World!</message>")
            ));

            String serverUrl = buildApiMethodUrl();
            ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_XML);
            assertThat(response.getBody()).isEqualTo("<message>Hello World!</message>");
        }

        @Test
        @DisplayName("Should return OK response for Text/XML")
        void shouldReturnOkResponseForTextXml() {
            givenThat(get(urlEqualTo("/api/message")).willReturn(
                    okTextXml("<message>Hello World!</message>")
            ));

            String serverUrl = buildApiMethodUrl();
            ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.TEXT_XML);
            assertThat(response.getBody()).isEqualTo("<message>Hello World!</message>");
        }
    }

    @Nested
    @DisplayName("When we read the response body from a file")
    class WhenWeReadResponseBodyFromFile {

        @Nested
        @DisplayName("When we return a JSON document")
        class WhenWeReturnJsonDocument {

            @Test
            @DisplayName("Should return the configured HTTP response")
            void shouldReturnHttpConfiguredHttpResponse() {
                givenThat(get(urlEqualTo("/api/message")).willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json;charset=UTF-8")
                        .withBodyFile("json/hello.json")
                ));

                String serverUrl = buildApiMethodUrl();
                ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
                assertThat(response.getBody()).isEqualTo("{ \"message\": \"Hello World!\" }");
            }
        }

        @Nested
        @DisplayName("When we return an XML document")
        class WhenWeReturnXmlDocument {

            @Test
            @DisplayName("Should return the configured HTTP response")
            void shouldReturnHttpConfiguredHttpResponse() {
                givenThat(get(urlEqualTo("/api/message")).willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/xml")
                        .withBodyFile("xml/hello.xml")
                ));

                String serverUrl = buildApiMethodUrl();
                ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_XML);
                assertThat(response.getBody()).isEqualTo("<message>Hello World!</message>");
            }
        }

        @Nested
        @DisplayName("When we return a plain text string")
        class WhenWeReturnPlainTextString {

            @Test
            @DisplayName("Should return the configured HTTP response")
            void shouldReturnHttpConfiguredHttpResponse() {
                givenThat(get(urlEqualTo("/api/message")).willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBodyFile("text/hello.txt")
                ));

                String serverUrl = buildApiMethodUrl();
                ResponseEntity<String> response = restTemplate.getForEntity(serverUrl, String.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.TEXT_PLAIN);
                assertThat(response.getBody()).isEqualTo("Hello World!");
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
