package cf.avicia.chestcountmod2.client.configs;

import cf.avicia.chestcountmod2.client.ChestCountMod2Client;
import cf.avicia.chestcountmod2.client.configs.locations.LocationsGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Arrays;

public class ConfigsButton extends ButtonWidget {
    public String[] choices;
    private ConfigsSection configsSection;
    private int currentIndex;

    public ConfigsButton(int x, int y, int width, String[] choices, String defaultValue) {
        super(x, y, width, 20, Text.of(defaultValue), ButtonWidget::onPress, DEFAULT_NARRATION_SUPPLIER);
        this.currentIndex = Arrays.asList(choices).indexOf(defaultValue);
        this.choices = choices;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (this.getMessage().getString().equals("Edit")) {
            ChestCountMod2Client.screenToRender = new LocationsGui();
            return;
        }
        this.currentIndex++;
        if (this.currentIndex == choices.length) {
            this.currentIndex = 0;
        }
        this.setMessage(Text.of(this.choices[this.currentIndex]));

        if (this.configsSection != null) {
            this.configsSection.updateConfigs(this.getMessage().getString());
        }
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, getX(), getY(), getX() + width, getY() + 1, 0xFFFFFFFF);
        fill(matrices, getX() + width - 1, getY(), getX() + width, getY() + 20, 0xFFFFFFFF);
        fill(matrices, getX(), getY(), getX() + 1, getY() + 20, 0xFFFFFFFF);
        fill(matrices, getX(), getY() + 19, getX() + width, getY() + 20, 0xFFFFFFFF);
        int color = 0xFF8888;
        if (choices[currentIndex].equals("Enabled")) color = 0x88FF88;
        if (choices[currentIndex].equals("Edit")) color = 0xFFFF00;
        drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, choices[currentIndex], getX() + width / 2, getY() + 6, color);
    }

    public void setConfigsSection(ConfigsSection configsSection) {
        this.configsSection = configsSection;
    }
}
