package ufu.davigabriel.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ufu.davigabriel.server.ClientGRPC;

@Getter
@Setter
@Builder
@ToString
public class Client {
    private String clientId;
    private String name;
    private String zipCode;

    public ClientGRPC toClientGRPC() {
        return ClientGRPC.newBuilder()
                .setCID(getClientId())
                .setName(name)
                .setZipCode(zipCode)
                .build();
    }

    public static Client fromClientGRPC(ClientGRPC clientGRPC) {
        return Client.builder()
                .clientId(clientGRPC.getCID())
                .name(clientGRPC.getName())
                .zipCode(clientGRPC.getZipCode())
                .build();
    }
}