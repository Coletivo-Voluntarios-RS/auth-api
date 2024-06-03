package ong.bonanza.auth.application.usecase;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import ong.bonanza.auth.adapter.client.keycloak.KeycloakClient;
import ong.bonanza.auth.adapter.client.keycloak.dto.CredentialDTO;
import ong.bonanza.auth.adapter.client.keycloak.dto.UserRegisterDTO;
import ong.bonanza.auth.domain.enumeration.TipoUsuarioPublico;

@RequiredArgsConstructor
@Component
public class CadastrarContaUC {

    private final CadastrarContaUCMapper mapper;

    private final KeycloakClient keycloakClient;

    public void executar(NovaContaDTO novaConta) {
        keycloakClient.criarUsuario(mapper.toUserRegisterDTO(novaConta, true));
    }

    public record NovaContaDTO(
            @Email String email,
            @NotBlank String credencial,
            @NotNull TipoUsuarioPublico tipoUsuario) {
    }

    @Mapper
    public interface CadastrarContaUCMapper {

        @Mapping(target = "credentials", source = "novaContaDTO.credencial")
        @Mapping(target = "groups", source = "novaContaDTO.tipoUsuario")
        UserRegisterDTO toUserRegisterDTO(NovaContaDTO novaContaDTO, boolean enabled);

        default List<CredentialDTO> toCredentialDTOList(String credential) {
            return new ArrayList<>(List.of(new CredentialDTO("password", credential, false)));
        }

        default List<String> toGroups(TipoUsuarioPublico tipoUsuarioPublico) {
            return tipoUsuarioPublico == null ? null : new ArrayList<>(List.of(tipoUsuarioPublico.getGrupo()));
        }

    }

}
