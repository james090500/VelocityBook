package com.james090500.VelocityBook;

import com.google.inject.Inject;
import com.james090500.VelocityBook.commands.CommandHandler;
import com.james090500.VelocityBook.config.Configs;
import com.james090500.VelocityBook.helpers.BookLauncher;
import com.james090500.VelocityBook.packets.OpenBookPacket;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.Protocol;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.providers.ModuleProvider;
import lombok.Getter;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Plugin(id = "velocitybook", name = "VelocityBook", version = "1.0.0", description = "Books for the entire proxy", authors = { "james095000" }, dependencies = { @Dependency(id = "protocolize") })
public class VelocityBook {

    public final String PREFIX = "&e[VelocityBook]&r ";
    @Getter private final ProxyServer server;
    @Getter private final Logger logger;
    @Getter private final Path dataDirectory;

    @Inject
    public VelocityBook(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        Protocolize.getService(ModuleProvider.class).registerModule(new BookModule());

        //Load the configs
        Configs.loadConfigs(this);

        //Setup command flow
        final CommandHandler handler = new CommandHandler(this);
        server.getCommandManager().register(server.getCommandManager().metaBuilder("vbook").build(), new BrigadierCommand(
                LiteralArgumentBuilder.<CommandSource>literal("vbook").executes(handler::about)
                        .then(LiteralArgumentBuilder.<CommandSource>literal("book").executes(handler::book))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("book").then(RequiredArgumentBuilder.<CommandSource, String>argument("name", StringArgumentType.word()).executes(handler::book)))
                        .then(LiteralArgumentBuilder.<CommandSource>literal("reload").requires(source -> source.hasPermission("vgui.admin")).executes(handler::reload))
        ));

        //Register command aliases for books
        Configs.getBooks().forEach((name, book) -> {
            String[] commands = book.getCommands();
            if(commands == null || commands.length == 0) return;

            CommandMeta.Builder commandBuilder = server.getCommandManager().metaBuilder(commands[0]);
            for(String commannd : commands) {
                commandBuilder.aliases(commannd);
            }

            server.getCommandManager().register(commandBuilder.build(), (SimpleCommand) invocation -> {
                new BookLauncher(this).execute(book.getName(), (Player) invocation.source());
            });
        });
    }
}
