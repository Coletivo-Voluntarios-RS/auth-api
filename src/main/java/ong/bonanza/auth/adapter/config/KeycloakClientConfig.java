package ong.bonanza.auth.adapter.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import ong.bonanza.auth.adapter.client.keycloak.KeycloakClient;
import ong.bonanza.auth.adapter.provider.AuthenticationProvider;

@Configuration
public class KeycloakClientConfig {

    @Bean
    WebClient webClientKeycloak(@Value("${keycloak.url}") String url) {
        return WebClient.create(url);
    }

    @Bean
    public KeycloakClient keycloakClient(
            @Qualifier("webClientKeycloak") WebClient webClient,
            @Value("${keycloak.realm}") String realm,
            AuthenticationProvider authenticationProvider,
            @Value("${keycloak.client-id}") String clientId,
            @Value("${keycloak.client-secret}") String clientSecret) {
        return new KeycloakClient(webClient, realm, authenticationProvider, clientId, clientSecret);
    }

}
