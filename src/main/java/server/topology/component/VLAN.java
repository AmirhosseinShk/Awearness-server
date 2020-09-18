package server.topology.component;

import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

public class VLAN implements Cloneable {
    /**
     * The name of the VLAN
     */
    private String name = "";

    /**
     * The label of the VLAN
     */
    private String label = "";


    /**
     * The IP address of the network
     */
    private IPAddress networkAddress;

    /**
     * The network mask of the VLAN
     */
    private int networkMask;
    /**
     * The list of hosts contained in the VLAN
     */
    private List<Host> hosts = new ArrayList<Host>();

    /**
     * The list of all the interfaces contained in the VLAN
     */
    private List<Interface> interfaces = new ArrayList<Interface>();

    /**
     * Create a default VLAN
     */
    public VLAN() {
        java.util.Random rand = new java.util.Random();
        this.label = rand.nextInt(10000) + "";
    }

    /**
     * Instantiates a new VLAN.
     *
     * @param label the label of the VLAN
     */
    public VLAN(String label) {
        this.label = label;
    }

    /**
     * Instantiates a new VLAN.
     *
     * @param label The label of the VLAN
     * @param name  The name of the VLAN
     */
    public VLAN(String label, String name) {
        this.label = label;
        this.name = name;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets hosts.
     *
     * @return the hosts
     */
    public List<Host> getHosts() {
        return hosts;
    }

    /**
     * Add host.
     *
     * @param host the hosts to add
     */
    public void addHost(Host host) {
        this.hosts.add(host);
    }

    /**
     * Gets interfaces.
     *
     * @return the interfaces
     */
    public List<Interface> getInterfaces() {
        return interfaces;
    }

    /**
     * Add interface.
     *
     * @param intface the interface to add
     */
    public void addInterface(Interface intface) {
        this.interfaces.add(intface);
    }

    /**
     * Gets label.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets label.
     *
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public VLAN clone() throws CloneNotSupportedException {
        return (VLAN) super.clone();
    }

    /**
     * To dOM element.
     *
     * @return the element
     */
    public Element toDOMElement() {
        Element root = new Element("vlan");

        Element vlanNameElement = new Element("name");
        vlanNameElement.setText(this.name);
        root.addContent(vlanNameElement);

        Element vlanLabelElement = new Element("label");
        vlanLabelElement.setText(this.label);
        root.addContent(vlanLabelElement);

        return root;
    }

    /**
     * Gets network address.
     *
     * @return the network address
     */
    public IPAddress getNetworkAddress() {
        return networkAddress;
    }

    /**
     * Sets network address.
     *
     * @param networkAddress the network address
     */
    public void setNetworkAddress(IPAddress networkAddress) {
        this.networkAddress = networkAddress;
    }

    /**
     * Gets network mask.
     *
     * @return the network mask
     */
    public int getNetworkMask() {
        return networkMask;
    }

    /**
     * Sets network mask.
     *
     * @param networkMask the network mask
     */
    public void setNetworkMask(int networkMask) {
        this.networkMask = networkMask;
    }
}
