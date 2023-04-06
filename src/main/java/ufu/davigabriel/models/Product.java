package ufu.davigabriel.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Product {
    private String productId;
    private String name;
    private String description;
    private double price;
    private int quantity;
}
