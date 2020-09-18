package server.attackgraph;


public class Arc implements Cloneable {
    /**
     * The source vertex of the arc
     */
    public Vertex source;

    /**
     * The destination vertex of the arc
     */
    public Vertex destination;

    /**
     * Create an arc from source to destination
     *
     * @param source      The source vertex
     * @param destination The destinatino vertex
     */
    public Arc(Vertex source, Vertex destination) {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public Arc clone() throws CloneNotSupportedException {
        return (Arc) super.clone();
    }

    @Override
    public String toString() {
        return "Arc [destination=" + destination + ", source=" + source + "]";
    }

}
