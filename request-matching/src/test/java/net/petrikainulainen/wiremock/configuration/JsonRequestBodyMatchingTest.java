package net.petrikainulainen.wiremock.configuration;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class demonstrates how we can specify our expectations
 * for a request body which contains a JSON document.
 */
@DisplayName("Specify expectations for a JSON document")
class JsonRequestBodyMatchingTest {

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
    @DisplayName("When we want to ensure that the actual request body is equal to the expected request body")
    class WhenWeWantToEnsureThatActualBodyIsEqualToExpectedBody {

        @Nested
        @DisplayName("When we don't ignore additional attributes and array ordering")
        class WhenWeIgnoreAdditionalAttributesAndArrayOrdering {

            @Test
            @DisplayName("Should compare the actual request body with the expected request body")
            void shouldCompareActualRequestBodyWithExpectedRequestBody() {
                givenThat(post(urlEqualTo("/api/message"))
                        .withRequestBody(equalToJson("{\"message\": \"Hello World!\"}"))
                        .willReturn(aResponse().withStatus(200))
                );

                String apiMethodUrl = buildApiMethodUrl();
                HttpEntity<String> httpRequest = new HttpEntity<>("{\"message\": \"Hello World!\"}");

                ResponseEntity<String> response = restTemplate.exchange(apiMethodUrl,
                        HttpMethod.POST,
                        httpRequest,
                        String.class
                );
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            }
        }

        @Nested
        @DisplayName("When we ignore additional attributes")
        class WhenWeIgnoreAdditionalAttributes  {

            @Test
            @DisplayName("Should compare the sactual request body with the expected request body")
            void shouldCompareActualRequestBodyWithExpectedRequestBody() {
                givenThat(post(urlEqualTo("/api/message"))
                        .withRequestBody(equalToJson("{\"message\": \"Hello World!\"}", false, true))
                        .willReturn(aResponse().withStatus(200))
                );

                String apiMethodUrl = buildApiMethodUrl();
                HttpEntity<String> httpRequest = new HttpEntity<>("{\"name\": \"Petri Kainulainen\", \"message\": \"Hello World!\"}");

                ResponseEntity<String> response = restTemplate.exchange(apiMethodUrl,
                        HttpMethod.POST,
                        httpRequest,
                        String.class
                );
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            }
        }

        @Nested
        @DisplayName("When we ignore array ordering")
        class WhenWeIgnoreArrayOrdering  {

            @Test
            @DisplayName("Should compare the actual request body with the expected request body")
            void shouldCompareActualRequestBodyWithExpectedRequestBody() {
                givenThat(post(urlEqualTo("/api/message"))
                        .withRequestBody(equalToJson("{\"messages\": [\"Hello World!\", \"foobar\"]}", true, false))
                        .willReturn(aResponse().withStatus(200))
                );

                String apiMethodUrl = buildApiMethodUrl();
                HttpEntity<String> httpRequest = new HttpEntity<>("{\"messages\": [\"foobar\", \"Hello World!\"]}");

                ResponseEntity<String> response = restTemplate.exchange(apiMethodUrl,
                        HttpMethod.POST,
                        httpRequest,
                        String.class
                );
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            }
        }
    }

    @Nested
    @DisplayName("When we want to ensure that the actual request body contains an attribute")
    class WhenWeWantToEnsureThatActualRequestBodyContainsAttribute {

