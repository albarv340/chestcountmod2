package cf.avicia.chestcountmod2.client.configs.locations.locationselements;

import cf.avicia.chestcountmod2.client.configs.locations.LocationsGui;
import cf.avicia.chestcountmod2.client.configs.locations.LocationsHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.util.HashMap;
import java.util.List;

public class ElementGroup {
    private final List<TextElement> elementsList;
    private final String key;
    private int startX, startY;
    private boolean clicked = false;

    public ElementGroup(String key, List<TextElement> elementsList) {
        this.key = key;
        this.elementsList = elementsList;
    }

    public void draw(DrawContext drawContext) {
        if (!LocationsGui.isOpen()) {
            elementsList.forEach(e -> e.draw(drawContext));
        }
    }

    public void drawGuiElement(DrawContext drawContext) {
        elementsList.forEach(e -> e.draw(drawContext));
    }

    public void pickup(int mouseX, int mouseY) {
        elementsList.forEach(element -> {
            if (element.inRange(mouseX, mouseY)) {
                startX = mouseX;
                startY = mouseY;
                clicked = true;
            }
        });
    }

    public void move(int mouseX, int mouseY) {
        if (!clicked) return;

        elementsList.forEach(element -> element.move(mouseX - startX, mouseY - startY));
        startX = mouseX;
        startY = mouseY;
    }

    public void release(int mouseX, int mouseY) {
        if (!clicked) return;

        startX = 0;
        startY = 0;
        clicked = false;
    }

    public void save() {
        LocationsHandler.save(this);
    }

    public String getKey() {
        return key;
    }

    public String toString() {
        // The leftmost text
        float x = elementsList.stream().map(TextElement::getX).min(Float::compare).orElse((float) 0);
        // The highest text
        float y = elementsList.stream().map(TextElement::getY).min(Float::compare).orElse((float) 0);

        float screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        float screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
        // The coordinates are saved as portion of screenwidth and screenheight separated by a comma
        float xProp = ((int) x) / screenWidth;
        float yProp = ((int) y) / screenHeight;

        return xProp + "," + yProp;
    }
}
