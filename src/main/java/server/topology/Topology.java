package server.topology;

import server.topology.component.Host;
import server.topology.component.IPAddress;
import server.topology.component.Network;
import server.topology.component.VLAN;
import server.topology.component.Interface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Topology implements Cloneable {
    /**
     * The list of hosts in the topology
     */
    private ArrayList<Host> hosts = new ArrayList<Host>();

    /**
     * The list of contained vlans
     */
    private HashMap<String, VLAN> vlans = new HashMap<String, VLAN>();

    /**
     * Gets hosts.
     *
     * @return the hosts
     */
    public ArrayList<Host> getHosts() {
        return hosts;
    }

    /**
     * Add host.
     *
     * @param host the host to add
     */
    public void addHost(Host host) {
        this.hosts.add(host);
    }

    /**
     * Gets vlans.
     *
     * @return the vlans
     */
    public HashMap<String, VLAN> getVlans() {
        return vlans;
    }

    /**
     * Gets vlan.
     *
     * @param label the label of the vlan to search
     * @return the vlan
     */
    public VLAN getVlan(String label) {
        return this.vlans.get(label);
    }

    /**
     * Add vlan.
     *
     * @param vlan the vlan to add
     */
    public void addVlan(VLAN vlan) {
        this.vlans.put(vlan.getLabel(), vlan);
    }

    /**
     * Gets new or existing vlan.
     *
     * @param vlanLabel the vlan label
     * @return the vlan witch label is vlanLabel if it exists, else create a new vlan
     */
    public VLAN getNewOrExistingVlan(String vlanLabel) {
        if (getVlans().containsKey(vlanLabel))
            return getVlans().get(vlanLabel);
        else {
            VLAN vlan = new VLAN(vlanLabel);
            vlan.setName(vlanLabel);
            this.vlans.put(vlanLabel, vlan);
            return vlan;
        }
    }

    /**
     * Gets host by iP address.
     *
     * @param ipAddress an IP Address
     * @return The host in the topology that has this IP Address. If this host doesn't exists, just add a new one.
     * @throws Exception the exception
     */
    public Host getHostByIPAddress(IPAddress ipAddress) throws Exception {
        Host existingHost = existingHostByIPAddress(ipAddress);
        if (existingHost != null)
            return existingHost;
        Host newHost = new Host(ipAddress.getAddress(), this);
        newHost.addInterface("int1", ipAddress.getAddress());
        this.addHost(newHost);
        return newHost;
    }

    /**
     * Gets interface by ip address.
     *
     * @param ipAddress the ip address
     * @return the interface by ip address
     * @throws Exception the exception
     */
    public Interface getInterfaceByIpAddress(IPAddress ipAddress) throws Exception {
        for (Host host : getHosts()) {
            for (Interface networkInterface : host.getInterfaces().values()) {
                if (networkInterface.getAddress().equals(ipAddress)) {
                    return networkInterface;
                }
            }
        }
        return null;
    }

    /**
     * Existing host by iP address.
     *
     * @param ipAddress the ip address
     * @return the host if it exists else null
     */
    public Host existingHostByIPAddress(IPAddress ipAddress) {
        for (int i = 0; i < this.getHosts().size(); i++) {
            if (this.getHosts().get(i).hasIP(ipAddress))
                return this.getHosts().get(i);
        }
        return null;
    }

    /**
     * Existing host by name.
     *
     * @param name the name of the host
     * @return the host if it exists else null
     */
    public Host existingHostByName(String name) {
        for (int i = 0; i < this.getHosts().size(); i++) {
            if (this.getHosts().get(i).getName().equals(name))
                return this.getHosts().get(i);
        }
        //if(name.equals("internet"))
        //	return new Host("internet",this);

        return null;
    }

    @Override
    public Topology clone() throws CloneNotSupportedException {
        Topology copie = (Topology) super.clone();

        copie.hosts = new ArrayList<Host>(this.getHosts());
        for (int i = 0; i < copie.getHosts().size(); i++) {
            copie.getHosts().set(i, copie.getHosts().get(i).clone());
        }

        copie.vlans = new HashMap<String, VLAN>(this.getVlans());
        for (String key : copie.getVlans().keySet()) {
            copie.getVlans().put(key, copie.getVlans().get(key).clone());
        }

        //For all the hosts
        for (int i = 0; i < copie.getHosts().size(); i++) {
            //update the references in all the interfaces of all the hosts
            Host hostCopie = copie.getHosts().get(i);
            for (String key : hostCopie.getInterfaces().keySet()) {
                Interface currentInterface = hostCopie.getInterfaces().get(key);
                //replace the directly connected interfaces by the real interfaces of the new object


                for (int j = 0; j < currentInterface.getDirectlyAccessibleInterface().size(); j++) {
                    try {
                        Interface newHostAccessibleInterface = currentInterface.getDirectlyAccessibleInterface().get(j);
                        Host newHostAccessible = copie.getHostByIPAddress(newHostAccessibleInterface.getHost().getFirstIPAddress());
                        currentInterface.getDirectlyAccessibleInterface().set(j, newHostAccessible.getInterfaces().get(newHostAccessibleInterface.getName()));

                    } catch (Exception e) {
                        System.out.println("Problem when copying the topology : host whitout ip address");
                    }
                }
            }
            hostCopie.setTopology(copie);

        }

        //For all the vlans
        for (String key : copie.getVlans().keySet()) {
            //update the references in all the interfaces and hosts of all the vlans
            VLAN currentVlan = copie.getVlans().get(key);
            for (int k = 0; k < currentVlan.getInterfaces().size(); k++) {
                Interface intface = currentVlan.getInterfaces().get(k);
                currentVlan.getInterfaces().set(k, copie.existingHostByIPAddress(intface.getAddress()).getExistingInterfaceFromIP(intface.getAddress()));
            }

            for (int k = 0; k < currentVlan.getHosts().size(); k++) {
                Host currentHost = currentVlan.getHosts().get(k);
                currentVlan.getHosts().set(k, copie.existingHostByIPAddress(currentHost.getFirstIPAddress()));
            }
        }

        return copie;
    }

    @Override
    public String toString() {
        String result = "Topology :\n";
        for (int i = 0; i < getHosts().size(); i++) {
            result += "    - " + getHosts().get(i) + "\n";
        }
        return result;
    }

    /**
     * Gets hosts in network.
     *
     * @param network a network
     * @return all the hosts of this topology that are in the network
     */
    public List<Host> getHostsInNetwork(Network network) {
        List<Host> result = new ArrayList<Host>();
        for (int i = 0; i < this.getHosts().size(); i++) {
            Host host = this.getHosts().get(i);
            if (host.inNetwork(network)) {
                result.add(host);
            }
        }
        return result;
    }

}
