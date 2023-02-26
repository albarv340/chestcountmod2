package cf.avicia.chestcountmod2.client.commands.subcommands;

import cf.avicia.chestcountmod2.client.ChestCountMod2Client;
import cf.avicia.chestcountmod2.client.configs.locations.LocationsGui;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;


public class LocationsCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> command(String commandName) {
        return ClientCommandManager.literal(commandName)
                .executes(context -> {
                    ChestCountMod2Client.screenToRender = new LocationsGui();
                    return 0;
                });
    }
}
