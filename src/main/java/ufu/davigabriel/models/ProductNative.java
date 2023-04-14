package ufu.davigabriel.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.json.JSONObject;
import ufu.davigabriel.server.Product;

@Getter
@Setter
@Builder
@ToString
public class ProductNative {
    private String productId;
    private String name;
    private int quantity;
    private double price;
    private String description;

    public Product toProductGRPC() {
        return Product.newBuilder()
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

    public static ProductNative fromProductGRPC(Product Product) {
        JSONObject data = new JSONObject(Product.getData());
        return ProductNative.builder()
                .productId(data.getString("PID"))
                .name(data.getString("name"))
                .quantity(data.getInt("quantity"))
                .price(data.getDouble("price"))
                .description(data.getString("description"))
                .build();
    }
}




