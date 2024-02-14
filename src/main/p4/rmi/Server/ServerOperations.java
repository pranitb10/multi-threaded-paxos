package main.p4.rmi.Server;

/**
 * An operation in the Paxos consensus algorithm is represented by the ServerOperations class.
 * It contains the value (for PUT operations) and the related key and operation type (DELETE or PUT).
 */
public class ServerOperations {

    /**
     * The type of operation (PUT, GET, or DELETE).
     */
    String type;

    /**
     * The key related to the process.
     */
    String key;

    /**
     * The value related to the process.
     */
    String value;

    /**
     * Creates an Operation object with the type, value, and key that are asked for.
     *
     * @param type  The type of operation.
     * @param key   The key associated with the operation.
     * @param value The value associated with the operation.
     */
    ServerOperations(String type, String key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }
}
