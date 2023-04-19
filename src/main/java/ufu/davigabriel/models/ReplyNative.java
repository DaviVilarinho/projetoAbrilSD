package ufu.davigabriel.models;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ReplyNative {
    SUCESSO(0, ""),
    DUPLICATA(400, "Item ja existe."),
    INEXISTENTE(404, "Item nao existe."),
    ERRO_DESCONHECIDO(502, "Falha interna.");

    private final int code;
    private final String description;

    private ReplyNative(int code, String description){
        this.code = code;
        this.description = description;
    }
}