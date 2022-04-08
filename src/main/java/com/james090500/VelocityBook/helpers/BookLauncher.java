package com.james090500.VelocityBook.helpers;

import com.james090500.VelocityBook.VelocityBook;
import com.james090500.VelocityBook.config.Configs;
import com.james090500.VelocityBook.packets.OpenBookPacket;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.Hand;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.SoundCategory;
import dev.simplix.protocolize.api.inventory.PlayerInventory;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class BookLauncher {

    private VelocityBook velocityBook;

    public BookLauncher(VelocityBook velocityBook) {
        this.velocityBook = velocityBook;
    }

    /**
     * Launches a book instance from a book name
     * @param bookName The book name
     * @param player The player to launch the book for
     */
    public void execute(String bookName, Player player) {
        ProtocolizePlayer protocolizePlayer = Protocolize.playerProvider().player(player.getUniqueId());

        Configs.Book bookConfig = Configs.getBooks().get(bookName);
        if(bookConfig == null) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(velocityBook.PREFIX + "Book not found"));
            return;
        }

        //Stop players with no permissions
        if(!bookConfig.getPerm().equalsIgnoreCase("default") && !player.hasPermission(bookConfig.getPerm())) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(velocityBook.PREFIX + "Book not found"));
            return;
        }

        //Put book in hand, open then remove book
        PlayerInventory playerInventory = protocolizePlayer.proxyInventory();
        int handSlot = 8;

        //Put book in hand
        playerInventory.item(handSlot + 36, bookConfig.getItemStack());
        playerInventory.heldItem((short) handSlot);
        playerInventory.update();

        //Open Book
        try {
            protocolizePlayer.sendPacket(new OpenBookPacket(Hand.MAIN_HAND));
        } catch(Exception e) {
            player.sendMessage(Component.text("Something went wrong! Tell James plz <3"));
            e.printStackTrace();
        }

        //Empty the inventory
        playerInventory.item(handSlot + 36, ItemStack.NO_DATA);
        playerInventory.update();

        if(bookConfig.getSound() != null) {
            protocolizePlayer.playSound(Sound.valueOf(bookConfig.getSound()), SoundCategory.MASTER, 1f, 1f);
        }
    }
}