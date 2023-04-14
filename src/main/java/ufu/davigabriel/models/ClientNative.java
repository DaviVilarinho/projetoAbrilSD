package ufu.davigabriel.models;

import org.json.JSONObject;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ufu.davigabriel.server.Client;

@Getter
@Setter
@Builder
@ToString
public class ClientNative {
    private String clientId;
    private String name;
    private String zipCode;

    public Client toClient() {
        return Client.newBuilder()
                .setCID(getClientId())
                .setData(new JSONObject()
                        .put("CID", clientId)
                        .put("name", name)
                        .put("zipCode", zipCode)
                        .toString()
                )
                .build();
    }

    public static ClientNative fromClient(Client client) {
        JSONObject data = new JSONObject(client.getData());
        return ClientNative.builder()
                .clientId(data.getString("CID"))
                .name(data.getString("name"))
                .zipCode(data.getString("zipCode"))
                .build();
    }
}