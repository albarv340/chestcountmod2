package cf.avicia.chestcountmod2.client.configs.locations.locationselements;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;

public class TextElement {
    protected float x, y;
    protected Color color;
    protected String text;

    public TextElement(String text, float x, float y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.text = text;
    }

    public void draw(MatrixStack matrices) {
        Screen.drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, Text.of(text), (int) x, (int) y, color.getRGB());
    }

    public void move(float changeX, float changeY) {
        x += changeX;
        y += changeY;
    }
    public boolean inRange(int mouseX, int mouseY) {
        // Returns true if the coordinates are overlapping with the textElement
        TextRenderer fontRenderer = MinecraftClient.getInstance().textRenderer;
        final int width = fontRenderer.getWidth(this.text);
        final int height = 12;
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    public String getText() {
        return text;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setText(String text) {
        this.text = text;
    }
}
