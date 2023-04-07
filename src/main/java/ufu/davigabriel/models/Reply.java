package ufu.davigabriel.models;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Reply {
    SUCESSO(0, ""),
    DUPLICATA(-1, "Usuario ja existe."),
    INEXISTENTE(-2, "Usuario nao existe.");

    private final int error;
    private final String description;

    private Reply(int error, String description){
        this.error = error;
        this.description = description;
    }
}