package main.p4.rmi.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The KeyValueStore interface specifies how to use remote method invocation (RMI) to communicate
 * with a key-value store.
 */

public interface KeyValueStore extends Remote {
    /**
     * Adds a key-value pair to the store.
     *
     * @param key   The key to be inserted.
     * @param value The value associated to the key.
     * @return A message stating the outcome of the put procedure.
     * @throws RemoteException      when there is a remote exception in the server.
     * @throws InterruptedException if the client operation gets interrupted.
     */
    String putKV(String key, String value) throws RemoteException, InterruptedException;

    /**
     * Gets back the value connected to a specified key.
     *
     * @param key The key to get.
     * @return The value associated with the key, or a message mentioning that no such key exists.
     * @throws RemoteException      when there is a remote exception in the server.
     * @throws InterruptedException if the client operation gets interrupted.
     */
    String getKV(String key) throws RemoteException, InterruptedException;

    /**
     * Removes, using the key, a key-value pair from the storage.
     *
     * @param key The key to delete.
     * @return A message displaying the deletion operation's outcome.
     * @throws RemoteException     when there is a remote exception in the server.
     * @throws InterruptedException if the client operation gets interrupted.
     */
    String deleteKV(String key) throws RemoteException, InterruptedException;

    /**
     * Looks up the store to check whether it has the key or not.
     *
     * @param key The key to check.
     * @return If the key is in the store, it is true; otherwise, it is false.
     * @throws RemoteException      when there is a remote exception in the server.
     * @throws InterruptedException if the client operation gets interrupted.
     */
    Boolean containsKey(String key) throws RemoteException, InterruptedException;
}
