package com.james090500.VelocityBook.config;

import com.james090500.VelocityBook.VelocityBook;
import com.moandjiezana.toml.Toml;
import lombok.Getter;

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
            books.put(book.getName(), book);
        }
    }

    @Getter
    public class Book {
        private String name;
        private String perm;
        private String sound;
        private String[] commands;
        private String content;
    }
}