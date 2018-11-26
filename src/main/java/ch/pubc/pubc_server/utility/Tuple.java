package ch.pubc.pubc_server.utility;

/**
 * Eine Generische Klass um 2 Typen zu speichern.
 * @param <Type1> Wert zum speichern vom Type 1
 * @param <Type2> Wert zum speichern vom Type 2
 */
public class Tuple<Type1, Type2> {
    private final Type1 item1;
    private final Type2 item2;

    /***
     * Erstellt einen Tupel
     * @param item1 Wert 1 vom Type 1
     * @param item2 Wert 2 vom Type 2
     */
    public Tuple(Type1 item1, Type2 item2) {
        this.item1 = item1;
        this.item2 = item2;
    }

    /**
     * @return gibt den Wert 1 mit dem Type 1 zurück
     */
    public Type1 getItem1() {
        return item1;
    }

    /**
     * @return gibt den Wert 1 mit dem Type 1 zurück
     */
    public Type2 getItem2() {
        return item2;
    }
}