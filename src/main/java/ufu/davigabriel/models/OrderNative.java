package ufu.davigabriel.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.json.JSONArray;
import org.json.JSONObject;
import ufu.davigabriel.server.Order;

import java.util.ArrayList;

@Builder
@Setter
@Getter
@ToString
public class OrderNative {
    private String orderId;
    private String clientId;
    private ArrayList<OrderItemNative> orderItemNatives;

    public static OrderNative fromOrderGRPC(Order order) {
        JSONObject data = new JSONObject(order.getData());
        return OrderNative.builder()
                .orderId(order.getOID())
                .clientId(order.getCID())
                .orderItemNatives(mapFromJSONArray(data.getJSONArray("products")))
                .build();
    }

    public Order toOrderGRPC() {
        return Order.newBuilder()
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
        orderItemNatives.forEach((oI) -> {
            jsonArray.put(new JSONObject()
                    .put("name", oI.getName())
                    .put("quantity", oI.getQuantity())
                    .put("price", oI.getPrice())
            );
        });

        return jsonArray;
    }

    private static ArrayList<OrderItemNative> mapFromJSONArray(JSONArray jsonArray){
        ArrayList<OrderItemNative> arrayList = new ArrayList<>();
        jsonArray.forEach((object) -> {
            JSONObject jsonObject = (JSONObject) object;
            arrayList.add(OrderItemNative.builder()
                    .name(jsonObject.getString("name"))
                    .quantity(jsonObject.getInt("quantity"))
                    .price(jsonObject.getDouble("price"))
                    .build()
            );
        });
        return arrayList;
    }
}
