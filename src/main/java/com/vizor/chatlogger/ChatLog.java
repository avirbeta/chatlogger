package com.vizor.chatlogger;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ChatLog {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter FULL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static volatile BufferedWriter writer = null;
    private static String lastKey = null;

    private ChatLog() {
    }

    public static synchronized void tick(MinecraftClient client) {
        String key = client.world == null ? null : serverKey(client);
        if (key == null) {
            close();
            return;
        }
        if (!key.equals(lastKey)) {
            close();
            lastKey = key;
            openWriter(client);
        }
    }

    public static void onChat(Text message) {
        String text = message.getString();
        if (text.isEmpty()) return;

        synchronized (ChatLog.class) {
            BufferedWriter w = writer;
            if (w == null) return;
            String time = LocalDateTime.now().format(TS);
            try {
                w.write("[" + time + "] " + text);
                w.newLine();
                w.flush();
            } catch (IOException e) {
                System.err.println("[chatlogger] Ошибка записи: " + e);
            }
        }
    }

    private static void openWriter(MinecraftClient client) {
        String ip;
        String name;
        ServerInfo si = client.getCurrentServerEntry();
        if (si != null) {
            ip = si.address;
            name = si.name;
        } else {
            ip = "singleplayer";
            name = "Singleplayer";
        }

        File chatDir = new File(FabricLoader.getInstance().getGameDir().toFile(), "chat");
        if (!chatDir.mkdirs() && !chatDir.isDirectory()) {
            System.err.println("[chatlogger] Не удалось создать папку chat");
            return;
        }

        String safeName = name.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
        if (safeName.isEmpty()) {
            safeName = "unknown";
        }
        Path file = chatDir.toPath().resolve(safeName + ".txt");

        try {
            writer = Files.newBufferedWriter(
                    file,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            String now = LocalDateTime.now().format(FULL);
            writer.write("[" + now + "] === Сервер: " + name + " | IP: " + ip + " ===");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            writer = null;
            System.err.println("[chatlogger] Не удалось открыть файл: " + e);
        }
    }

    public static synchronized void close() {
        BufferedWriter w = writer;
        writer = null;
        lastKey = null;
        if (w == null) return;
        try {
            String now = LocalDateTime.now().format(FULL);
            w.write("[" + now + "] === Конец сессии ===");
            w.newLine();
            w.flush();
            w.close();
        } catch (IOException ignored) {
        }
    }

    private static String serverKey(MinecraftClient client) {
        ServerInfo si = client.getCurrentServerEntry();
        if (si != null) {
            return si.address + "|" + si.name;
        }
        if (client.isInSingleplayer()) {
            return "singleplayer";
        }
        return null;
    }
}
