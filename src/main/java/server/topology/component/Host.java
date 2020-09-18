package server.topology.component;


import server.topology.Topology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Host implements Cloneable {

    /**
     * The name of the host
     */
    private String name = "";

    /**
     * The list of Interface of the host
     */
    private HashMap<String, Interface> interfaces = new HashMap<>();

    /**
     * The routing table of the host
     */
    private RoutingTable routingTable;

    /**
     * The topology in which is the host
     */
    private Topology topology = null;

    /**
     * Create an empty host
     */
    public Host(Topology topology) {
        routingTable = new RoutingTable(this);
        this.topology = topology;
    }

    /**
     * Create a host with its name
     *
     * @param name the name
     */
    public Host(String name, Topology topology) {
        this(topology);
        this.setName(name);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the interfaces
     */
    public HashMap<String, Interface> getInterfaces() {
        return interfaces;
    }

    /**
     * @return the routingTable
     */
    public RoutingTable getRoutingTable() {
        return routingTable;
    }

    /**
     * @param routingTable the routingTable to set
     */
    public void setRoutingTable(RoutingTable routingTable) {
        this.routingTable = routingTable;
    }

    /**
     * @return the topology
     */
    public Topology getTopology() {
        return topology;
    }

    /**
     * @param topology the topology to set
     */
    public void setTopology(Topology topology) {
        this.topology = topology;
    }

    /**
     * Add (or get) an interface on the host
     *
     * @param name      the interface name
     * @param ipAddress the ip address of the interface
     * @return the new (or existing) interface
     * @throws Exception
     */
    public Interface addInterface(String name, String ipAddress) throws Exception {
        if (!this.getInterfaces().containsKey(name)) { //If this host has not already this interface
            Interface intface = new Interface(name, ipAddress, this);
            this.getInterfaces().put(name, intface);
            return intface;
        } else
            return this.getInterfaces().get(name);
    }

    /**
     * Add (or get) an interface on the host
     *
     * @param name      the interface name
     * @param ipAddress the ip address of the interface
     * @param vlan      the vlan of the interface
     * @return the new (or existing) interface
     * @throws Exception
     */
    public Interface addInterface(String name, String ipAddress, VLAN vlan) throws Exception {
        if (!this.getInterfaces().containsKey(name)) { //If this host has not already this interface
            Interface intface = new Interface(name, ipAddress, this, vlan);
            this.getInterfaces().put(name, intface);
            return intface;
        } else
            return this.getInterfaces().get(name);
    }

    /**
     * @return the list of vlans in which is this host
     */
    public List<VLAN> getVlans() {
        List<VLAN> result = new ArrayList<VLAN>();
        for (String key : this.getInterfaces().keySet()) {
            Interface intface = getInterfaces().get(key);
            result.add(intface.getVlan());
        }
        return result;
    }

    /**
     * @param ip an IP Address
     * @return true if this host as this address IP on one of its interface
     */
    public boolean hasIP(IPAddress ip) {
        return !(this.getExistingInterfaceFromIP(ip) == null);
    }

    /**
     * @param ip the IP to look for
     * @return the existing interface of the host that correspond to the ip if it exists else null
     */
    public Interface getExistingInterfaceFromIP(IPAddress ip) {
        Interface result = null;
        for (String key : this.getInterfaces().keySet()) {
            if (this.getInterfaces().get(key).getAddress().equals(ip)) {
                result = this.getInterfaces().get(key);
                break;
            }
        }
        return result;
    }

    public IPAddress getFirstIPAddress() {
        if (this.getInterfaces().size() > 0) {
            return this.getInterfaces().get(this.getInterfaces().keySet().iterator().next()).getAddress();
        } else
            return null;
    }

    /**
     * @param network a network
     * @return true if this host is in the network else false
     */
    public boolean inNetwork(Network network) {
        for (String key : this.getInterfaces().keySet()) {
            Interface intface = this.getInterfaces().get(key);
            if (intface.getNetwork() != null && (new Network(intface.getAddress())).isIncludedIn(network))
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((getInterfaces() == null) ? 0 : getInterfaces().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Host other = (Host) obj;

        if (getName() == null) {
            if (other.getName() != null)
                return false;
        } else if (!getName().equals(other.getName()))
            return false;

        if (getInterfaces() == null) {
            if (other.getInterfaces() != null)
                return false;
        }
        if (this.getInterfaces() != null) {
            for (String key : this.getInterfaces().keySet()) {
                if (!other.getInterfaces().containsKey(key) || !other.getInterfaces().get(key).equals(this.getInterfaces().get(key))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public Host clone() throws CloneNotSupportedException {
        Host copie = (Host) super.clone();

		/*
		 * Interfaces
		 */
        copie.interfaces = new HashMap<String, Interface>(this.getInterfaces());
        for (String key : copie.getInterfaces().keySet()) {
            copie.getInterfaces().put(key, copie.getInterfaces().get(key).clone());
        }

        //update the host object in all interfaces
        for (String key : copie.getInterfaces().keySet()) {
            Interface currentInterface = copie.getInterfaces().get(key);

            //replace the host object
            currentInterface.setHost(copie);
        }

		/*
		 * Routing table
		 */
        copie.routingTable = this.getRoutingTable().clone();

        //update the reference to the interfaces in the route object with the new interfaces
        for (int i = 0; i < copie.getRoutingTable().getRouteList().size(); i++) {
            Route currentRoute = copie.getRoutingTable().getRouteList().get(i);
            currentRoute.setIntface(copie.getInterfaces().get(currentRoute.getIntface().getName()));
        }

        return copie;
    }

    @Override
    public String toString() {
        return getName();
    }
}
