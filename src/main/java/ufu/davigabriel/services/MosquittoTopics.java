package ufu.davigabriel.services;

public enum MosquittoTopics {
    CLIENT_CREATION_TOPIC("client/creation"),
    CLIENT_UPDATE_TOPIC("client/update"),
    CLIENT_DELETION_TOPIC("client/deletion"),
    ORDER_CREATION_TOPIC("order/creation"),
    ORDER_UPDATE_TOPIC("order/update"),
    ORDER_DELETION_TOPIC("order/deletion"),
    PRODUCT_CREATION_TOPIC("product/creation"),
    PRODUCT_UPDATE_TOPIC("product/update"),
    PRODUCT_DELETION_TOPIC("product/deletion");

    private String topic;

    MosquittoTopics(String topic) {
        this.topic = topic;
    }
}
