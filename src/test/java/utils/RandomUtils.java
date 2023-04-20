package utils;

import net.bytebuddy.utility.RandomString;
import ufu.davigabriel.models.ClientNative;
import ufu.davigabriel.models.ProductNative;

import java.util.Random;

public class RandomUtils {
    public static ClientNative generateRandomClient() {
        return ClientNative.builder()
                .CID(RandomString.make(32).strip().trim())
                .name(RandomString.make(16))
                .zipCode(RandomString.make(8))
                .build();
    }

    public static ProductNative generateRandomProduct() {
        return ProductNative.builder()
                .PID(RandomString.make(32).strip().trim())
                .name(RandomString.make(16))
                .price(new Random().nextDouble())
                .quantity(new Random().nextInt())
                .description(RandomString.make(64))
                .build();
    }
}
