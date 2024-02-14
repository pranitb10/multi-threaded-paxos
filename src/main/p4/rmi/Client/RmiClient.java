package main.p4.rmi.Client;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.ServerNotActiveException;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import main.p4.rmi.Server.KeyValueStore;
import main.p4.rmi.RequestProcessor.RequestProcessor;

/**
 * Interactions with the RMI server are handled by the RmiClient class.
 */
public class RmiClient {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.SSS");

    /**
     * The main method starts the RmiClient application.
     *
     * @param args Command-line arguments: <hostname> <port> <remoteObjectName>
     */
    public static void main(String[] args) {
        try {
            if (args.length != 3) {
                System.out.println("Usage: java Client <hostname> <port> <remoteObjectName>. " +
                        "\n Please enter appropriate arguments.");
                System.exit(1);
            }

            String hostname = args[0];
            int port = Integer.parseInt(args[1]);
            String remoteObjectName = args[2];

            RMIClientSocketFactory rmiClientSocketFactory = getRmiClientSocketFactory(hostname, port);

            Random random = new Random();
            int addingKV = random.nextInt(4);
            Registry kvRegistry = LocateRegistry.getRegistry(hostname, port + addingKV, rmiClientSocketFactory);
            KeyValueStore remoteObject = (KeyValueStore) kvRegistry.lookup(remoteObjectName);

            // Pre-populating the key values in the store
            for (int i = 0; i < 10; i++) {
                System.out.println(currentTimestamp() + " Pre-populating the key values in the store using PUT.");
                executeClientOperations("PUT key" + i + " value" + i, remoteObject);
                System.out.println(currentTimestamp() + " Pre-Population has been completed successfully!");
            }

            // Perform GET operations to fetch the pre-populated key values
            for (int i = 0; i < 5; i++) {
                System.out.println(currentTimestamp() + " Performing GET Operations...");
                executeClientOperations("GET key" + i, remoteObject);
                System.out.println(currentTimestamp() + " GET Operations have been completed successfully!");
            }

            // Perform DELETE operations on the pre-populated key values
            for (int i = 0; i < 5; i++) {
                System.out.println(currentTimestamp() + " Performing DELETE Operations...");
                executeClientOperations("DELETE key" + i, remoteObject);
                System.out.println(currentTimestamp() + " DELETE Operations have been completed successfully!");
            }

            // Perform new PUT operations to add more key values to the store.
            for (int i = 5; i < 10; i++) {
                System.out.println(currentTimestamp() + " Performing new PUT Operations...");
                executeClientOperations("PUT key" + i + " value" + i, remoteObject);
                System.out.println(currentTimestamp() + " new PUT Operations have been completed successfully!");
            }

            // Taking inputs from users to perform operations between Server and Client
            while (true) {
                try {
                    Scanner inputScanner = new Scanner(System.in);
                    System.out.println(currentTimestamp() + " - Enter GET, PUT, DELETE to perform operations or " +
                            "EXIT to exit: ");
                    String userOperation = inputScanner.nextLine();
                    addingKV = random.nextInt(4);
                    kvRegistry = LocateRegistry.getRegistry(hostname, port + addingKV, rmiClientSocketFactory);
                    System.out.println("Server port - " + (port + addingKV));
                    remoteObject = (KeyValueStore) kvRegistry.lookup(remoteObjectName);
                    if (userOperation.equalsIgnoreCase("EXIT"))
                        break;
                    else if (userOperation.startsWith("PUT ") || userOperation.startsWith("GET ")
                            || userOperation.startsWith("DELETE ")) {
                        executeClientOperations(userOperation, remoteObject);
                    }
                } catch (RemoteException e) {
                    System.out.println(currentTimestamp() + " - Could not process Client Request due to " +
                            "RemoteException!");
                } catch (ServerNotActiveException se) {
                    System.out.println(currentTimestamp() + " - Could not process Client Request due to " +
                            "ServerNotActiveException!");
                } catch (Exception e) {
                    System.out.println(currentTimestamp() + " - Could not process Client Request due to the Exception: "
                            + e.getMessage());
                }
            }
        } catch (RemoteException e) {
            System.out.println(currentTimestamp() + " - Could not process Client Request due to " +
                    "RemoteException!");
        } catch (Exception e) {
            System.out.println(currentTimestamp() + " - Could not process Client Request due to the Exception: "
                    + e.getMessage());
        }
    }

