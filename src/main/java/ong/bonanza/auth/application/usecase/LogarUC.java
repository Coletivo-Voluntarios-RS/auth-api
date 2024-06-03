package ong.bonanza.auth.application.usecase;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ong.bonanza.auth.adapter.client.keycloak.KeycloakClient;

@RequiredArgsConstructor
@Component
public class LogarUC {

    private final LogarUCMapper mapper;

    private final KeycloakClient keycloakClient;

    public TokenDTO executar(String usuario, String senha) {
        return mapper.toTokenDTO(keycloakClient.autenticar(usuario, senha));
    }

    public record TokenDTO(String accessToken,
            Long expiresIn,
            Long refreshExpiresIn,
            String refreshToken,
            String tokenType,
            String sessionState,
            String scope) {
    }

    @Mapper
    public interface LogarUCMapper {

        @Mapping(target = "accessToken", source = "access_token")
        @Mapping(target = "expiresIn", source = "expires_in")
        @Mapping(target = "refreshExpiresIn", source = "refresh_expires_in")
        @Mapping(target = "refreshToken", source = "refresh_token")
        @Mapping(target = "tokenType", source = "token_type")
        @Mapping(target = "sessionState", source = "session_state")
        TokenDTO toTokenDTO(ong.bonanza.auth.adapter.client.keycloak.dto.TokenDTO tokenDTO);

    }

}
