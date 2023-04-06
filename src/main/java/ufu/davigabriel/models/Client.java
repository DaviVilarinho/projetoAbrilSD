package ufu.davigabriel.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Client {
    private String clientId;
    private String name;
    private String zipCode;
}