package ufu.davigabriel.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Builder
@Setter
@Getter
@ToString(includeFieldNames = true)
public class Order {
    private String orderId;
    private String clientId;
    private ArrayList<OrderItems> orderItems;
}
