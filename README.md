# multi-threaded-Paxos
Multi-threaded Key-Value Store using RPC

The primary goal of the project is to guarantee that a group of computers can reach an agreement using the Paxos consensus algorithm, even if certain machines encounter difficulties. To overcome the shortcomings of earlier techniques like the Two-Phase Commit protocol, this entails defining roles such as "Proposers," "Acceptors," and "Learners" to cooperate and agree on values. It is essential to comprehend the steps of Paxos: prepare, accept, learn; handle errors; and simulate components like clients and data stores. The experiment illustrates Paxos's capacity to create consensus in a distributed system despite possible disruptions by regulating event sequencing and reaching a dependable agreement on values.
