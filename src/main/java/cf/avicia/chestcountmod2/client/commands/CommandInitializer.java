package cf.avicia.chestcountmod2.client.commands;

import cf.avicia.chestcountmod2.client.ChestCountMod2Client;
import cf.avicia.chestcountmod2.client.commands.subcommands.*;
import cf.avicia.chestcountmod2.client.configs.ConfigsGui;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class CommandInitializer {

    public static void initializeCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            LiteralCommandNode<FabricClientCommandSource> chestCountModCommand = dispatcher.register(
                    ClientCommandManager.literal("chestcountmod")
                            .then(ConfigsCommand.command("configs"))
                            .then(ConfigsCommand.command("cf"))
                            .then(LocationsCommand.command("locations"))
                            .then(LocationsCommand.command("l"))
                            .then(LastMythicCommand.command("lm"))
                            .then(LastMythicCommand.command("lastmythic"))
                            .executes(context -> {
                                ChestCountMod2Client.screenToRender = new ConfigsGui();
                                return 0;
                            })
            );

            dispatcher.register(ClientCommandManager.literal("lastmythic").executes(LastMythicCommand::sendLastMythicMessage));
            dispatcher.register(ClientCommandManager.literal("lm").executes(LastMythicCommand::sendLastMythicMessage));

            dispatcher.register(
                    ClientCommandManager.literal("ccc").redirect(chestCountModCommand).executes(context -> {
                        ChestCountMod2Client.screenToRender = new ConfigsGui();
                        return 0;
                    })
            );
            dispatcher.register(
                    ClientCommandManager.literal("ccm").redirect(chestCountModCommand).executes(context -> {
                        ChestCountMod2Client.screenToRender = new ConfigsGui();
                        return 0;
                    })
            );
        });
    }
}
