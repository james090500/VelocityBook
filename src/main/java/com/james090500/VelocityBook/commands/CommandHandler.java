package com.james090500.VelocityBook.commands;

import com.james090500.VelocityBook.VelocityBook;
import com.james090500.VelocityBook.config.Configs;
import com.james090500.VelocityBook.helpers.BookLauncher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class CommandHandler {

    private VelocityBook velocityBook;

    public CommandHandler(VelocityBook velocityBook) {
        this.velocityBook = velocityBook;
    }

    /**
     * The command for /vbook book
     * Handles listing books and passes a valid argument to the BookLauncher
     * @param commandSourceCommandContext
     * @return
     */
    public int book(CommandContext<CommandSource> commandSourceCommandContext) {
        if(!(commandSourceCommandContext.getSource() instanceof Player)) {
            Component error = LegacyComponentSerializer.legacyAmpersand().deserialize(velocityBook.PREFIX + "Only a player can run these commands");
            commandSourceCommandContext.getSource().sendMessage(error);
            return 0;
        }

        Player player = (Player) commandSourceCommandContext.getSource();
        ParsedArgument<CommandSource, ?> nameArgument = commandSourceCommandContext.getArguments().get("name");
        if(nameArgument == null) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(velocityBook.PREFIX + "Available books"));
            Configs.getBooks().forEach((title, book) -> {
                //Hide books with no permissions
                if(book.getPerm().equalsIgnoreCase("default") || player.hasPermission(book.getPerm())) {
                    player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(velocityBook.PREFIX + title));
                }
            });
            return 1;
        }

        new BookLauncher(velocityBook).execute((String) nameArgument.getResult(), player);
        return 1;
    }

    /**
     * Reloads the configs
     * @param commandSourceCommandContext
     * @return
     */
    public int reload(CommandContext<CommandSource> commandSourceCommandContext) {
        Configs.loadConfigs(velocityBook);
        CommandSource source = commandSourceCommandContext.getSource();
        source.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(velocityBook.PREFIX + "Reloaded"));
        velocityBook.getLogger().info("VelocityBook Reloaded");
        return 1;
    }

    /**
     * A bit of basic about information
     * @param commandSourceCommandContext
     * @return
     */
    public int about(CommandContext<CommandSource> commandSourceCommandContext) {
        CommandSource source = commandSourceCommandContext.getSource();
        source.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(velocityBook.PREFIX + "VelocityBook by james090500"));
        return 1;
    }
}