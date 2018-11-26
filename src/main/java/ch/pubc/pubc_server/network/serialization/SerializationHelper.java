package ch.pubc.pubc_server.network.serialization;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

/**
 * Klasse zum Serialisieren und Deserialisieren von Objekten. Basiert auf commonslang3
 */
public class SerializationHelper {
    public static <T extends Serializable> byte[] serialize(T object) {
        return SerializationUtils.serialize(object);
    }

    public static Serializable deserialize(byte[] object) {
        return (Serializable) SerializationUtils.deserialize(object);
    }
}
