package server.scoring;


public class Arc {

    /**
     * The source id
     */
    private double Source;

    /**
     * the destination id
     */
    private double Destination;

    /**
     * Instantiates a new Arc.
     *
     * @param source      the source id
     * @param destination the destination id
     */
    public Arc(double source, double destination) {
        Source = source;
        Destination = destination;
    }

    /**
     * Gets source.
     *
     * @return the source id
     */
    public double getSource() {
        return Source;
    }

    /**
     * Sets source.
     *
     * @param source the source id
     */
    public void setSource(double source) {
        Source = source;
    }

    /**
     * Gets destination.
     *
     * @return the destination id
     */
    public double getDestination() {
        return Destination;
    }

    /**
     * Sets destination.
     *
     * @param destination the destination id
     */
    public void setDestination(double destination) {
        Destination = destination;
    }

}
