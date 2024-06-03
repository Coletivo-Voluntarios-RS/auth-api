package ong.bonanza.auth.adapter.client.keycloak.dto;

import java.util.UUID;

public record UserDTO(UUID id, String email, Boolean emailVerified, Boolean enabled) {

}
