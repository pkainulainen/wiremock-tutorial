package net.petrikainulainen.wiremock.configuration;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class demonstrates how we can use request matchers that
 * compare the actual basic authentication header value with the
 * expected basic authentication header value.
 */
@DisplayName("Compare the actual header value with the expected header value")
class BasicAuthMatchingTest {

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

    @Test
    @DisplayName("Should ensure that request has correct username and password")
    void shouldEnsureThatRequestHasCorrectUsernameAndPassword() {
        givenThat(get(urlEqualTo("/api/message?id=1"))
                .withBasicAuth("username", "password")
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

    private HttpEntity<String> createHttpRequest() {
        HttpHeaders headers = new HttpHeaders();

        String auth = "username:password";
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(Charset.forName("US-ASCII")) );
        String authHeader = "Basic " + new String( encodedAuth );
        headers.set( "Authorization", authHeader );

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
