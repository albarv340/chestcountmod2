package cf.avicia.chestcountmod2.client.configs.locations;

import cf.avicia.chestcountmod2.client.configs.ConfigsHandler;
import cf.avicia.chestcountmod2.core.CustomFile;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.Map;

public class ResetLocationsButton extends ButtonWidget {

    private final LocationsGui gui;

    public ResetLocationsButton(int x, int y, int widthIn, int heightIn, String buttonText, LocationsGui gui) {
        super(x, y, widthIn, heightIn, Text.of(buttonText), ButtonWidget::onPress, DEFAULT_NARRATION_SUPPLIER);
        this.gui = gui;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        CustomFile locationsFile = new CustomFile(ConfigsHandler.getConfigPath("locations"));
        JsonObject locationsJson = locationsFile.readJson();

        for (Map.Entry<String, String> locationData : LocationsHandler.defaultLocations.entrySet()) {
            locationsJson.addProperty(locationData.getKey(), locationData.getValue());
        }

        locationsFile.writeJson(locationsJson);
        LocationsHandler.locations = locationsJson;
        gui.init();
    }
}
