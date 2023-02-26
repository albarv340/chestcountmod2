package cf.avicia.chestcountmod2.client.commands.subcommands;

import cf.avicia.chestcountmod2.client.ChestCountMod2Client;
import com.google.gson.JsonObject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class LastMythicCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> command(String commandName) {
        return ClientCommandManager.literal(commandName)
                .executes(LastMythicCommand::sendLastMythicMessage);
    }

    public static int sendLastMythicMessage(CommandContext<FabricClientCommandSource> context) {
        JsonObject lastMythic = ChestCountMod2Client.getMythicData().getLastMythic();
        if (lastMythic != null) {
            context.getSource().sendFeedback(Text.literal("§9Last mythic: §5" + lastMythic.get("mythic").getAsString()));
            context.getSource().sendFeedback(Text.literal("§3In chest #§6" + lastMythic.get("chestCount")
                    + "§3 after §c" + lastMythic.get("dry") + "§3 chests dry."));
            context.getSource().sendFeedback(Text.literal("§9In the chest at: " + lastMythic.get("x") + " " + lastMythic.get("y") + " " + lastMythic.get("z")));
            context.getSource().sendFeedback(Text.literal("§cCurrent dry streak: " + ChestCountMod2Client.getMythicData().getChestsDry()));
        } else {
            context.getSource().sendFeedback(Text.literal("§cNo mythics found"));
        }
        return 0;
    }
}
