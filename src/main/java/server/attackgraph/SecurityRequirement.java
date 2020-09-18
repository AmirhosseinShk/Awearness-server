package server.attackgraph;


public class SecurityRequirement {
    /**
     * The name of the security requirement
     */
    private String name;

    /**
     * The metric of the security requirement
     */
    private double metric = 0.;

    /**
     * Create a security requirement from name and metric
     *
     * @param name   the name of security requirement
     * @param metric the metric associated
     */
    public SecurityRequirement(String name, double metric) {
        this.name = name;
        this.metric = metric;
    }

    /**
     * Transform the plain text metric to a double value (used for calculation)
     *
     * @param plainText the textual metric
     * @return a double value corresponding to this metric
     */
    public static double getMetricValueFromPlainText(String plainText) {
        switch (plainText.toLowerCase()) {
            case "none":
                return 0.0;
            case "minor":
                return 30.0;
            case "medium":
                return 60.0;
            case "severe":
                return 90.0;
            case "Dangerous":
                return 99.0;
            default:
                return 0;
        }
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
     * @return the metric
     */
    public double getMetric() {
        return metric;
    }

    /**
     * @param metric the metric to set
     */
    public void setMetric(double metric) {
        this.metric = metric;
    }

    /**
     * Returns the metric has a plain texte string (generally for display)
     *
     * @return the name associated to the metric value
     */
    public String getMetricPlainText() {
        if (metric < 0)
            return "None";
        else if (metric < 30)
            return "Minor";
        else if (metric < 60)
            return "Medium";
        else if (metric < 90)
            return "Severe";
        else
            return "Dangerous";
    }
}
