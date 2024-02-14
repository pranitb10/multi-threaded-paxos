package main.p4.rmi.Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The RmiServer class represents a server in the Paxos consensus framework and implements the
 * PaxosProposer, PaxosAcceptor, PaxosLearner, and KeyValueStore interfaces.
 */
public class RmiServer extends UnicastRemoteObject implements PaxosProposer, PaxosAcceptor,
        PaxosLearner, KeyValueStore {

    private final ConcurrentHashMap<String, String> keyValueStore = new ConcurrentHashMap<>();
    private final Map<String, KeyValuePair<String, ServerOperations>> containsKey;
    private final Map<String, KeyValuePair<Integer, Boolean>> keyValuePairMap;
    private PaxosAcceptor[] acceptors;
    private PaxosLearner[] learners;
    private final int serverId;
    private boolean serverStatus = false;
    private long serverDownTime = 0;
    int serverDownTimer = 100;
    boolean isSuccess = false;

    private static final SimpleDateFormat dateFormat
            = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.SSS");

    /**
     * A RmiServer constructor constructs an object with the specified serverId.
     *
     * @param serverId the ID of the RmiServer.
     * @throws RemoteException when there is a remote exception in the server.
     */
    public RmiServer(int serverId) throws RemoteException {
        this.serverId = serverId;
        this.containsKey = new HashMap<>();
        this.keyValuePairMap = new HashMap<>();
    }

    /**
     * Defines this server's acceptors.
     *
     * @param acceptors An array of acceptors to define.
     * @throws RemoteException when there is a remote exception in the server.
     */
    public void setAcceptors(PaxosAcceptor[] acceptors) throws RemoteException {
        this.acceptors = acceptors;
    }

    /**
     * Defines this server's learners.
     *
     * @param learners An array of learners to define.
     * @throws RemoteException when there is a remote exception in the server.
     */
    public void setLearners(PaxosLearner[] learners) throws RemoteException {
        this.learners = learners;
    }

    @Override
    public synchronized String putKV(String key, String value) throws RemoteException{
        isSuccess = false;
        createOperationProposal(new ServerOperations("PUT", key, value));
        if (isSuccess)
            return "PUT operation successful for key - " + key + " with value - " + value;
        else
            return "Error occurred while performing PUT operation for key -> " + key;
    }

    @Override
    public synchronized String getKV(String key) throws RemoteException {
        if (keyValueStore.containsKey(key))
            return keyValueStore.get(key);
        return "The entered key -> " + key + "does not exist.";
    }

    @Override
    public synchronized String deleteKV(String key) throws RemoteException{
        isSuccess = false;
        createOperationProposal(new ServerOperations("DELETE", key, null));
        if (isSuccess)
            return "DELETE operation successful for key -> " + key;
        else
            return "Error occurred while performing DELETE operation for key -> -> " + key;
    }

    @Override
    public Boolean containsKey(String key) throws RemoteException {
        return keyValueStore.containsKey(key);
    }

    /**
     * Detects whether the acceptor server is down at the moment by checking its status.
     * The status of the server is then updated and returned as "not down" if it was previously
     * labeled as down and the specified server down timeframe has passed.
     *
     * @return {@code false} If the server appears to be down; otherwise, return {@code true}.
     * @throws RemoteException when there is a remote exception in the server.
     */
    private boolean detectPaxosAcceptorStatus() throws RemoteException {
        if (serverStatus) {
            long currentTime = System.currentTimeMillis() / 1000L;
            if (this.serverDownTime + serverDownTimer <= currentTime) {
                serverStatus = false;
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Creates a proposal ID and invokes the proposeOperation function to start the proposal
     * process for an operation. During the Paxos consensus procedure, this approach makes the
     * process of proposing an operation simpler.
     *
     * @param operation that should be suggested and approved.
     * @throws RemoteException when there is a remote exception in the server.
     */
    private void createOperationProposal(ServerOperations operation) throws RemoteException{
        String proposalId = generateProposalId();
        propose(proposalId, operation);
    }

    @Override
    public synchronized Boolean prepAcceptor(String proposalId, ServerOperations ops)
            throws RemoteException {
        if (detectPaxosAcceptorStatus()) {
            return null;
        }
        if (this.containsKey.containsKey(ops.key)) {
            if (Long.parseLong(this.containsKey.get(ops.key).getKey().split(":")[1]) >
                    Long.parseLong(proposalId.split(":")[1])) {
                return false;
            }
        }
        this.containsKey.put(ops.key, new KeyValuePair<>(proposalId, ops));
        return true;
    }

    @Override
    public synchronized void accept(String proposalId, ServerOperations proposalValue)
            throws RemoteException {
        if (detectPaxosAcceptorStatus()) {
            return;
        }

        if (this.containsKey.containsKey(proposalValue.key)) {
            if (Long.parseLong(this.containsKey.get(proposalValue.key).getKey()
                    .split(":")[1]) <= Long.parseLong(proposalId.split(":")[1])) {
                for (PaxosLearner learner : this.learners) {
                    learner.learn(proposalId, proposalValue);
                }
            }
        }
    }

    @Override
    public synchronized void propose(String proposalId, ServerOperations proposalValue)
            throws RemoteException {
        List<Boolean> prepareResponse = new ArrayList<>();
        for (PaxosAcceptor acceptor : this.acceptors) {
            Boolean res = acceptor.prepAcceptor(proposalId, proposalValue);
            prepareResponse.add(res);
        }
        int majority = 0;

        for (int i = 0; i < 5; i++) {
            if (prepareResponse.get(i) != null) {
                if (prepareResponse.get(i))
                    majority += 1;
            }
        }

        if (majority >= Math.ceil((double) acceptors.length / 2)) {
            for (int i = 0; i < 5; i++) {
                if (prepareResponse.get(i) != null)
                    this.acceptors[i].accept(proposalId, proposalValue);
            }
        }
    }

    @Override
    public synchronized void learn(String proposalId, ServerOperations acceptedValue)
            throws RemoteException {
        if (!this.keyValuePairMap.containsKey(proposalId)) {
            this.keyValuePairMap.put(proposalId, new KeyValuePair<>(1, false));
        } else {
            KeyValuePair<Integer, Boolean> learnerPair = this.keyValuePairMap.get(proposalId);
            learnerPair.setKey(learnerPair.getKey() + 1);
            if (learnerPair.getKey() >= Math.ceil((double) acceptors.length / 2)
                    && !learnerPair.getValue()) {
                this.isSuccess = executeOperation(acceptedValue);
                learnerPair.setValue(true);
            }
            this.keyValuePairMap.put(proposalId, learnerPair);
        }
    }

    /**
     * Creates a unique proposal ID by merging the timestamp with the server's ID.
     *
     * @return A distinct ID for the proposal, formatted serverId:timestamp.
     * @throws RemoteException when there is a remote exception in the server.
     */
    private String generateProposalId() throws RemoteException {
        return serverId + ":" + System.currentTimeMillis();
    }

    /**
     * Carries out the required action on the keyValueStore.
     *
     * @param operation The operation needed to be performed.
     * @return If the operation was carried out successfully, it is true; if not, it is false.
     * @throws RemoteException when there is a remote exception in the server.
     */
    private boolean executeOperation(ServerOperations operation) throws RemoteException {
        if (operation == null) return false;
        switch (operation.type) {
            case "PUT":
                keyValueStore.put(operation.key, operation.value);
                System.out.println(getCurrentTime() + " - PUT Operation successful for Key:Value - "
                        + operation.key + ":" + operation.value);
                return true;
            case "DELETE":
                if (keyValueStore.containsKey(operation.key)) {
                    keyValueStore.remove(operation.key);
                    System.out.println(getCurrentTime() + " - DELETE Operation successful for Key -> "
                            + operation.key);
                    return true;
                } else {
                    System.out.println(getCurrentTime() + " - DELETE Operation Failed for Key -> "
                            + operation.key);
                    return false;
                }
            default:
                throw new IllegalArgumentException("Unknown operation type: " + operation.type);
        }
    }

    /**
     * Captures the timestamp and sets the server status to "down".
     */
    public void setServerDown() {
        this.serverStatus = true;
        this.serverDownTime = System.currentTimeMillis() / 1000L;
    }

    /**
     * Obtain the current timestamp for in the chosen format.
     *
     * @return current formatted timestamp.
     */
    private String getCurrentTime() {
        return "<Time: " + dateFormat.format(new Date()) + ">";
    }
}
