package ong.bonanza.auth.domain.enumeration;

public enum TipoUsuarioPublico {
    VOLUNTARIO("voluntarios"),
    BENEFICIARIO("beneficiarios");

    private String grupo;

    public String getGrupo() {
        return grupo;
    }

    private TipoUsuarioPublico(String grupo) {
        this.grupo = grupo;
    }
}
