package ong.bonanza.auth.adapter.client.keycloak.dto;

public record TokenDTO(String access_token,
        Long expires_in,
        Long refresh_expires_in,
        String refresh_token,
        String token_type,
        String session_state,
        String scope) {

}
