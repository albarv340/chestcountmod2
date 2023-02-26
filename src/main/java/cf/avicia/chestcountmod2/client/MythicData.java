package cf.avicia.chestcountmod2.client;

import cf.avicia.chestcountmod2.client.configs.ConfigsHandler;
import cf.avicia.chestcountmod2.core.CustomFile;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MythicData {
    private int chestsDry = 0;

    public MythicData() {
    }

    public JsonObject getMythicData() {
        CustomFile mythicData = new CustomFile(ConfigsHandler.getConfigPath("mythicData"));
        if (mythicData.readJson() == null) {
            return new JsonObject();
        }
        return mythicData.readJson();
    }

    public JsonObject getDryData() {
        CustomFile dryData = new CustomFile(ConfigsHandler.getConfigPath("dryCount"));
        if (dryData.readJson() == null) {
            return new JsonObject();
        }
        return dryData.readJson();
    }

    public void addMythic(int chestCount, String mythic, int dry, int x, int y, int z) {
        try {

            JsonObject currentData = getMythicData();
            JsonObject newData = new JsonObject();
            newData.addProperty("chestCount", chestCount);
            newData.addProperty("mythic", mythic);
            newData.addProperty("dry", dry);
            newData.addProperty("x", x);
            newData.addProperty("y", y);
            newData.addProperty("z", z);
            if (!currentData.has("mythics")) {
                currentData.add("mythics", new JsonArray());
            }
            currentData.get("mythics").getAsJsonArray().add(newData);
            CustomFile mythicData = new CustomFile(ConfigsHandler.getConfigPath("mythicData"));
            mythicData.writeJson(currentData);
            updateDry();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void updateDry() {
        try {
            JsonObject lastMythic = getLastMythic();
            if (lastMythic != null) {
                chestsDry = ChestCountMod2Client.getChestCountData().getTotalChestCount() - lastMythic.get("chestCount").getAsInt();
            } else {
                chestsDry = ChestCountMod2Client.getChestCountData().getTotalChestCount();
            }
            saveDryToFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToDry() {
        chestsDry++;
        try {
            saveDryToFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveDryToFile() {
        JsonObject currentData = getDryData();
        currentData.addProperty("dryCount", chestsDry);
        CustomFile mythicData = new CustomFile(ConfigsHandler.getConfigPath("dryCount"));
        mythicData.writeJson(currentData);
    }

    public int getChestsDry() {
        return chestsDry;
    }

    public JsonObject getLastMythic() {
        JsonObject mythicData = getMythicData().getAsJsonObject();
        if (!mythicData.has("mythics")) {
            mythicData.add("mythics", new JsonArray());
        }
        JsonArray mythics = mythicData.get("mythics").getAsJsonArray();
        if (mythics.size() > 0) {
            return mythics.get(mythics.size() - 1).getAsJsonObject();
        } else {
            return null;
        }
    }
}
