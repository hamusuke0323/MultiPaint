package com.hamusuke.paint.command;

import com.hamusuke.paint.command.commands.StopCommand;
import com.mojang.brigadier.CommandDispatcher;

public class Commands {
    public static void registerCommands(CommandDispatcher<CommandSource> dispatcher, boolean dedicated) {
        if (dedicated) {
            StopCommand.register(dispatcher);
        }
    }
}
