package ufu.davigabriel.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

@Getter
@Setter
@Builder
@ToString(includeFieldNames = true)
public class Client {
    private String clientId;
    private String name;
    private String zipCode;
}