package ufu.davigabriel.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class Client {
    private String clientId;
    private String name;
    private String zipCode;
}