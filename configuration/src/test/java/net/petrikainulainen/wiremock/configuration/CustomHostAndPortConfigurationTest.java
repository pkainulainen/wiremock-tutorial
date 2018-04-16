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
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class demonstrates how we can configure the system
 * under test when we want to use a custom IP address
 * and a custom port.
 */
class CustomHostAndPortConfigurationTest {

    private RestTemplate restTemplate;
    private WireMockServer wireMockServer;

    @BeforeEach
    void configureSystemUnderTest() {
        this.restTemplate = new RestTemplate();
        this.wireMockServer = new WireMockServer(options()
                .bindAddress("127.0.0.1")
                .port(9090)
        );
        this.wireMockServer.start();
        configureFor("127.0.0.1", 9090);
    }

    @Test
    @DisplayName("Should ensure that WireMock server was started")
    void shouldEnsureThatServerWasStarted() {
        givenThat(get(urlEqualTo("/")).willReturn(aResponse()
                .withStatus(200)
        ));

        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:9090", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @AfterEach
    void stopWireMockServer() {
        this.wireMockServer.stop();
    }
}
