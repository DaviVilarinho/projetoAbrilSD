package ufu.davigabriel.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class OrderItem {
    private String productId;
    private String name;
    private int quantity;
    private double price;
}
