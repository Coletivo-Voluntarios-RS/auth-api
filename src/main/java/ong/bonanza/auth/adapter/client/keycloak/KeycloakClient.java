package ong.bonanza.auth.adapter.client.keycloak;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.BodyInserters.FormInserter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;
import ong.bonanza.auth.adapter.client.keycloak.dto.TokenDTO;
import ong.bonanza.auth.adapter.client.keycloak.dto.UserDTO;
import ong.bonanza.auth.adapter.client.keycloak.dto.UserInfoDTO;
import ong.bonanza.auth.adapter.client.keycloak.dto.UserRegisterDTO;
import ong.bonanza.auth.adapter.provider.AuthenticationProvider;
import ong.bonanza.auth.application.exception.ConflictException;
import ong.bonanza.auth.application.exception.ForbiddenException;
import ong.bonanza.auth.application.exception.UnauthorizedException;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class KeycloakClient {

    private final WebClient webClient;

    private final String realm;

    private final AuthenticationProvider authenticationProvider;

    private final String clientId;

    private final String clientSecret;

    private final Pattern USER_ID = Pattern.compile("(?<=(.*\\/users\\/)).*");

    public TokenDTO autenticar(String username, String password) {
        return autenticar(
                realm,
                BodyInserters.fromFormData("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("username", username)
                        .with("password", password)
                        .with("grant_type", "password"));
    }

    public Optional<UserInfoDTO> userInfo() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/realms/{realm}/protocol/openid-connect/userinfo")
                        .build(realm))
                .header("Authorization", String.format("Bearer %s", authenticationProvider.token()))
                .retrieve()
                .bodyToMono(UserInfoDTO.class)
                .onErrorResume(WebClientResponseException.NotFound.class, notFound -> Mono.empty())
                .onErrorResume(WebClientResponseException.Unauthorized.class,
                        forbidden -> Mono.error(new UnauthorizedException()))
                .onErrorResume(WebClientResponseException.Forbidden.class,
                        forbidden -> Mono.error(new ForbiddenException("acessar user-info")))
                .blockOptional();
    }

    public Optional<UserDTO> buscarUsuarioPorId(UUID id) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("admin/realms/{realm}/users/{id}")
                        .build(realm, id))
                .header("Authorization", String.format("Bearer %s", authenticationProvider.token()))
                .retrieve()
                .bodyToMono(UserDTO.class)
                .onErrorResume(WebClientResponseException.NotFound.class, notFound -> Mono.empty())
                .onErrorResume(WebClientResponseException.Unauthorized.class,
                        forbidden -> Mono.error(new UnauthorizedException()))
                .onErrorResume(WebClientResponseException.Forbidden.class,
                        forbidden -> Mono.error(new ForbiddenException("buscar por usuario por ID")))
                .blockOptional();
    }

    public List<UserDTO> buscarUsuariosPorEmail(String email, Integer max) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("admin/realms/{realm}/users")
                        .queryParam("email", email)
                        .queryParam("max", max)
                        .build(realm))
                .header("Authorization", String.format("Bearer %s", authenticationProvider.token()))
                .retrieve()
                .bodyToFlux(UserDTO.class)
                .onErrorResume(WebClientResponseException.Unauthorized.class,
                        forbidden -> Mono.error(new UnauthorizedException()))
                .onErrorResume(WebClientResponseException.Forbidden.class,
                        forbidden -> Mono.error(new ForbiddenException("buscar usuarios")))
                .collectList()
                .block();
    }

    public UserDTO criarUsuario(UserRegisterDTO userRegistration) {
        ResponseEntity<Void> response = webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/admin/realms/{realm}/users")
                        .build(realm))
                .header("Authorization", String.format("Bearer %s", autenticarClient().access_token()))
                .body(BodyInserters.fromValue(userRegistration))
                .retrieve()
                .toBodilessEntity()
                .onErrorResume(WebClientResponseException.Unauthorized.class,
                        forbidden -> Mono.error(new UnauthorizedException()))
                .onErrorResume(WebClientResponseException.Forbidden.class,
                        forbidden -> Mono.error(new ForbiddenException("buscar usuarios")))
                .onErrorResume(WebClientResponseException.Conflict.class,
                        forbidden -> Mono.error(new ConflictException("email já cadastrado")))
                .block();

        return new UserDTO(getUserId(getHeaderValue(response.getHeaders(), "Location")),
                userRegistration.email(),
                false,
                userRegistration.enabled());
    }

    private String getHeaderValue(HttpHeaders headers, String key) {
        List<String> h = headers.get(key);
        if (h != null)
            if (h.size() == 1)
                return h.get(0);
        return null;
    }

    private UUID getUserId(String headerValue) {
        Matcher matcher = USER_ID.matcher(headerValue);
        if (matcher.find())
            return UUID.fromString(matcher.group());

        throw new RuntimeException();
    }

    private TokenDTO autenticarClient() {
        return autenticar(
                realm,
                BodyInserters.fromFormData("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("grant_type", "client_credentials"));
    }

    private TokenDTO autenticar(String realm, FormInserter<String> formData) {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/realms/{realm}/protocol/openid-connect/token")
                        .build(realm))
                .body(formData)
                .retrieve()
                .bodyToMono(TokenDTO.class)
                .onErrorResume(WebClientResponseException.Unauthorized.class,
                        forbidden -> Mono.error(new UnauthorizedException()))
                .onErrorResume(WebClientResponseException.Forbidden.class,
                        forbidden -> Mono.error(new ForbiddenException("logar usuarios")))
                .block();
    }

}
