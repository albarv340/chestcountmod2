package cf.avicia.chestcountmod2.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ChestCountData {
    private int totalChestCount = 0;
    private int sessionChestCount = 0;
    private boolean hasBeenInitialized = false;

    public ChestCountData() {

    }

    public void updateChestCount() {
        JsonObject mythicData = ChestCountMod2Client.getMythicData().getMythicData();
        JsonElement dryCount = ChestCountMod2Client.getMythicData().getDryData().get("dryCount");
        JsonObject lastMythic = ChestCountMod2Client.getMythicData().getLastMythic();

        if (lastMythic != null && dryCount != null) {
            this.totalChestCount = dryCount.getAsInt() + lastMythic.get("chestCount").getAsInt();
        } else if (lastMythic != null) {
            this.totalChestCount = lastMythic.get("chestCount").getAsInt();
        } else if (dryCount != null) {
            this.totalChestCount = dryCount.getAsInt();
        } else {
            this.totalChestCount = 0;
        }
        this.hasBeenInitialized = true;

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
