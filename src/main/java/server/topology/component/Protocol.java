package server.topology.component;

/**
 * IP protocols (TCP, UDP, ICMP) or all protocols (ANY)
 */
public enum Protocol {
    TCP, UDP, ANY, ICMP;

    public static Protocol getProtocolFromString(String protocol) {
        if (protocol.toLowerCase().equals("tcp")) {
            return Protocol.TCP;
        } else if (protocol.toLowerCase().equals("udp")) {
            return Protocol.TCP;
        } else if (protocol.toLowerCase().equals("httpprotocol")) {
            return Protocol.TCP;
        } else if (protocol.toLowerCase().equals("icmp")) {
            return Protocol.ICMP;
        } else {
            return Protocol.ANY;
        }
    }

    public boolean contained(Protocol prot) {
        return this == Protocol.ANY || prot == this;
    }
}
