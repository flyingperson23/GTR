package gtr.api.net;

import gtr.common.input.EnumKey;
import gtr.common.input.Key;
import gtr.common.input.Keybinds;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.ArrayList;
import java.util.List;

public class KeysPacket implements IMessage {
    private List<Key> keys;

    public KeysPacket() {
    }

    public KeysPacket(List<Key> keys) {
        this.keys = keys;
    }

    public List<Key> getList() {
        return this.keys;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        for (Key key : keys) {
            buf.writeByte(key.KEY.getID());
            buf.writeBoolean(key.state);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        keys = new ArrayList<>();
        for (int i = 0; i < Keybinds.REGISTERY.size(); i++) {
            int id = buf.readByte();
            Key current = new Key(EnumKey.getKeyByID(id));
            current.state = buf.readBoolean();
            keys.add(current);
        }
    }
}
