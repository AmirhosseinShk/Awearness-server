package server.attackgraph;


public class ImpactMetric {

	/**
	 * The impact value of the metric
	 */
	private double value;

	/**
	 * The weight of the metric
	 */
	private double weight;

	public ImpactMetric(double value, double weight) {
		this.value = value;
		this.weight = weight;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}

    @Override
    public String toString() {
        return "(" + value + "," + weight + ")";
    }
}
