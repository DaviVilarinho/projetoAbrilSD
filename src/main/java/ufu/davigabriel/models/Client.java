package ufu.davigabriel.models;

import org.json.JSONObject;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ufu.davigabriel.server.ClientGRPC;
import ufu.davigabriel.server.ClientOrErrorGRPC;

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
                .setData(new JSONObject()
                        .put("name", name)
                        .put("zipCode", zipCode)
                        .toString()
                )
                .build();
    }

    public ClientOrErrorGRPC toClientOrErrorGRPC() {
        return ClientOrErrorGRPC.newBuilder()
                .setClientGRPC(toClientGRPC())
                .build();
    }

    public static Client fromClientGRPC(ClientGRPC clientGRPC) {
        JSONObject data = new JSONObject(clientGRPC.getData());
        return Client.builder()
                .clientId(clientGRPC.getCID())
                .name(data.getString("name"))
                .zipCode(data.getString("zipCode"))
                .build();
    }
}