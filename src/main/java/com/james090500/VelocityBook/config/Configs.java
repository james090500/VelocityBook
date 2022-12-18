package com.james090500.VelocityBook.config;

import com.james090500.VelocityBook.VelocityBook;
import com.moandjiezana.toml.Toml;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;

public class Configs {

    @Getter private static HashMap<String, Book> books = new HashMap<>();

    /**
     * Loads the config files.
     * @param velocityBook
     */
    public static void loadConfigs(VelocityBook velocityBook) {
        //Create data directory
        if(!velocityBook.getDataDirectory().toFile().exists()) {
            velocityBook.getDataDirectory().toFile().mkdir();
        }

        //Create book directory
        File bookDir = new File(velocityBook.getDataDirectory().toFile() + "/books");
        if(!bookDir.exists()) {
            bookDir.mkdir();
        }

        if(bookDir.listFiles().length == 0) {
            try (InputStream in = VelocityBook.class.getResourceAsStream("/example.toml")) {
                Files.copy(in, new File(bookDir + "/example.toml").toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for(File file : bookDir.listFiles()) {
            Book book = new Toml().read(file).to(Book.class);
            book.itemStack = generateItemStack(book);
            books.put(book.getName(), book);
        }
    }

    /**
     * Generates the book item dynamically and caches it
     * @param book The book config
     * @return
     */
    private static ItemStack generateItemStack(Book book) {
        //Create the book
        ItemStack bookItem = new ItemStack(ItemType.WRITTEN_BOOK);

        //Create a pages tag and fill it with the book contents
        ListTag<StringTag> pagesTag = new ListTag<>(StringTag.class);
        for(String pageText : book.getPages()) {
            //Convert to serialized json
            Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(pageText);
            String json = GsonComponentSerializer.gson().serialize(component);

            //Put on the page
            StringTag pageTag = new StringTag(json);
            pagesTag.add(pageTag);
        }

        //Add the pages to the main tag
        CompoundTag tag = new CompoundTag();
        tag.put("author", new StringTag(book.getAuthor()));
        tag.put("title", new StringTag(book.getTitle()));
        tag.put("pages", pagesTag);

        //Add NBT data to the book
        bookItem.nbtData(tag);

        return bookItem;
    }

    @Getter
    public class Book {
        private String name;
        private String perm;
        private String sound;
        private String[] commands;
        private String title;
        private String author;
        private String[] pages;

        private ItemStack itemStack;
    }
}