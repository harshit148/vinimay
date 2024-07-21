    package org.xdev100.vinimay.api.model;

    import com.fasterxml.jackson.annotation.JsonSubTypes;
    import com.fasterxml.jackson.annotation.JsonTypeInfo;
    import lombok.Getter;
    import lombok.Setter;

    @Getter
    @Setter

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = DepthResponse.class),
            @JsonSubTypes.Type(value = OpenOrders.class),
            @JsonSubTypes.Type(value = OrderPlaced.class),
            @JsonSubTypes.Type(value = OrderCancelled.class),
            @JsonSubTypes.Type(value = Fill.class),
    })
    public abstract class MessageFromOrderBook {

    }
