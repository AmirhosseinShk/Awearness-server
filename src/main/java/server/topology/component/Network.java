package server.topology.component;

public class Network implements Cloneable {
    /**
     * The name of the network
     */
    private String name = "";

    /**
     * The ip address of the network
     */
    private IPAddress address = null;

    /**
     * The mask of the network
     */
    private IPAddress mask = IPAddress.getIPv4NetMask(32);

    /**
     * Create a network with an address and a mask
     *
     * @param address the ip address
     * @param mask    the mask
     */
    public Network(IPAddress address, IPAddress mask) {
        this.address = address;
        this.mask = mask;
    }

    /**
     * Instantiates a new Network.
     *
     * @param address the address
     */
    public Network(IPAddress address) {
        this.address = address;
        this.mask = IPAddress.getIPv4NetMask(32);
    }


    /**
     * Create a network with a string
     *
     * @param networkString network string with the CIDR format : A.B.C.D/X
     * @throws Exception the exception
     */
    public Network(String networkString) throws Exception {
        if (!networkString.contains("/"))
            throw new Exception("Wrong network format");
        String address = networkString.split("/")[0];
        String mask = networkString.split("/")[1];
        this.address = new IPAddress(address);
        this.mask = IPAddress.getIPv4NetMask(Integer.parseInt(mask));
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
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets address.
     *
     * @return the address
     */
    public IPAddress getAddress() {
        return address;
    }

    /**
     * Gets mask.
     *
     * @return the mask
     */
    public IPAddress getMask() {
        return mask;
    }

    /**
     * Is included in.
     *
     * @param network an other network
     * @return true if this network is included in the other network
     */
    public boolean isIncludedIn(Network network) {
        return IPAddress.networkInOtherNetwork(this.getAddress(), this.getMask(), network.getAddress(), network.getMask());
    }


    @Override
    public Network clone() throws CloneNotSupportedException {
        Network copie = (Network) super.clone();
        copie.address = this.getAddress().clone();
        copie.mask = this.getMask().clone();

        return copie;
    }

    @Override
    public String toString() {
        return getName() + " : " + getAddress().getAddress() + "/" + getMask().getMaskFromIPv4Address();
    }
}
