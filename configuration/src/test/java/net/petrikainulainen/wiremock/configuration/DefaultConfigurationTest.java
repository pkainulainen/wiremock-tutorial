package net.petrikainulainen.wiremock.configuration;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class demonstrates how we can configure the system
 * under test when we want to use default WireMock configuration.
 */
class DefaultConfigurationTest {

    private RestTemplate restTemplate;
    private WireMockServer wireMockServer;

    @BeforeEach
    void configureSystemUnderTest() {
        this.restTemplate = new RestTemplate();
        this.wireMockServer = new WireMockServer();
        this.wireMockServer.start();

        //Normally we don't have to do this if we use the default configuration.
        //However, because other tests make changes to the global WireMock configuration,
        //we have to make the same changes here because we don't know the invocation
        //order of our test classes.
        configureFor("localhost", 8080);
    }

    @Test
    @DisplayName("Should ensure that WireMock server was started")
    void shouldEnsureThatServerWasStarted() {
        givenThat(get(urlEqualTo("/")).willReturn(aResponse()
                .withStatus(200)
        ));

        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @AfterEach
    void stopWireMockServer() {
        this.wireMockServer.stop();
    }
}
