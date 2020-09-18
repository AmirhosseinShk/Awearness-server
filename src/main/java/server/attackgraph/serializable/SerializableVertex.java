package server.attackgraph.serializable;

import server.hostInformation.graph.InformationSystemGraphVertex;

import java.io.Serializable;


public class SerializableVertex implements Serializable {

    /**
     * The related machine (if type of vertex is Machine)
     */
    private final String machine;

    /**
     * The related network (if type of vertex is Network)
     */
    private final String network;

    /**
     * The type of vertex
     */
    private final InformationSystemGraphVertex.TopologyVertexType type;

    /**
     * True if the machine/network is controlled by the attacker, else false
     */
    private final boolean machineOfAttacker;

    /**
     * True if the machine has been compromised
     */
    private final boolean compromised;

    /**
     * True if this vertex is the target of the attack path
     */
    private final boolean target;

    /**
     * Build a serializable vertex from a information system graph vertex.
     *
     * @param informationSystemGraphVertex the related information system graph vertex.
     */
    public SerializableVertex(InformationSystemGraphVertex informationSystemGraphVertex) {
        switch (informationSystemGraphVertex.getType()) {
            case Machine:
                this.machine = informationSystemGraphVertex.getMachine().getName();
                this.network = "";
                break;
            case Network:
                this.machine = "";
                this.network = informationSystemGraphVertex.getNetwork().getAddress() + "/" +
                        informationSystemGraphVertex.getNetwork().getMask();
                break;
            default:
                this.machine = "";
                this.network = "";
        }
        type = informationSystemGraphVertex.getType();
        machineOfAttacker = informationSystemGraphVertex.isMachineOfAttacker();
        compromised = informationSystemGraphVertex.isCompromised();
        target = informationSystemGraphVertex.isTarget();
    }

    /**
     * Gets machine.
     *
     * @return the machine
     */
    public String getMachine() {
        return machine;
    }

    /**
     * Gets network.
     *
     * @return the network
     */
    public String getNetwork() {
        return network;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public InformationSystemGraphVertex.TopologyVertexType getType() {
        return type;
    }

    /**
     * Is machine of attacker.
     *
     * @return the boolean
     */
    public boolean isMachineOfAttacker() {
        return machineOfAttacker;
    }

    /**
     * Is compromised.
     *
     * @return the boolean
     */
    public boolean isCompromised() {
        return compromised;
    }

    /**
     * Is target.
     *
     * @return the boolean
     */
    public boolean isTarget() {
        return target;
    }

    /**
     * Test if equals to another serializable vertex
     * @param vertex the vertex to test
     * @return true if the vertices are equals
     */
    public boolean equals(SerializableVertex vertex) {
        boolean result = (this.isCompromised() == vertex.isCompromised());
        result &= (this.isTarget() == vertex.isTarget());
        result &= (this.isMachineOfAttacker() == vertex.isMachineOfAttacker());
        result &= (this.getType() == vertex.getType());
        result &= (this.getNetwork().equals(vertex.getNetwork()));
        result &= (this.getMachine().equals(vertex.getMachine()));
        return result;
    }
}
