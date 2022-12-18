package com.hamusuke.paint.command.commands;

import com.hamusuke.paint.command.CommandSource;
import com.hamusuke.paint.command.Commands;
import com.mojang.brigadier.CommandDispatcher;

public class SaveCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("saveall").executes(c -> {
            c.getSource().getServer().saveAll();
            return 1;
        }));
    }
}
