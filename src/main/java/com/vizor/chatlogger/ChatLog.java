package com.vizor.chatlogger;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ChatLog {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter FULL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static BufferedWriter writer = null;
    private static String lastKey = null;

    private ChatLog() {
    }

    public static void tick(MinecraftClient client) {
        if (client.world == null) {
            // в меню или загрузке — закрываем если был открыт
            if (writer != null) {
                close();
            }
            return;
        }
        String key = serverKey(client);
        if (key == null) {
            return;
        }
        if (!key.equals(lastKey)) {
            close();
            lastKey = key;
            openWriter(client);
        }
    }

    public static void onChat(Text message) {
        if (writer == null) {
            return;
        }
        String time = LocalDateTime.now().format(TS);
        String text = message.getString();
        synchronized (writer) {
            try {
                writer.write("[" + time + "] " + text);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                System.err.println("[chatlogger] Ошибка записи: " + e);
            }
        }
    }

    private static void openWriter(MinecraftClient client) {
        String ip, name;
        ServerInfo si = client.getCurrentServerEntry();
        if (si != null) {
            ip = si.address;
            name = si.name;
        } else {
            ip = "singleplayer";
            name = "Singleplayer";
        }

        File chatDir = new File(FabricLoader.getInstance().getGameDir().toFile(), "chat");
        chatDir.mkdirs();

        String safeName = name.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
        if (safeName.isEmpty()) {
            safeName = "unknown";
        }
        File file = new File(chatDir, safeName + ".txt");

        try {
            writer = new BufferedWriter(new FileWriter(file, true));
            String now = LocalDateTime.now().format(FULL);
            writer.write("[" + now + "] === Сервер: " + name + " | IP: " + ip + " ===");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("[chatlogger] Не удалось открыть файл: " + e);
        }
    }

    private static void close() {
        if (writer != null) {
            synchronized (writer) {
                try {
                    String now = LocalDateTime.now().format(FULL);
                    writer.write("[" + now + "] === Конец сессии ===");
                    writer.newLine();
                    writer.flush();
                    writer.close();
                } catch (IOException ignored) {
                }
            }
            writer = null;
        }
        lastKey = null;
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
