package no.ntnu.trostespel.networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import no.ntnu.trostespel.state.GameState;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class Serializer <T>{
    private final Class<T> type;
    private Kryo kryo;
    private Output output;

    public Serializer(Class<T> clazz) {

        this.type = clazz;
        kryo = new Kryo();
        kryo.register(clazz);
        registerClasses(clazz);
        output = new Output(2346);
    }

    private void registerClasses(Class<T> clazz) {
        /*kryo.register(clazz);
        for (Field f : clazz.getDeclaredFields()) {
            for (Annotation a : f.getAnnotations()) {
                if (a.annotationType().)
            }
        }*/
        kryo.setRegistrationRequired(false);
    }

    public byte[] writeAndCopyBuffer(T data) {
        output.setBuffer(new byte[2346]);
        kryo.writeObject(output, data);
        return output.toBytes();
    }

    public void flush() {
        output.flush();
    }
}