    private static RMIClientSocketFactory getRmiClientSocketFactory(String hostname, int port) throws IOException {
        RMIClientSocketFactory rmiClientSocketFactory = new RMIClientSocketFactory() {
            public Socket createSocket(String host, int port) throws IOException {
                Socket clientSocket = new Socket();
                clientSocket.connect(new InetSocketAddress(host, port), 5000); // 5 sec timeout
                return clientSocket;
            }

            public ServerSocket createServerSocket(int port) throws IOException {
                return new ServerSocket(port);
            }
        };

        rmiClientSocketFactory.createSocket(hostname, port);
        return rmiClientSocketFactory;
    }

    /**
     * Executes the operation specified on the remote object.
     *
     * @param operation   The operation to performed on the remote object.
     * @param remoteObject The remote object of the KeyValueStore.
     * @throws ServerNotActiveException exception when the server is inactive.
     * @throws RemoteException          when there is a remote exception in the server.
     * @throws InterruptedException   if the client operation gets interrupted.
     */
    private static void executeClientOperations(String operation, KeyValueStore remoteObject)
            throws ServerNotActiveException, RemoteException, InterruptedException {
        System.out.println(currentTimestamp() + " Operation Received from Client: " + operation);
        RequestProcessor response = requestProcessor(operation, remoteObject);
        String responseMessage;
        if (!response.reqStatus) {
            System.out.println(currentTimestamp() + " : Incorrect request received: Malformed Request Length. ->"
                    + operation.length());
            responseMessage = response.reqMessage;
        } else {
            responseMessage = response.reqValue;
        }
        System.out.println(currentTimestamp() + " Response from server -> " + responseMessage);
    }

    /**
     * The request is handled by the remote KeyValueStore object.
     *
     * @param dataRequested The request data.
     * @param remoteObject The remote KeyValueStore object.
     * @return The response processed by the requestProcessor
     * @throws RemoteException      when there is a remote exception in the server.
     * @throws InterruptedException if the client operation gets interrupted.
     */
    private static RequestProcessor requestProcessor(String dataRequested, KeyValueStore remoteObject)
            throws RemoteException, InterruptedException {
        if (dataRequested.startsWith("PUT")) {
            String[] requestParts = dataRequested.split(" ");
            if (requestParts.length == 3) {
                String key = requestParts[1];
                String value = requestParts[2];
                remoteObject.putKV(key, value);
                return new RequestProcessor(true, "PUT process successful", "Key:"
                        + key + " added with the Value:" + value);
            } else {
                return new RequestProcessor(false, "PUT operation failed due to malformed input",
                        "");
            }
        }

        if (dataRequested.startsWith("GET")) {
            String[] requestParts = dataRequested.split(" ");
            if (requestParts.length == 2) {
                String key = requestParts[1];
                if (remoteObject.containsKey(key)) {
                    String value = remoteObject.getKV(key);
                    return new RequestProcessor(true, "GET operation successful!",
                            "Value returned for the given Key is : " + value);
                } else {
                    return new RequestProcessor(false, "Key not found in key store", "");
                }
            } else {
                return new RequestProcessor(false, "GET operation failed due to malformed input",
                        "");
            }
        }

        if (dataRequested.startsWith("DELETE")) {
            String[] parts = dataRequested.split(" ");
            if (parts.length == 2) {
                String key = parts[1];
                remoteObject.deleteKV(key);
                return new RequestProcessor(true, "DELETE operation successful!",
                        "Value deleted for Key:" + key);
            } else {
                return new RequestProcessor(false, "DELETE operation failed due to malformed input",
                        "");
            }
        }
        return new RequestProcessor(false, "Operation failed due to malformed input", "");
    }

    /**
     * Obtain the current timestamp for in the chosen format.
     *
     * @return current formatted timestamp.
     */
    private static String currentTimestamp() {
        return "[Time: " + dateFormat.format(new Date()) + "]";
    }
}
