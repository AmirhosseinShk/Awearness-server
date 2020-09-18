package server.topology.component;

import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;


public class Interface implements Cloneable {
    /**
     * The name of the interface
     */
    private String name = "";

    /**
     * The IPAddress of the interface
     */
    private IPAddress address;

    /**
     * The VLAN in which is the interface
     */
    private VLAN vlan;

    /**
     * The network in which is the interface
     */
    private Network network = null;

    /**
     * The host in which is the interface
     */
    private Host host = null;

    /**
     * True if the host is directly connected to the internet
     */
    private boolean connectedToTheInternet = false;

    /**
     * Create an interface from its name and ip address
     *
     * @param name    the interface name
     * @param address its IP address
     * @param host    the host which has this interface
     * @throws Exception
     */
    public Interface(String name, String address, Host host) throws Exception {
        this.host = host;
        this.name = name;
        this.address = new IPAddress(address);
        vlan = new VLAN();
    }

    /**
     * Create an interface from its name and ip address
     *
     * @param name    the interface name
     * @param address its IP address
     * @param host    the host which has this interface
     * @param vlan    the vlan of the interface
     * @throws Exception
     */
    public Interface(String name, String address, Host host, VLAN vlan) throws Exception {
        this.host = host;
        this.name = name;
        this.address = new IPAddress(address);
        this.vlan = vlan;
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
     * @return the address
     */
    public IPAddress getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(IPAddress address) {
        this.address = address;
    }

    /**
     * @return the vlan
     */
    public VLAN getVlan() {
        return vlan;
    }

    /**
     * @param vlan the vlan to set
     */
    public void setVlan(VLAN vlan) {
        this.vlan = vlan;
        vlan.addInterface(this);
        vlan.addHost(this.host);
    }

    /**
     * @return the network
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * @param network the network to set
     */
    public void setNetwork(Network network) {
        this.network = network;
    }

    /**
     * @return the host
     */
    public Host getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(Host host) {
        this.host = host;
    }

    /**
     * @return the directlyAccessibleInterface
     */
    public List<Interface> getDirectlyAccessibleInterface() {
        List<Interface> result = new ArrayList<Interface>();
        for (int i = 0; i < this.vlan.getInterfaces().size(); i++) {
            Interface intface = this.vlan.getInterfaces().get(i);
            if (!intface.equals(this)) {
                result.add(intface);
            }
        }
        return result;
    }

    /**
     * @return the connectedToTheInternet
     */
    public boolean isConnectedToTheInternet() {
        return connectedToTheInternet;
    }

    /**
     * @param connectedToTheInternet the connectedToTheInternet to set
     */
    public void setConnectedToTheInternet(boolean connectedToTheInternet) {
        this.connectedToTheInternet = connectedToTheInternet;
    }

    /**
     * @return the dom element corresponding to this interface in XML
     */
    public Element toDomElement() {
        Element root = new Element("interface");

        Element intfaceNameElement = new Element("name");
        intfaceNameElement.setText(this.getName());
        root.addContent(intfaceNameElement);

        root.addContent(this.vlan.toDOMElement());

        Element intfaceIpaddressElement = new Element("ipaddress");
        intfaceIpaddressElement.setText(this.getAddress().getAddress());
        root.addContent(intfaceIpaddressElement);

        if (getNetwork() != null) {
            Element networkIpaddressElement = new Element("network");
            networkIpaddressElement.setText(getNetwork().getAddress().getAddress());
            root.addContent(networkIpaddressElement);

            Element maskElement = new Element("mask");
            maskElement.setText(getNetwork().getMask().getAddress());
            root.addContent(maskElement);
        }

        Element intfaceDirectlyConnectedElement = new Element("directly-connected");
        root.addContent(intfaceDirectlyConnectedElement);
        for (int i = 0; i < this.getDirectlyAccessibleInterface().size(); i++) {
            Element ipAddressElement = new Element("ipaddress");
            ipAddressElement.setText(this.getDirectlyAccessibleInterface().get(i).getAddress().getAddress());
            intfaceDirectlyConnectedElement.addContent(ipAddressElement);
        }

        if (this.isConnectedToTheInternet()) {
            Element internetElement = new Element("internet");
            intfaceDirectlyConnectedElement.addContent(internetElement);
        }
        return root;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
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
        Interface other = (Interface) obj;
        if (getAddress() == null) {
            if (other.getAddress() != null)
                return false;
        } else if (!getAddress().equals(other.getAddress()))
            return false;
        if (getName() == null) {
            if (other.getName() != null)
                return false;
        } else if (!getName().equals(other.getName()))
            return false;
        return true;
    }

    @Override
    public Interface clone() throws CloneNotSupportedException {
        Interface copie = (Interface) super.clone();
        copie.setAddress(this.getAddress().clone());
        copie.setVlan(this.vlan.clone());
        return copie;
    }

    @Override
    public String toString() {
        return "Interface [address=" + getAddress() + ", name=" + getName() + "]";
    }
}
