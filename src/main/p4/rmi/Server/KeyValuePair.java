package main.p4.rmi.Server;

/**
 * A basic key-value pair is represented by the KeyValuePair class.
 *
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 */
public class KeyValuePair<K, V> {
    private K key;
    private V value;


    /**
     * Creates a Pair object by passing in the key and value.
     *
     * @param key   The key of the pair.
     * @param value The value associated with the key.
     */
    KeyValuePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Obtains the value linked to the key.
     *
     * @return The value associated with the key.
     */
    public V getValue() {
        return value;
    }

    /**
     * Sets the key's associated value.
     *
     * @param value The new value to be associated with the key.
     */
    public void setValue(V value) {
        this.value = value;
    }

    /**
     * Gets the key of the pair.
     *
     * @return The key of the pair.
     */
    public K getKey() {
        return key;
    }

    /**
     * Sets the key of the pair.
     *
     * @param key The new key for the pair.
     */
    public void setKey(K key) {
        this.key = key;
    }
}
