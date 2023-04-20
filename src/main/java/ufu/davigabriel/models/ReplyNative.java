package ufu.davigabriel.models;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ReplyNative {
    SUCESSO(0, ""),
    DUPLICATA(400, "Item ja existe."),
    INEXISTENTE(404, "Item nao existe."),
    ERRO_DESCONHECIDO(500, "Falha interna."),
    ERRO_MQTT(502, "Erro no servidor Mosquitto.");

    private final int code;
    private final String description;

    ReplyNative(int code, String description) {
        this.code = code;
        this.description = description;
    }
}