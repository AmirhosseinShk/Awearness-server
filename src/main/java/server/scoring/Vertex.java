package server.scoring;

import server.attackgraph.ImpactMetric;

public class Vertex {

    /**
     * The vertex ID
     */
    private double ID;

    /**
     * The vertex Fact
     */
    private String Fact;

    /**
     * The vertex metric
     */
    private double MulvalMetric;

    /**
     * The vertex type
     */
    private String Type;


    /**
     * The vertex impact metrics
     */
    private ImpactMetric[] ImpactMetrics = null;

    /**
     * Instantiates a new Vertex.
     *
     * @param id     the id
     * @param fact   the fact
     * @param metric the metric
     * @param type   the type
     */
    public Vertex(double id, String fact, double metric, String type) {
        ID = id;
        Fact = fact;
        MulvalMetric = metric;
        setType(type);
    }

    /**
     * Instantiates a new Vertex.
     *
     * @param vertex the vertex
     */
    public Vertex(Vertex vertex) {
        ID = vertex.ID;
        Fact = vertex.Fact;
        MulvalMetric = vertex.MulvalMetric;
        setType(vertex.getType());
    }

    /**
     * Gets iD.
     *
     * @return the iD
     */
    public double getID() {
        return ID;
    }

    /**
     * Sets iD.
     *
     * @param id the id
     */
    public void setID(double id) {
        ID = id;
    }

    /**
     * Gets fact.
     *
     * @return the fact
     */
    public String getFact() {
        return Fact;
    }

    /**
     * Sets fact.
     *
     * @param fact the fact
     */
    public void setFact(String fact) {
        Fact = fact;
    }

    /**
     * Gets mulval metric.
     *
     * @return the mulval metric
     */
    public double getMulvalMetric() {
        return MulvalMetric;
    }

    /**
     * Sets mulval metric.
     *
     * @param metric the metric
     */
    public void setMulvalMetric(double metric) {
        MulvalMetric = metric;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return Type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        Type = type;
    }

    //END CODE KM

    /**
     * Get impact metrics.
     *
     * @return the impact metric [ ]
     */
    public ImpactMetric[] getImpactMetrics() {
        return ImpactMetrics;
    }

    /**
     * Sets impact metrics.
     *
     * @param impactMetrics the impact metrics
     */
    public void setImpactMetrics(ImpactMetric[] impactMetrics) {
        ImpactMetrics = impactMetrics;
    }
}
