package cf.avicia.chestcountmod2.client;

import cf.avicia.chestcountmod2.client.web.WebRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;

public class ChestCountData {
    private int totalChestCount = 0;
    private int sessionChestCount = 0;
    private boolean hasBeenInitialized = false;
    private JsonObject playerAPIData = null;
    private long lastRequestTime = 0;

    public ChestCountData() {

    }

    public void updateChestCount() {
        try {
            JsonElement dryCount = ChestCountMod2Client.getMythicData().getDryData().get("dryCount");
            JsonObject lastMythic = ChestCountMod2Client.getMythicData().getLastMythic();
            if (!hasBeenInitialized && System.currentTimeMillis() - lastRequestTime > 60000) {
                Gson gson = new Gson();
                if (MinecraftClient.getInstance().player != null) {
                    String playerAPIDataResponse = WebRequest.getData("https://api.wynncraft.com/v3/player/" + MinecraftClient.getInstance().player.getUuidAsString());
                    lastRequestTime = System.currentTimeMillis();
                    playerAPIData = gson.fromJson(playerAPIDataResponse, JsonObject.class);
                }
            }

            if (playerAPIData != null && playerAPIData.has("globalData") && playerAPIData.getAsJsonObject("globalData").has("chestsFound")) {
                this.totalChestCount = playerAPIData.getAsJsonObject("globalData").get("chestsFound").getAsInt();
            } else {
                if (lastMythic != null && dryCount != null) {
                    this.totalChestCount = dryCount.getAsInt() + lastMythic.get("chestCount").getAsInt();
                } else if (lastMythic != null) {
                    this.totalChestCount = lastMythic.get("chestCount").getAsInt();
                } else if (dryCount != null) {
                    this.totalChestCount = dryCount.getAsInt();
                } else {
                    this.totalChestCount = 0;
                }
            }
            if (playerAPIData != null) {
                this.hasBeenInitialized = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getTotalChestCount() {
        return totalChestCount + sessionChestCount;
    }

    public void addToSessionChestCount() {
        this.sessionChestCount++;
    }

    public int getSessionChestCount() {
        return sessionChestCount;
    }

    public boolean hasBeenInitialized() {
        return hasBeenInitialized;
    }
}
