package com.hamusuke.paint.command.commands;

import com.hamusuke.paint.command.CommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class StopCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("stop").executes(c -> {
            c.getSource().getServer().stop(false);
            return 1;
        }));
    }
}
