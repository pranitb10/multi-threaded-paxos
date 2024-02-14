package main.p4.rmi.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The methods that a Paxos acceptor must implement in order to take part in the Paxos consensus
 * procedure are specified in the PaxosAcceptor interface.
 */
public interface PaxosAcceptor extends Remote {

    /**
     * Sets up the proposal acceptor with the appropriate proposal ID and operation. The proposer
     * invokes this procedure in the Paxos preparation phase.
     *
     * @param proposalId The unique proposal ID.
     * @param operation  The proposed operation.
     * @return True if the acceptor is prepared to accept the proposal, false otherwise.
     * @throws RemoteException when there is a remote exception in the server.
     */
    Boolean prepAcceptor(String proposalId, ServerOperations operation) throws RemoteException;

    /**
     * Accepts the proposal with the proposal ID and value as supplied. The proposer invokes this
     * method in the Paxos accept phase.
     *
     * @param proposalId     The unique proposal ID.
     * @param proposalValue  The value proposed in the proposal.
     * @throws RemoteException when there is a remote exception in the server.
     */
    void accept(String proposalId, ServerOperations proposalValue) throws RemoteException;
}
