package org.xdev100.vinimay.engine.model;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.*;

public class SnapshotDeserializer implements JsonDeserializer<List<Map.Entry<String, UserBalance>>> {
    @Override
    public List<Map.Entry<String, UserBalance>> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray jsonArray = json.getAsJsonArray();
        List<Map.Entry<String, UserBalance>> userBalanceEntries = new ArrayList<>();

        for (JsonElement element : jsonArray) {
            JsonArray entryArray = element.getAsJsonArray();
            String userId = entryArray.get(0).getAsString();
            JsonObject balanceObject = entryArray.get(1).getAsJsonObject();

            Map<String, BalanceInfo> balances = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : balanceObject.entrySet()) {
                JsonObject balanceDetails = entry.getValue().getAsJsonObject();
                BalanceInfo balance = new BalanceInfo(balanceDetails.get("available").getAsDouble(), balanceDetails.get("locked").getAsDouble());
                balances.put(entry.getKey(), balance);
            }

            UserBalance userBalanceEntry = new UserBalance(balances);
            // Construct the map entry manually as Map.Entry is an interface and cannot be instantiated directly
            userBalanceEntries.add(new AbstractMap.SimpleEntry<>(userId, userBalanceEntry));
        }

        return userBalanceEntries;
    }
}
