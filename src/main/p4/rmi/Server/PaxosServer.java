package main.p4.rmi.Server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Using RMI, Paxos servers are created and managed via the PaxosServer class.
 */
public class PaxosServer {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.SSS");

    /**
     * The main method starts the PaxosServer.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        try {
            int numberOfServers = 5;
            try {
                if (args.length != 2) {
                    System.out.println(getCurrentTime() + " - Try: Command line input incorrect. "
                            + "\nUse: java PaxosServer <port> <remoteObjectName>");
                    System.exit(1);
                }

                int portInput = Integer.parseInt(args[0]);
                String remoteObjectName = args[1];

                RmiServer[] rmiServers = new RmiServer[numberOfServers];

                // Creating and binding servers using the port and serverId to the RMI registry
                for (int serverId = 0; serverId < numberOfServers; serverId++) {
                    int port = portInput + serverId;

                    LocateRegistry.createRegistry(port);

                    rmiServers[serverId] = new RmiServer(serverId);

                    Registry rmiRegistry = LocateRegistry.getRegistry(port);
                    rmiRegistry.rebind(remoteObjectName, rmiServers[serverId]);

                    System.out.println(getCurrentTime() + " - The Server " + serverId + " is now running at port "
                            + port);
                }

                // Scheduling the server drops
                serverDropScheduler(rmiServers);

                // Connecting the Paxos Acceptor and Learner to our Paxos Server
                for (int serverId = 0; serverId < numberOfServers; serverId++) {
                    PaxosAcceptor[] acceptors = new PaxosAcceptor[numberOfServers];
                    PaxosLearner[] learners = new PaxosLearner[numberOfServers];
                    for (int i = 0; i < numberOfServers; i++) {
                        acceptors[i] = (PaxosAcceptor) rmiServers[i];
                        learners[i] = (PaxosLearner) rmiServers[i];
                    }
                    rmiServers[serverId].setAcceptors(acceptors);
                    rmiServers[serverId].setLearners(learners);
                }

            } catch (Exception e) {
                System.err.println(getCurrentTime() + " - Server exception: " + e.toString());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println(getCurrentTime() + " - Could not process Client Request due to the Exception: "
                    + e.getMessage());
        }
    }

    /**
     * Periodically schedules server drops.
     *
     * @param rmiServers An array of servers.
     */
    private static void serverDropScheduler(RmiServer[] rmiServers) {
        Timer serverDropTimer = new Timer();
        serverDropTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                simulateServerDrop(rmiServers);
            }
        }, 10000, 100000);
    }

    /**
     * Simulates a random server outage.
     *
     * @param rmiServers An array of servers.
     */
    private static void simulateServerDrop(RmiServer[] rmiServers) {
        int serverId = (int) (Math.random() * rmiServers.length);
        rmiServers[serverId].setServerDown();
        System.out.println(getCurrentTime() + " - Server " + serverId + " is will be dropped now!!!");
    }

    /**
     * Obtain the current timestamp for in the chosen format.
     *
     * @return current formatted timestamp.
     */
    private static String getCurrentTime() {
        return "<Time: " + dateFormat.format(new Date()) + ">";
    }
}
