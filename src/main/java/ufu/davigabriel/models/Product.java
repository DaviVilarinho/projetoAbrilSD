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
    private String description;
    private double price;
    private int quantity;

    public ProductGRPC toProductGRPC() {
        return ProductGRPC.newBuilder()
                .setPID(getProductId())
                .setData(new JSONObject()
                        .put("name", name)
                        .put("description", description)
                        .put("price", price)
                        .put("quantity", quantity)
                        .toString()
                )
                .build();
    }

    public static Product fromProductGRPC(ProductGRPC ProductGRPC) {
        JSONObject data = new JSONObject(ProductGRPC.getData());
        return Product.builder()
                .productId(ProductGRPC.getPID())
                .name(data.getString("name"))
                .description(data.getString("description"))
                .price(data.getDouble("price"))
                .quantity(data.getInt("quantity"))
                .build();
    }
}




