package ufu.davigabriel.client;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum AdminPortalReply {
    SUCESSO(0, ""),
    DUPLICATA(400, "Item ja existe."),
    INEXISTENTE(404, "Item nao existe."),
    ERRO_DESCONHECIDO(502, "Falha interna.");

    private final int error;
    private final String description;

    private AdminPortalReply(int error, String description){
        this.error = error;
        this.description = description;
    }
}