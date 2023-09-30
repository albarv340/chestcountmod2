package cf.avicia.chestcountmod2.client.configs;

import cf.avicia.chestcountmod2.core.CustomFile;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;

public class ConfigsHandler {

    public static JsonObject configs = null;
    private static CustomFile configsFile = null;


    public static Config[] configsArray = new Config[]{
            new ConfigToggle("General", "Randomize color of Loot Chest names", "Disabled", "enableColoredName"),
            new ConfigToggle("Chat", "Say mythic found in chat", "Enabled", "displayMythicOnFind"),
            new ConfigToggle("Chat", "Say mythic type in chat on mythic found", "Enabled", "displayMythicTypeOnFind"),
            new ConfigToggle("Display", "Always display chest count on screen", "Disabled", "alwaysShowChestCount"),
            new ConfigToggle("Display", "Always display session chest count on screen", "Disabled", "alwaysShowSessionChestCount"),
            new ConfigToggle("Display", "Always display dry count on screen", "Disabled", "alwaysShowDry"),
            new ConfigToggle("Display", "Always display last mythic on screen", "Disabled", "alwaysShowLastMythic"),
            new ConfigToggle("Display", "Display location", "Edit", "locations")
    };

    public static void initializeConfigs() {
        configsFile = new CustomFile(getConfigPath("configs"));
        JsonObject configsJson = configsFile.readJson();
        boolean configsChanged = false;

        for (Config config : configsArray) {
            JsonElement configElement = configsJson.get(config.configsKey);

            if (configElement == null || configElement.isJsonNull()) {
                configsJson.addProperty(config.configsKey, config.defaultValue);
                configsChanged = true;
            }
        }

        if (configsChanged) {
            configsFile.writeJson(configsJson);
        }
        configs = configsJson;
    }

    public static String getConfigPath(String name) {
        return String.format("chestcountmod/%s/%s.json", MinecraftClient.getInstance().getSession().getUuidOrNull().toString().replaceAll("-", ""), name);
    }

    public static String getConfig(String configKey) {
        JsonElement configElement = configs.get(configKey);

        if (configElement == null || configElement.isJsonNull()) {
            return "";
        } else {
            return configElement.getAsString();
        }
    }

    public static void updateConfigs(String configsKey, String newValue) {
        JsonObject configsJson = configsFile.readJson();
        configsJson.addProperty(configsKey, newValue);

        if (configsKey.equals("autoStream") && newValue.equals("Disabled")) {
            if (MinecraftClient.getInstance().getNetworkHandler() != null) {
                MinecraftClient.getInstance().getNetworkHandler().sendCommand("stream");
            }
        }

        ConfigsHandler.configs = configsJson;
        configsFile.writeJson(configsJson);
    }

    public static boolean getConfigBoolean(String configKey) {
        String configValue = getConfig(configKey);

        return configValue.equals("Enabled");
    }
}