        @Test
        @DisplayName("Should ensure that the actual request body contains an attribute")
        void shouldEnsureThatActualRequestBodyContainsAttribute() {
            givenThat(post(urlEqualTo("/api/message"))
                    .withRequestBody(matchingJsonPath("$.message"))
                    .willReturn(aResponse().withStatus(200))
            );

            String apiMethodUrl = buildApiMethodUrl();
            HttpEntity<String> httpRequest = new HttpEntity<>("{\"message\": \"Hello World!\"}");

            ResponseEntity<String> response = restTemplate.exchange(apiMethodUrl,
                    HttpMethod.POST,
                    httpRequest,
                    String.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("When we want to ensure that the actual request body has an attribute with the expected value")
    class WhenWeWantToEnsureThatActualRequestBodyHasAttributeWithExpectedValue {

        @Nested
        @DisplayName("When we use JsonPath")
        class WhenWeUseJsonPath {

            @Test
            @DisplayName("Should ensure that the actual request body has an attribute with the expected value")
            void shouldEnsureThatActualRequestBodyHasAttributeWithExpectedValue() {
                givenThat(post(urlEqualTo("/api/message"))
                        .withRequestBody(matchingJsonPath("$.[?(@.message == 'Hello World!')]"))
                        .willReturn(aResponse().withStatus(200))
                );

                String apiMethodUrl = buildApiMethodUrl();
                HttpEntity<String> httpRequest = new HttpEntity<>("{\"message\": \"Hello World!\"}");

                ResponseEntity<String> response = restTemplate.exchange(apiMethodUrl,
                        HttpMethod.POST,
                        httpRequest,
                        String.class
                );
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            }
        }

        @Nested
        @DisplayName("When we combine JsonPath with a matcher")
        class WhenWeCombineJsonPathWithMatcher {

            @Nested
            @DisplayName("When we specify our expectation for an attribute value")
            class WhenWeSpecifyOurExpectationForAttributeValue {

                @Test
                @DisplayName("Should ensure that the actual request body has an attribute with the expected value")
                void shouldEnsureThatActualRequestBodyHasAttributeWithExpectedValue() {
                    givenThat(post(urlEqualTo("/api/message"))
                            .withRequestBody(matchingJsonPath("$.message", equalTo("Hello World!")))
                            .willReturn(aResponse().withStatus(200))
                    );

                    String apiMethodUrl = buildApiMethodUrl();
                    HttpEntity<String> httpRequest = new HttpEntity<>("{\"message\": \"Hello World!\"}");

                    ResponseEntity<String> response = restTemplate.exchange(apiMethodUrl,
                            HttpMethod.POST,
                            httpRequest,
                            String.class
                    );
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                }
            }

            @Nested
            @DisplayName("When we specify our expectation for a sub-document")
            class WhenWeSpecifyOurExpectationForSubDocument {

                @Test
                @DisplayName("Should ensure that the actual request body has an attribute with the expected sub-document")
                void shouldEnsureThatActualRequestBodyHasAttributeWithExpectedSubDocument() {
                    givenThat(post(urlEqualTo("/api/message"))
                            .withRequestBody(matchingJsonPath("$.message", equalToJson("{\"name\": \"Petri\", \"text\": \"Hello World!\"}")))
                            .willReturn(aResponse().withStatus(200))
                    );

                    String apiMethodUrl = buildApiMethodUrl();
                    HttpEntity<String> httpRequest = new HttpEntity<>("" +
                            "{\"message\": " +
                                "{\"name\": \"Petri\", \"text\": \"Hello World!\"}" +
                            "}");

                    ResponseEntity<String> response = restTemplate.exchange(apiMethodUrl,
                            HttpMethod.POST,
                            httpRequest,
                            String.class
                    );
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                }
            }
        }
    }

    @Nested
    @DisplayName("When we want to ensure that the attribute size is equal to the expected value")
    class WhenWeWantToEnsureThatAttributeSizeIsEqualToExpectedValue {

        @Test
        @DisplayName("Should ensure that the attribute size is equal to the expected value")
        void shouldEnsureThatActualRequestBodyHasAttributeWithExpectedValue() {
            givenThat(post(urlEqualTo("/api/message"))
                    .withRequestBody(matchingJsonPath("$[?(@.messages.size() == 1)]"))
                    .willReturn(aResponse().withStatus(200))
            );

            String apiMethodUrl = buildApiMethodUrl();
            HttpEntity<String> httpRequest = new HttpEntity<>("{\"messages\": [\"Hello World!\"]}");

            ResponseEntity<String> response = restTemplate.exchange(apiMethodUrl,
                    HttpMethod.POST,
                    httpRequest,
                    String.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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
