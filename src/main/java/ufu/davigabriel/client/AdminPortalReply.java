package ufu.davigabriel.client;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum AdminPortalReply {
    SUCESSO(0, ""),
    DUPLICATA(-1, "Item ja existe."),
    INEXISTENTE(-2, "Item nao existe.");

    private final int error;
    private final String description;

    private AdminPortalReply(int error, String description){
        this.error = error;
        this.description = description;
    }
}