package ong.bonanza.auth.adapter.client.keycloak.dto;

public record UserInfoDTO(
        String sub,
        boolean email_verified,
        String preferred_username,
        String email) {

}
