package ufu.davigabriel.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class OrderItems {
    private String productId;
    private int quantity;
}
