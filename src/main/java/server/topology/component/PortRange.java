package server.topology.component;

public class PortRange implements Cloneable {
    /**
     * If true, the port range contains all available ports (1-65535)
     */
    private boolean any = false;

    /**
     * The minimum port
     */
    private int min;

    /**
     * The maximum port
     */
    private int max;

    /**
     * Create a range with a minimum and a maximum
     *
     * @param min the minimum
     * @param max the maximum
     */
    public PortRange(int min, int max) {
        super();
        this.setMin(min);
        this.setMax(max);
    }

    /**
     * Create a range of an ports
     *
     * @param any true if the port range contains all possible ports
     */
    public PortRange(boolean any) {
        super();
        this.setAny(any);
    }

    /**
     * @param string a port range string
     * @return a new port range from a string containing "any", "all", "22" or "21-23"
     */
    public static PortRange fromString(String string) {
        if (string.toLowerCase().contains("any")) {
            return new PortRange(true);
        } else if (string.toLowerCase().contains("all")) {
            return new PortRange(true);
        } else if (string.toLowerCase().contains("*")) {
            return new PortRange(true);
        } else if (string.toLowerCase().contains("-")) {
            return new PortRange(Integer.parseInt(string.split("-")[0]), Integer.parseInt(string.split("-")[1]));
        } else if (string.toLowerCase().contains("httpport")) {
            return new PortRange(80, 80);
        } else {
            return new PortRange(Integer.parseInt(string), Integer.parseInt(string));
        }
    }

    /**
     * @return the any
     */
    public boolean isAny() {
        return any;
    }

    /**
     * @param any the any to set
     */
    public void setAny(boolean any) {
        this.any = any;
    }

    /**
     * @return the min
     */
    public int getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(int min) {
        this.min = min;
    }

    /**
     * @return the max
     */
    public int getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(int max) {
        this.max = max;
    }
    /**
     * @param a a port range
     * @return true if a is in the range else false
     */
    public boolean inRange(PortRange a) {
        return isAny() || (a.getMax() <= getMax() && a.getMin() >= getMin());

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isAny() ? 1231 : 1237);
        result = prime * result + getMax();
        result = prime * result + getMin();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PortRange other = (PortRange) obj;
        return isAny() == other.isAny() && getMax() == other.getMax() && getMin() == other.getMin();
    }

    @Override
    public PortRange clone() throws CloneNotSupportedException {
        return (PortRange) super.clone();
    }

    @Override
    public String toString() {
        if (this.isAny())
            return "any";
        else if (getMin() == getMax())
            return getMin() + "";
        else
            return getMin() + "-" + getMax();
    }

}
