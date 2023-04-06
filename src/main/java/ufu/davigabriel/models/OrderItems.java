package ufu.davigabriel.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class OrderItems {
    private String productId;
    private int quantity;
}
