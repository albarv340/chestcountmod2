package cf.avicia.chestcountmod2.client.configs;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.regex.Pattern;

public class ConfigsTextField extends TextFieldWidget {
    public ConfigsSection configsSection;
    public String allowedInputs, finalValidation;
    public Color borderColor;

    public ConfigsTextField(String allowedInputs, String finalValidation, TextRenderer textRenderer, int x, int y, int par5Width, int par6Height) {
        super(textRenderer, x + 4, y + 4, par5Width, par6Height, Text.of(""));
        this.allowedInputs = allowedInputs;
        this.finalValidation = finalValidation;

        this.setDrawsBackground(false);
    }

    @Override
    public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if (Pattern.matches(this.finalValidation, this.getText())) {
            borderColor = new Color(0, 255, 0, 200);
        } else {
            borderColor = new Color(255, 0, 0, 200);
        }

        int modifiedX = this.getX() - 4;
        int modifiedY = this.getY() - 4;
        drawContext.fill(modifiedX - 1, modifiedY - 1, modifiedX + this.width + 1, modifiedY + this.height + 1, borderColor.getRGB());
        drawContext.fill(modifiedX, modifiedY, modifiedX + this.width, modifiedY + this.height, -16777216);

        super.renderWidget(drawContext, mouseX, mouseY, delta);
    }

    @Override
    public void write(String text) {
        if (text.length() == 0 || Pattern.matches(this.allowedInputs, text)) {
            super.write(text);
        }
    }

    public void setConfigsSection(ConfigsSection configsSection) {
        this.configsSection = configsSection;
    }
}
