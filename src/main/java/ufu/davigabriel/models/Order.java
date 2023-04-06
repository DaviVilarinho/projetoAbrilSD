package ufu.davigabriel.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Builder
@Setter
@Getter
public class Order {
    private String orderId;
    private String clientId;
    private ArrayList<OrderItems> orderItems;
}
