package ufu.davigabriel.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.json.JSONObject;
import ufu.davigabriel.server.ProductGRPC;

@Getter
@Setter
@Builder
@ToString
public class Product {
    private String productId;
    private String name;
    private int quantity;
    private double price;
    private String description;

    public ProductGRPC toProductGRPC() {
        return ProductGRPC.newBuilder()
                .setPID(getProductId())
                .setData(new JSONObject()
                        .put("PID", productId)
                        .put("name", name)
                        .put("quantity", quantity)
                        .put("price", price)
                        .put("description", description)
                        .toString()
                )
                .build();
    }

    public static Product fromProductGRPC(ProductGRPC ProductGRPC) {
        JSONObject data = new JSONObject(ProductGRPC.getData());
        return Product.builder()
                .productId(data.getString("PID"))
                .name(data.getString("name"))
                .quantity(data.getInt("quantity"))
                .price(data.getDouble("price"))
                .description(data.getString("description"))
                .build();
    }
}




