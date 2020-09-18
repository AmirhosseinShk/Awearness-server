package server.hostInformation.graph;

import server.hostInformation.InformationSystemHost;
import server.topology.component.Network;

/**
 * Class used to represent a vertex of the {@link InformationSystemGraph InformationSystemGraph}
 *
 */
public class InformationSystemGraphVertex {

    /**
     * The related machine (if type of vertex is Machine)
     */
    private InformationSystemHost machine;

    /**
     * The related network (if type of vertex is Network)
     */
    private Network network;

    /**
     * The type of vertex
     */
    private TopologyVertexType type;

    /**
     * True if the machine/network is controlled by the attacker, else false
     */
    private boolean machineOfAttacker = false;

    /**
     * True if the machine has been compromised
     */
    private boolean compromised = false;

    /**
     * True if this vertex is the target of the attack path
     */
    private boolean target = false;

    /**
     * Get the related machine (if type of vertex is Machine)
     * @return the machine
     */
    public InformationSystemHost getMachine() {
        return machine;
    }

    /**
     * Sets machine.
     *
     * @param machine the machine to set to this vertex
     */
    public void setMachine(InformationSystemHost machine) {
        this.machine = machine;
    }

    /**
     * Get the related network (if type of vertex is Network)
     * @return the network
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * Sets network.
     *
     * @param network the network to set to this vertex
     */
    public void setNetwork(Network network) {
        this.network = network;
    }

    /**
     * The type of vertex
     * @return the type
     */
    public TopologyVertexType getType() {
        return type;
    }

    /**
     * Set the type of this vertex
     *
     * @param type the type of vertex
     */
    public void setType(TopologyVertexType type) {
        this.type = type;
    }

    /**
     * True if the machine/network is controlled by the attacker, else false
     * @return the boolean
     */
    public boolean isMachineOfAttacker() {
        return machineOfAttacker;
    }

    /**
     * Set if this vertex represent a potential machine of the attacker
     *
     * @param machineOfAttacker true if this vertex is a machine of attacker
     */
    public void setMachineOfAttacker(boolean machineOfAttacker) {
        this.machineOfAttacker = machineOfAttacker;
    }

    /**
     * True if the machine has been compromised
     * @return the boolean
     */
    public boolean isCompromised() {
        return compromised;
    }

    /**
     * Set if the current vertex is a compromised machine or network
     *
     * @param compromised the new compromised value
     */
    public void setCompromised(boolean compromised) {
        this.compromised = compromised;
    }

    @Override
    public String toString() {
        switch (getType()) {
            case Machine:
                return this.getMachine().getName();
            case Network:
                return this.getNetwork().getAddress().getAddress();
        }
        return "";
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
     * Sets target.
     *
     * @param target the target
     */
    public void setTarget(boolean target) {
        this.target = target;
    }

    /**
     * The type of vertex of a topology vertex
     */
    public static enum TopologyVertexType {
        /**
         * A Machine.
         */
        Machine, /**
         * A Network.
         */
        Network
    }


}
