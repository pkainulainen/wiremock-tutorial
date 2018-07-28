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
 * for a request body which contains an XML document.
 */
@DisplayName("Specify expectations for an XML document")
class XmlRequestBodyMatchingTest {

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

        @Test
        @DisplayName("Should compare the actual request body with the expected request body")
        void shouldCompareActualRequestBodyWithExpectedRequestBody() {
            givenThat(post(urlEqualTo("/api/message"))
                    .withRequestBody(equalToXml("<message>Hello World!</message>"))
                    .willReturn(aResponse().withStatus(200))
            );

            String apiMethodUrl = buildApiMethodUrl();
            HttpEntity<String> httpRequest = new HttpEntity<>("<message>Hello World!</message>");

            ResponseEntity<String> response = restTemplate.exchange(apiMethodUrl,
                    HttpMethod.POST,
                    httpRequest,
                    String.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("When we want to ensure that the actual request body contains an element")
    class WhenWeWantToEnsureThatActualRequestBodyContainsElement {

        @Test
        @DisplayName("Should ensure that the actual request body contains an element")
        void shouldEnsureThatActualRequestBodyContainsElement() {
            givenThat(post(urlEqualTo("/api/message"))
                    .withRequestBody(matchingXPath("/message"))
                    .willReturn(aResponse().withStatus(200))
            );

            String apiMethodUrl = buildApiMethodUrl();
            HttpEntity<String> httpRequest = new HttpEntity<>("<message>Hello World!</message>");

            ResponseEntity<String> response = restTemplate.exchange(apiMethodUrl,
                    HttpMethod.POST,
                    httpRequest,
                    String.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("When we want to ensure that the actual request body has an element with the expected value")
    class WhenWeWantToEnsureThatActualRequestBodyHasElementWithExpectedValue {

        @Nested
        @DisplayName("When we use XPath")
        class WhenWeUseXPath {

            @Test
            @DisplayName("Should ensure that the actual request body has an element with the expected value")
            void shouldEnsureThatActualRequestBodyHasElementWithExpectedValue() {
                givenThat(post(urlEqualTo("/api/message"))
                        .withRequestBody(matchingXPath("/message[text()='Hello World!']"))
                        .willReturn(aResponse().withStatus(200))
                );

                String apiMethodUrl = buildApiMethodUrl();
                HttpEntity<String> httpRequest = new HttpEntity<>("<message>Hello World!</message>");

                ResponseEntity<String> response = restTemplate.exchange(apiMethodUrl,
                        HttpMethod.POST,
                        httpRequest,
                        String.class
                );
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            }
        }

        @Nested
        @DisplayName("When we combine XPath with a matcher")
        class WhenWeCombineXPathWithMatcher {

            @Nested
            @DisplayName("When we specify our expectation for an element value")
            class WhenWeSpecifyOurExpectationForElementValue {

                @Test
                @DisplayName("Should ensure that the actual request body has an element with the expected value")
                void shouldEnsureThatActualRequestBodyHasElementWithExpectedValue() {
                    givenThat(post(urlEqualTo("/api/message"))
                            .withRequestBody(matchingXPath("/message/text()", equalTo("Hello World!")))
                            .willReturn(aResponse().withStatus(200))
                    );

                    String apiMethodUrl = buildApiMethodUrl();
                    HttpEntity<String> httpRequest = new HttpEntity<>("<message>Hello World!</message>");

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
                @DisplayName("Should ensure that the actual request body has an element with the expected sub-document")
                void shouldEnsureThatActualRequestBodyHasElementWithExpectedSubDocument() {
                    givenThat(post(urlEqualTo("/api/message"))
                            .withRequestBody(matchingXPath("/message/name", equalToXml("<name>Petri</name>")))
                            .willReturn(aResponse().withStatus(200))
                    );

                    String apiMethodUrl = buildApiMethodUrl();
                    HttpEntity<String> httpRequest = new HttpEntity<>("<message>" +
                                "<name>Petri</name>" +
                                "<text>Hello World!</text>" +
                            "</message>"
                    );

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
    @DisplayName("When we want to ensure that the document has X elements")
    class WhenWeWantToEnsureThatDocumentHasXElements {

        @Test
        @DisplayName("Should ensure that the document has X elements")
        void shouldEnsureThatActualRequestBodyHasXElements() {
            givenThat(post(urlEqualTo("/api/message"))
                    .withRequestBody(matchingXPath("/messages[count(message)=1]"))
                    .willReturn(aResponse().withStatus(200))
            );

            String apiMethodUrl = buildApiMethodUrl();
            HttpEntity<String> httpRequest = new HttpEntity<>("<messages><message>Hello World!</message></messages>");

            ResponseEntity<String> response = restTemplate.exchange(apiMethodUrl,
                    HttpMethod.POST,
                    httpRequest,
                    String.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("When the request body uses XML namespaces")
    class WhenRequestBodyUsesXmlNamespaces {

        @Test
        @DisplayName("Should compare the actual request body with the expected request body")
        void shouldCompareActualRequestBodyWithExpectedRequestBody() {
            givenThat(post(urlEqualTo("/api/message"))
                    .withRequestBody(matchingXPath("/sample:message[text()='Hello World!']")
                            .withXPathNamespace("sample", "http://www.example.com")
                    )
                    .willReturn(aResponse().withStatus(200))
            );

            String apiMethodUrl = buildApiMethodUrl();
            HttpEntity<String> httpRequest = new HttpEntity<>("<sample:message xmlns:sample=\"http://www.example.com\">" +
                        "Hello World!" +
                    "</sample:message>");

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
