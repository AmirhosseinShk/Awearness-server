package server.hostInformation.graph;

/**
 * Class to represent an arc of the {@link InformationSystemGraph InformationSystemGraph}
 *
 */
public class InformationSystemGraphArc {
    /**
     * The arc source
     */
    private InformationSystemGraphVertex source;

    /**
     * The arc destination
     */
    private InformationSystemGraphVertex destination;

    /**
     * Eventually, the vulnerability that is supported by this arc
     */
    private String relatedVulnerability = null;

    /**
     * @return the source of the arc
     */
    public InformationSystemGraphVertex getSource() {
        return source;
    }

    /**
     * Set a new source to the arc
     *
     * @param source the new source of the arc
     */
    public void setSource(InformationSystemGraphVertex source) {
        this.source = source;
    }

    /**
     * @return the destination of the arc
     */
    public InformationSystemGraphVertex getDestination() {
        return destination;
    }

    /**
     * Set a new destination to the arc
     *
     * @param destination the new destination of the arc
     */
    public void setDestination(InformationSystemGraphVertex destination) {
        this.destination = destination;
    }

    /**
     * @return the vulnerability of the arc (can be null if not applicable)
     */
    public String getRelatedVulnerability() {
        return relatedVulnerability;
    }

    /**
     * Assign a new vulnerability to the arc
     *
     * @param relatedVulnerability the vulnerability to add to the arc
     */
    public void setRelatedVulnerability(String relatedVulnerability) {
        this.relatedVulnerability = relatedVulnerability;
    }

    /**
     * Test if two arcs are equals
     *
     * @param arc the arc to test with current arc
     * @return true if the arcs are equals
     */
    public boolean equals(InformationSystemGraphArc arc) {
        return arc.getDestination().equals(this.getDestination()) && arc.getSource().equals(this.getSource());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof InformationSystemGraphArc)
            return this.equals((InformationSystemGraphArc) obj);
        return super.equals(obj);
    }
}
