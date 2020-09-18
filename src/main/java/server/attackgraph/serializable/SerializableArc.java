package server.attackgraph.serializable;

import java.io.Serializable;


public class SerializableArc implements Serializable {
    /**
     * The source id of the arc
     */
    private final int source;

    /**
     * The destination id of the arc
     */
    private final int destination;

    /**
     * the label of the arc
     */
    private final String label;

    /**
     * @param source      the source id of the arc
     * @param destination the destination id of the arc
     * @param label       the label of the arc
     */

    public SerializableArc(int source, int destination, String label) {
        this.source = source;
        this.destination = destination;
        this.label = label;
    }

    /**
     * Get the source of the arc
     *
     * @return the arc source
     */
    public int getSource() {
        return source;
    }

    /**
     * Get the destination of the arc
     *
     * @return the arc destination
     */
    public int getDestination() {
        return destination;
    }

    /**
     * Get the label of the arc
     *
     * @return the arc label
     */
    public String getLabel() {
        return label;
    }
}
