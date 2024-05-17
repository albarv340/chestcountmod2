package cf.avicia.chestcountmod2.client;

import cf.avicia.chestcountmod2.client.commands.CommandInitializer;
import cf.avicia.chestcountmod2.client.configs.ConfigsHandler;
import cf.avicia.chestcountmod2.client.configs.locations.LocationsHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public class ChestCountMod2Client implements ClientModInitializer {

    public static Screen screenToRender = null;
    private static final ChestCountData CHEST_COUNT_DATA = new ChestCountData();
    private static final MythicData MYTHIC_DATA = new MythicData();

    public static ChestCountData getChestCountData() {
        return CHEST_COUNT_DATA;
    }

    public static MythicData getMythicData() {
        return MYTHIC_DATA;
    }
    @Override
    public void onInitializeClient() {
        ConfigsHandler.initializeConfigs();
        LocationsHandler.initializeLocations();
        CommandInitializer.initializeCommands();

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> InfoDisplay.render(drawContext));
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) ->  {
            EventHandlerClass.onChestOpen(client, screen);
            ScreenEvents.beforeRender(screen).register((screen1, matrices, mouseX, mouseY, tickDelta) -> {
                EventHandlerClass.checkForMythic(client, screen1);
            });
            ScreenEvents.afterRender(screen).register((screen1, matrices, mouseX, mouseY, tickDelta) -> {
                EventHandlerClass.drawDryCountInChest(client, screen1, matrices, scaledWidth, scaledHeight);
            });
        });


        ChestCountMod2Client.getChestCountData().updateChestCount();
        ChestCountMod2Client.getMythicData().updateDry();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (screenToRender != null) {
                client.setScreen(screenToRender);
                screenToRender = null;
            }
            if (!ChestCountMod2Client.getChestCountData().hasBeenInitialized()) {
                ChestCountMod2Client.getChestCountData().updateChestCount();
                ChestCountMod2Client.getMythicData().updateDry();
            }
        });
    }
}
