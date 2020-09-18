package server.networkfirewall;

import server.topology.Topology;
import server.topology.component.PortRange;
import org.jdom2.Element;
import server.topology.component.Protocol;

/**
 * Class to represent a line of the flow matrix (authorized access from source to destination)
 *
 */
public class FlowMatrixLine {
    /**
     * The source element
     */
    private final FlowMatrixElement source;

    /**
     * The destination element
     */
    private final FlowMatrixElement destination;

    /**
     * The source port range
     */
    private final PortRange source_port;

    /**
     * The destination port range
     */
    private final PortRange destination_port;

    /**
     * The protocol used
     */
    private final Protocol protocol;

    /**
     * Create a flow matrix line from a XML DOM element
     *
     * @param element  the XML DOM Element
     * @param topology the network topology object
     */
    public FlowMatrixLine(Element element, Topology topology) {
        if (element == null)
            throw new IllegalArgumentException("The flow matrix line element is null");
        source = new FlowMatrixElement(element.getChild("source"), topology);
        destination = new FlowMatrixElement(element.getChild("destination"), topology);
        source_port = PortRange.fromString(element.getChildText("source_port"));
        destination_port = PortRange.fromString(element.getChildText("destination_port"));
        protocol = Protocol.getProtocolFromString(element.getChildText("protocol"));
    }

    /**
     * @return get the source element
     */
    public FlowMatrixElement getSource() {
        return source;
    }

    /**
     * @return get the destination element
     */
    public FlowMatrixElement getDestination() {
        return destination;
    }

    /**
     * @return get the source port
     */
    public PortRange getSource_port() {
        return source_port;
    }

    /**
     * @return get the destination port
     */
    public PortRange getDestination_port() {
        return destination_port;
    }

    /**
     * @return get the protocol
     */
    public Protocol getProtocol() {
        return protocol;
    }
}
