package cf.avicia.chestcountmod2.client;

import cf.avicia.chestcountmod2.client.configs.ConfigsHandler;
import cf.avicia.chestcountmod2.client.configs.locations.LocationsHandler;
import cf.avicia.chestcountmod2.client.configs.locations.locationselements.ElementGroup;
import cf.avicia.chestcountmod2.client.configs.locations.locationselements.TextElement;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;


import java.awt.*;
import java.util.ArrayList;

public class InfoDisplay {
    public static ElementGroup getElementsToDraw() {
        int dry = ChestCountMod2Client.getMythicData().getChestsDry();
        String lastMythic = "";
        try {
            JsonObject lastMythicObject = ChestCountMod2Client.getMythicData().getLastMythic();
            if (lastMythicObject != null) {
                lastMythic = lastMythicObject.get("mythic").getAsString();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        String shortLastMythic = "";
        if (lastMythic.length() != 0) {
            try {
                String[] wordsInString = lastMythic.split(" ");
                shortLastMythic = wordsInString[1] + " " + wordsInString[wordsInString.length - 1];
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        String finalLastMythic = "Last Mythic: " + shortLastMythic;
        ArrayList<TextElement> elementsList = new ArrayList<>();

        Point location = new Point((int) LocationsHandler.getStartX("infoLocation"), (int) LocationsHandler.getStartY("infoLocation"));
        boolean showChestCount = ConfigsHandler.getConfigBoolean("alwaysShowChestCount");
        boolean showSessionChestCount = ConfigsHandler.getConfigBoolean("alwaysShowSessionChestCount");
        boolean showDryStreak = ConfigsHandler.getConfigBoolean("alwaysShowDry");
        boolean showLastMythic = ConfigsHandler.getConfigBoolean("alwaysShowLastMythic");

        // offset balances the displays, so they don't have blank rows
        int offset = 0;
        if (showChestCount) {
            elementsList.add(new TextElement(String.format("Chests Opened: %,.0f", (double) ChestCountMod2Client.getChestCountData().getTotalChestCount()).replace(" ", ","), location.x, location.y, Color.WHITE));
            offset++;
        }
        if (showSessionChestCount) {
            elementsList.add(new TextElement(String.format("Session Chests: %,.0f", (double) ChestCountMod2Client.getChestCountData().getSessionChestCount()).replace(" ", ","), location.x, location.y + (12 * offset), Color.WHITE));
            offset++;
        }
        if (showDryStreak) {
            elementsList.add(new TextElement(String.format("Chests Dry: %,.0f", (double) dry).replace(" ", ","), location.x, location.y + (12 * offset), Color.WHITE));
            offset++;
        }
        if (showLastMythic) {
            if (lastMythic.length() != 0) {
                elementsList.add(new TextElement(finalLastMythic, location.x, location.y + (12 * offset), new Color(168, 0, 168)));
            }
        }
        return new ElementGroup("infoLocation", elementsList);
    }

    public static void render(DrawContext drawContext) {
        getElementsToDraw().draw(drawContext);
    }
}
