package com.hamusuke.paint.command.commands;

import com.hamusuke.paint.command.CommandSource;
import com.hamusuke.paint.command.Commands;
import com.mojang.brigadier.CommandDispatcher;

public class StopCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("stop").executes(c -> {
            c.getSource().getServer().stop(false);
            return 1;
        }));
    }
}
