# Implementation of Paxos

This project uses consensus and fault tolerance techniques for a system that replicates a Key-Value Store Server. As explained in Lamport's "Paxos made simple" paper, the main objective is to use the Paxos algorithm to achieve synchronized updates and improve the system's resilience against replica failures. The project explores the complexities of implementing the algorithm, comprehending the stages necessary for consensus, and addressing intricacies connected to event sequencing by incorporating Paxos roles, such as Proposers, Acceptors, and Learners. Interestingly, the architecture allows proposers to elect their leaders while still accommodating dynamic client requests for replicas. Moreover, the acceptor threads' occasional failure is a critical need, highlighting Paxos's proficiency in managing replicated server failures.

## System Requirements

- Java Development Kit (JDK) 11

## How to Use

1. Open a CLI by going to the `/jar` folder.

2. Use the following command to launch the server: <br/>
   `java -jar RmiServer.jar <port> <remoteObject>` <br/>
   > Change `<port>` to the required port number where the client will be able to access remote methods from the server. Additionally, change `\remoteObject>` to the registry name of the selected remote object so that the client can access the server's functions. 
   > Example: `java -jar Server.jar 4000 ABC`.   

3. Use the following command to start the client.: <br/>
   `java -jar RmiClient.jar <hostname> <port> <remoteObject>` <br/>
   > To access remote objects on the server, replace `<serverPort>` with the port number and `<serverAddress>` with the hostname or IP address of the server. The value `localhost` can be assigned to the server port in this project. 
   > An example of how to run the RmiClient: `java -jar RmiClient.jar localhost 4000 ABC`.


4. Preloading values into the key store is the first step. The system will then carry out five actions, including PUT, DELETE, and GET.

5. After you select an operation, the client will ask you to enter the desired operation, Key, and Value. It will request that you re-enter the data if the input is inaccurate. After the operation is completed, the server responds to the client with information about the operation's success. Entering "EXIT" as the operation will cause the client to log out of the system.

6. Example on performing operations using RmiClient:

   `PUT key10 abc` <br/>
   `GET key10` <br/>
   `DELETE key10` <br/>

7. The client operation inputs that are available are: GET <KEY>, PUT <KEY> <VALUE>, and DELETE <KEY>. Case sensitivities apply to the inputs. </br>
   **Result**: 
   Key: <KEY> has been added with Value: <VALUE>, while Key: <KEY> has had its Value removed. The returned value for the specified Key is: <VALUE>.

8. The `/res` folder contains screenshots that can be used as references.

