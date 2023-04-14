package ufu.davigabriel.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.json.JSONArray;
import org.json.JSONObject;
import ufu.davigabriel.server.OrderGRPC;

import java.util.ArrayList;

@Builder
@Setter
@Getter
@ToString
public class Order {
    private String orderId;
    private String clientId;
    private ArrayList<OrderItem> orderItems;

    public static Order fromOrderGRPC(OrderGRPC orderGRPC) {
        JSONObject data = new JSONObject(orderGRPC.getData());
        return Order.builder()
                .orderId(orderGRPC.getOID())
                .clientId(orderGRPC.getCID())
                .orderItems(mapFromJSONArray(data.getJSONArray("products")))
                .build();
    }

    public OrderGRPC toOrderGRPC() {
        return OrderGRPC.newBuilder()
                .setOID(orderId)
                .setCID(clientId)
                .setData(new JSONObject()
                        .put("OID", orderId)
                        .put("CID", clientId)
                        .put("products", mapToJSONArray())
                        .toString()
                )
                .build();
    }

    private JSONArray mapToJSONArray(){
        JSONArray jsonArray = new JSONArray();
        orderItems.forEach((oI) -> {
            jsonArray.put(new JSONObject()
                    .put("name", oI.getName())
                    .put("quantity", oI.getQuantity())
                    .put("price", oI.getPrice())
            );
        });

        return jsonArray;
    }

    private static ArrayList<OrderItem> mapFromJSONArray(JSONArray jsonArray){
        ArrayList<OrderItem> arrayList = new ArrayList<>();
        jsonArray.forEach((object) -> {
            JSONObject jsonObject = (JSONObject) object;
            arrayList.add(OrderItem.builder()
                    .name(jsonObject.getString("name"))
                    .quantity(jsonObject.getInt("quantity"))
                    .price(jsonObject.getDouble("price"))
                    .build()
            );
        });
        return arrayList;
    }
}
