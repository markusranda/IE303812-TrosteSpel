package no.ntnu.trostespel.networking;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import no.ntnu.trostespel.PlayerActions;

import java.net.DatagramPacket;
import java.util.Arrays;

public class Deserializer <T>{

    private final Class<T> type;
    private Kryo kryo;
    private Input input;
    private T obj;

    public Deserializer(Class<T> clazz) {
        this.type = clazz;
        kryo = new Kryo();
        registerClasses(clazz);
        input = new Input(2346);
    }

    private void registerClasses(Class<T> clazz) {
        /*kryo.register(clazz);
        for (Class<?> c : clazz.getDeclaredClasses()) {
            System.out.println(c);
            kryo.register(c);
        }*/
        kryo.setRegistrationRequired(false);
    }

    public T deserialize(DatagramPacket packet) {
        input.setBuffer(packet.getData());
        return kryo.readObject(input, type);
    }
}
