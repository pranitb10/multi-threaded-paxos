package main.p4.rmi.RequestProcessor;

/**
 * The RequestProcessor class represents a response to an RMI server request.
 */
public class RequestProcessor {
    /**
     * A RequestProcessor constructor constructs an object with the specified request's
     * status, message, and value.
     *
     * @param reqStatus  The request's status (true if it was successful, false otherwise).
     * @param reqMessage A message containing more information about the request.
     * @param reqValue   If relevant, the request's value.
     */
    public RequestProcessor(Boolean reqStatus, String reqMessage, String reqValue) {
        this.reqStatus = reqStatus;
        this.reqMessage = reqMessage;
        this.reqValue = reqValue;
    }

    /**
     * The request's status (true if it was successful, false otherwise).
     */
    public Boolean reqStatus;

    /**
     * A message containing more information about the request.
     */
    public String reqMessage;

    /**
     * If relevant, the request's value.
     */
    public String reqValue;
}
