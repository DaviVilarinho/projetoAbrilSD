package ufu.davigabriel.models;

import com.google.gson.Gson;
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
    private String CID;
    private String name;
    private String zipCode;

    public Client toClient() {
        return Client.newBuilder()
                .setCID(getCID())
                .setData(new Gson().toJson(this))
                .build();
    }

    public static ClientNative fromClient(Client client) {
        return new Gson().fromJson(client.getData(), ClientNative.class);
    }
}