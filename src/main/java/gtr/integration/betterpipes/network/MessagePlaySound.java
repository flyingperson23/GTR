package gtr.integration.betterpipes.network;

import gtr.common.Sounds;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePlaySound implements IMessage {
    public MessagePlaySound(){}

    private float volume;
    public MessagePlaySound(float volume) {
        this.volume = volume;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(volume);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        volume = buf.readFloat();
    }

    public static class MessageHandler implements IMessageHandler<MessagePlaySound, IMessage> {
        @Override
        public IMessage onMessage(MessagePlaySound message, MessageContext ctx) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            Minecraft.getMinecraft().addScheduledTask(() -> {
                player.playSound(Sounds.WRENCH_SOUND, message.volume, 1.0F);
            });
            return null;
        }
    }
}