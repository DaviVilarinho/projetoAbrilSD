package utils;

import net.bytebuddy.utility.RandomString;
import ufu.davigabriel.models.Client;

public class RandomUtils {
    public static Client generateRandomClient() {
        return Client.builder()
                .name(RandomString.make(16))
                .zipCode(RandomString.make(8))
                .clientId(RandomString.make(32))
                .build();
    }
}
