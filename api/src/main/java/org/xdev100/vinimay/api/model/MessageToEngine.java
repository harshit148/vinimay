package org.xdev100.vinimay.api.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Setter
@Getter
public class MessageToEngine {
    private String type;
    private CreateOrder createOrder;
    private CancelOrder cancelOrder;
    private GetOpenOrder getOpenOrder;
    private OnRamp onRamp;
    private GetDepth getDepth;

    public static MessageToEngine from(CreateOrder createOrder) {
        MessageToEngine message = new MessageToEngine();
        message.setType("CREATE_ORDER");
        message.setCreateOrder(createOrder);
        return message;
    }
    public static MessageToEngine from(CancelOrder cancelOrder) {
        MessageToEngine message = new MessageToEngine();
        message.setType("CANCEL_ORDER");
        message.setCancelOrder(cancelOrder);
        return message;
    }
    public static MessageToEngine from(GetOpenOrder getOpenOrder){
        MessageToEngine message = new MessageToEngine();
        message.setType("GET_OPEN_ORDERS");
        message.setGetOpenOrder(getOpenOrder);
        return message;
    }
    public static MessageToEngine from(OnRamp onRamp){
        MessageToEngine message = new MessageToEngine();
        message.setType("ON_RAMP");
        message.setOnRamp(onRamp);
        return message;
    }
    public static MessageToEngine from(GetDepth getDepth){
        MessageToEngine message = new MessageToEngine();
        message.setType("GET_DEPTH");
        message.setGetDepth(getDepth);
        return message;
    }

    @Getter
    @Setter
    public static class CreateOrder {
        @NotBlank(message = "Please specify the market to trade in")
        private String market;

        @NotBlank(message = "Please specify the price of the asset")
        @Positive(message = "Invalid price. Please specify a positive price value")
        private double price;

        @NotBlank(message = "Please specify the quantity of the asset")
        @Positive(message = "Invalid quantity. Please specify a positive quantity value")
        private double quantity;

        @NotNull(message = "Please specify the side of order(buy|sell)")
        private OrderSide side;

        @NotNull(message = "Please specify the userId")
        private String userId;
    }

    @Getter
    @Setter
    public static class CancelOrder {
        @NotBlank(message = "Please specify the market of the order")
        private String market;

        @NotBlank(message = "Please specify the orderId to cancel")
        private String orderId;
    }

    @Getter
    @Setter
    public static class GetOpenOrder {
        @NotNull(message = "Please specify the userId")
        private String userId;

        @NotBlank(message = "Please specify the market")
        private String market;
    }

    @Getter
    @Setter
    public static class OnRamp {
        @NotBlank(message = "Please specify the amount")
        @Positive(message = "Invalid amount. Please specify a positive amount value")
        private double amount;

        @NotNull(message = "Please specify the user id")
        private String userId;

        @NotNull(message = "Please specify the transaction id")
        private String transactionId;
    }

    @Getter
    @Setter
    public static class GetDepth {
        @NotBlank(message = "Please specify the market")
        private String market;
    }
}
