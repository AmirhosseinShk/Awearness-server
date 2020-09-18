package server.topology.component;

import org.jdom2.Element;

public class Route implements Cloneable {
    /**
     * The destination
     */
    private IPAddress destination;

    /**
     * The gateway
     */
    private IPAddress gateway;

    /**
     * The network mask
     */
    private IPAddress mask;

    /**
     * The interface
     */
    private Interface intface;


    /**
     * Create a route
     *
     * @param destination destination ip address
     * @param gateway     gateway ip address
     * @param mask        the destination mask
     * @param intface     the interface
     */
    public Route(IPAddress destination, IPAddress gateway, IPAddress mask, Interface intface) {
        super();
        this.destination = destination;
        this.gateway = gateway;
        this.mask = mask;
        this.intface = intface;
    }

    /**
     * Gets destination.
     *
     * @return the destination
     */
    public IPAddress getDestination() {
        return destination;
    }

    /**
     * Sets destination.
     *
     * @param destination the destination to set
     */
    public void setDestination(IPAddress destination) {
        this.destination = destination;
    }

    /**
     * Gets gateway.
     *
     * @return the gateway
     */
    public IPAddress getGateway() {
        return gateway;
    }

    /**
     * Sets gateway.
     *
     * @param gateway the gateway to set
     */
    public void setGateway(IPAddress gateway) {
        this.gateway = gateway;
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
     * Sets mask.
     *
     * @param mask the mask to set
     */
    public void setMask(IPAddress mask) {
        this.mask = mask;
    }

    /**
     * Gets intface.
     *
     * @return the intface
     */
    public Interface getIntface() {
        return intface;
    }

    /**
     * Sets intface.
     *
     * @param intface the intface to set
     */
    public void setIntface(Interface intface) {
        this.intface = intface;
    }

    /**
     * To dom xML element.
     *
     * @return the dom element corresponding to this route
     */
    public Element toDomXMLElement() {
        Element root = new Element("route");

        Element destinationElement = new Element("destination");
        destinationElement.setText(this.getDestination().getAddress());
        root.addContent(destinationElement);

        Element maskElement = new Element("mask");
        maskElement.setText(this.getMask().getAddress());
        root.addContent(maskElement);

        Element gatewayElement = new Element("gateway");
        gatewayElement.setText(this.getGateway().getAddress());
        root.addContent(gatewayElement);

        Element interfaceElement = new Element("interface");
        interfaceElement.setText(this.getIntface().getName());
        root.addContent(interfaceElement);

        return root;
    }

    @Override
    public Route clone() throws CloneNotSupportedException {
        Route copie = (Route) super.clone();
        copie.setDestination(this.getDestination().clone());
        copie.setGateway(this.getGateway().clone());
        copie.setMask(this.getMask().clone());

        return copie;
    }

    @Override
    public String toString() {
        String result = "";
        result += "destination : " + getDestination().getAddress() + "/" + getMask().getMaskFromIPv4Address() + " -> gateway : " + getGateway().getAddress() + " on interface " + getIntface().getName();
        return result;
    }

}
