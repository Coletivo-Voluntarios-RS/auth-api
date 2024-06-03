package ong.bonanza.auth.adapter.client.keycloak.dto;

import java.util.List;

public record UserRegisterDTO(String email,
                Boolean enabled,
                List<CredentialDTO> credentials,
                List<String> groups) {

}
