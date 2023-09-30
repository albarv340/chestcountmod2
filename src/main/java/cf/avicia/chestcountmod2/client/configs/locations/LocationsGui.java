package cf.avicia.chestcountmod2.client.configs.locations;

import cf.avicia.chestcountmod2.client.ChestCountMod2Client;
import cf.avicia.chestcountmod2.client.InfoDisplay;
import cf.avicia.chestcountmod2.client.configs.ConfigsGui;
import cf.avicia.chestcountmod2.client.configs.locations.locationselements.ElementGroup;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public class LocationsGui extends Screen {
    private static boolean isOpen = false;
    private List<ElementGroup> items;


    public LocationsGui() {
        super(Text.of("ChestCountMod Locations"));

    }

    public static boolean isOpen() {
        return isOpen;
    }

    @Override
    public void init() {
        super.init();
        items = List.of(
                InfoDisplay.getElementsToDraw()
        );
        Screens.getButtons(this).add(new ResetLocationsButton( this.width / 2 - 50, this.height - 30, 100, 20, "Reset to Defaults", this));
        Screens.getButtons(this).add(new ButtonWidget.Builder( Text.of("Configs"), button -> ChestCountMod2Client.screenToRender = new ConfigsGui()).dimensions(10, 10, 50, 20).build());
        isOpen = true;
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        // Makes blur
        this.renderBackground(drawContext, mouseX, mouseY, delta);
        // Draws a shadowed string with a dark color, to make it easier to read depending on the background
        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(2.0F, 2.0F, 2.0F);
        drawContext.drawCenteredTextWithShadow(textRenderer, "ChestCountMod Locations", this.width / 4 + 1, 11, 0x444444);
        drawContext.drawCenteredTextWithShadow(textRenderer, "ChestCountMod Locations", this.width / 4, 10, 0xFFFFFF);
        drawContext.getMatrices().pop();

        if (items != null) {
            items.forEach(eg -> eg.drawGuiElement(drawContext));
        }
        super.render(drawContext, mouseX, mouseY, delta);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        items.forEach(e -> e.pickup((int) mouseX, (int) mouseY));

        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        items.forEach(e -> e.move((int) mouseX, (int) mouseY));

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        items.forEach(e -> e.release((int) mouseX, (int) mouseY));

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        if (items != null) {
            items.forEach(ElementGroup::save);
        }
        isOpen = false;
        super.close();
    }
}
