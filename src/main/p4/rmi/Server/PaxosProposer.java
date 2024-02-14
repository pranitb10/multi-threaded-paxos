package main.p4.rmi.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * In the Paxos consensus algorithm, a proposer is represented by the PaxosProposer interface.
 * It is the proposers' responsibility to start the proposal phase and provide values.
 */
public interface PaxosProposer {

    /**
     * Offers the acceptors a value along with a proposal ID.
     *
     * @param proposalId     The unique ID for the proposal.
     * @param proposalValue  The value to be proposed.
     * @throws RemoteException when there is a remote exception in the server.
     * @throws InterruptedException if the client operation gets interrupted.
     */
    void propose(String proposalId, ServerOperations proposalValue) throws RemoteException, InterruptedException;
}
