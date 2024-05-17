package cf.avicia.chestcountmod2.client;

import cf.avicia.chestcountmod2.client.configs.ConfigsHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class EventHandlerClass {
    private static final String[] colors = {"§8", "§0", "§c", "§d", "§1", "§2", "§4", "§5", "§9"};

    private static boolean hasMythicBeenRegistered = false;
    private static int chestsDry = 0;

    public static void onChestOpen(MinecraftClient client, Screen screen) {
        if (client.player == null || !(screen instanceof GenericContainerScreen)) {
            return;
        }

        String containerName = screen.getTitle().getString();
        // It is a loot chest, and it doesn't already have a new name
        if (containerName.contains("Loot Chest") && !containerName.contains("#")) {
            // All this code runs once when the loot chest has been opened
            ChestCountMod2Client.getChestCountData().addToSessionChestCount();
            ChestCountMod2Client.getMythicData().addToDry();
            chestsDry = ChestCountMod2Client.getMythicData().getChestsDry();
            final int totalChestCount = ChestCountMod2Client.getChestCountData().getTotalChestCount();
            // Defaults to not having a mythic in the chest
            hasMythicBeenRegistered = false;
            String titleColor = (ConfigsHandler.getConfigBoolean("enableColoredName") ? colors[totalChestCount % colors.length] : "§8");
            screen.title = Text.of(String.format("%s%s %s#%s Tot: %,.0f",
                    titleColor,
                    (ConfigsHandler.getConfigBoolean("abbreviateLootChest") ? screen.getTitle().getString().replace("Loot Chest", "LC") : screen.getTitle().getString()),
                    titleColor,
                    ChestCountMod2Client.getChestCountData().getSessionChestCount(),
                    (double) totalChestCount).replace(" ", ",")
            );
        }
    }


    public static void checkForMythic(MinecraftClient client, Screen screen) {
        if (client.player == null || !(screen instanceof GenericContainerScreen)) {
            return;
        }
        String containerName = screen.getTitle().getString();

        if (containerName.contains("Loot Chest")) {
            final DefaultedList<Slot> slots = client.player.currentScreenHandler.slots;
            int itemCount = 0;
            for (Slot slot : slots) {
                if (!slot.getStack().getName().getString().equals("Air") && !slot.inventory.equals(client.player.getInventory())) {
                    itemCount++;
                }
            }
            if (itemCount == 0) {
                return; // If there are no items on the chest (or the items haven't loaded) just try again basically
            }
            boolean isMythicInChest = false;

            for (Slot slot : slots) {
                ItemStack itemStack = slot.getStack();
                if (!itemStack.getName().getString().equals("Air") && !slot.inventory.equals(client.player.getInventory())) {
                    List<Text> lore = itemStack.getTooltip(client.player, TooltipContext.Default.ADVANCED);
                    // Find whether the lore includes Tier: Mythic
                    Optional<Text> mythicTier = lore.stream().filter(line -> line.getString().contains("Tier: Mythic")).findFirst();
                    Optional<Text> itemLevel = lore.stream().filter(line -> line.getString().contains("Lv. ")).findFirst();

                    if (mythicTier.isPresent()) {
                        if (!hasMythicBeenRegistered) { // Makes sure you don't register the same mythic twice
                            if (itemLevel.isPresent()) {
                                try {
                                    // A new mythic has been found!
                                    String mythicString = itemStack.getName().getString() + " " + itemLevel.get().getString();
                                    if (ConfigsHandler.getConfigBoolean("displayMythicOnFind")) {
                                        if (ConfigsHandler.getConfigBoolean("displayMythicTypeOnFind")) {
                                            client.player.sendMessage(Text.literal(mythicString + " : §c" + ChestCountMod2Client.getMythicData().getChestsDry() + " dry"));
                                        } else {
                                            client.player.sendMessage(Text.literal("§5Mythic found : §c" + ChestCountMod2Client.getMythicData().getChestsDry() + " dry"));
                                        }
                                    }
                                    ChestCountMod2Client.getMythicData().addMythic(
                                            ChestCountMod2Client.getChestCountData().getTotalChestCount(),
                                            mythicString.replaceAll("§.", ""),
                                            chestsDry,
                                            client.player.getBlockX(),
                                            client.player.getBlockY(),
                                            client.player.getBlockZ()
                                    );
                                } catch (Exception e) {
                                    // If a mythic is in the chest, just catch every exception (I don't want to risk a crash with a mythic in the chest)
                                    e.printStackTrace();
                                }
                            }
                        }
                        isMythicInChest = true;
                    }
                }
            }
            // After checking every item in the chest
            if (isMythicInChest) {
                if (!hasMythicBeenRegistered) {
                    hasMythicBeenRegistered = true;
                }
            }
        }
    }

    public static void drawDryCountInChest(MinecraftClient client, Screen screen, DrawContext drawContext, int scaledScreenWidth, int scaledScreenHeight) {
        if (client.player == null || !(screen instanceof GenericContainerScreen)) {
            return;
        }
        String containerName = screen.getTitle().getString();

        if (containerName.contains("Loot Chest")) {
            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(0f, 0f, 299f);
            drawContext.drawText(client.textRenderer, chestsDry + " Dry", scaledScreenWidth / 2 - 20, scaledScreenHeight / 2 - 11, new Color(64, 64, 64).getRGB(), false);
            drawContext.getMatrices().pop();
        }
    }

}
