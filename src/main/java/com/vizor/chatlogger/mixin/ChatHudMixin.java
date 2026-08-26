package com.vizor.chatlogger.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import com.vizor.chatlogger.ChatLog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"))
    private void chatlogger$onMessage(Text message, CallbackInfo ci) {
        try {
            ChatLog.onChat(message);
        } catch (Throwable ignored) {
        }
    }
}
