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

        Configs.Book book = Configs.getBooks().get(bookName);
        if(book == null) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(velocityBook.PREFIX + "Book not found"));
            return;
        }

        //Stop players with no permissions
        if(!book.getPerm().equalsIgnoreCase("default") && !player.hasPermission(book.getPerm())) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(velocityBook.PREFIX + "Book not found"));
            return;
        }

        //Create the book
        ItemStack bookItem = new ItemStack(ItemType.WRITTEN_BOOK);

        //Put book in hand, open then remove book
        PlayerInventory playerInventory = protocolizePlayer.proxyInventory();
        int hand = playerInventory.heldItem() + 36;

        //Put book in hand
        playerInventory.item(hand, bookItem);
        playerInventory.heldItem((short) 0);
        playerInventory.update();

        String test = Protocolize.protocolRegistration().debugInformation();
        velocityBook.getLogger().warn(test);

        //Open Book
        //Cannot send dev.simplix.protocolize.data.packets.UseItem to players with protocol version 758
        //I think I need a packet here...
        try {
            protocolizePlayer.sendPacket(new OpenBookPacket(Hand.MAIN_HAND));
        } catch(Exception e) {
            e.printStackTrace();
        }

        //Return item (item is null as inventory is null?)
        playerInventory.item(hand, new ItemStack(ItemType.AIR));
        playerInventory.update();

        if(book.getSound() != null) {
            protocolizePlayer.playSound(Sound.valueOf(book.getSound()), SoundCategory.MASTER, 1f, 1f);
        }
    }
}