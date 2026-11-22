package com.vizor.chatlogger;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ChatLoggerMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(ChatLog::tick);
        // Закрываем файл при выходе, чтобы не потерять хвост и оставить маркер сессии.
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ChatLog.close());
    }
}
