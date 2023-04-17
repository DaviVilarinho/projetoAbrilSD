package ufu.davigabriel.models;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ufu.davigabriel.server.Product;

@Getter
@Setter
@Builder
@ToString
public class ProductNative {
    private String PID;
    private String name;
    private int quantity;
    private double price;
    private String description;

    public Product toProduct() {
        return Product.newBuilder()
                .setPID(getPID())
                .setData(new Gson().toJson(this))
                .build();
    }

    public static ProductNative fromProduct(Product product) {
        return new Gson().fromJson(product.getData(), ProductNative.class);
    }
}




