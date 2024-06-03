package ong.bonanza.auth.adapter.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ong.bonanza.auth.application.usecase.CadastrarContaUC;
import ong.bonanza.auth.application.usecase.LogarUC;

@RequiredArgsConstructor
@RestController
@RequestMapping("auth")
public class AuthController {

    private final CadastrarContaUC cadastrarContaUC;

    private final LogarUC logarUC;

    @PostMapping("register")
    public ResponseEntity<Void> registrarUsuario(@RequestBody @Valid CadastrarContaUC.NovaContaDTO novaConta) {
        cadastrarContaUC.executar(novaConta);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/login", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
    public ResponseEntity<LogarUC.TokenDTO> login(
            @ModelAttribute("email") String email,
            @Schema(format = "password") @ModelAttribute("password") String password) {
        return ResponseEntity.ok(logarUC.executar(email, password));
    }

}
