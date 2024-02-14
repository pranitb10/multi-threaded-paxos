package main.p4.rmi.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * In the Paxos consensus algorithm, a learner is represented by the PaxosLearner interface.
 * It is the learners' responsibility is to define methods that let them learn accepted values for proposal.
 */
public interface PaxosLearner {

    /**
     * Informs the learner of a proposal's accepted value.
     *
     * @param proposalId     The unique identifier of the proposal.
     * @param acceptedValue  The accepted value for the proposal.
     * @throws RemoteException when there is a remote exception in the server.
     */
    void learn(String proposalId, ServerOperations acceptedValue) throws RemoteException;
}
