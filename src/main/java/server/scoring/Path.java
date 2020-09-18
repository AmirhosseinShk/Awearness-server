package server.scoring;

public class Path {

    /**
     * The list of ids
     */
    private double[] Path;

    /**
     * Instantiates a new Path.
     *
     * @param PathLength the path length
     */
    public Path(int PathLength) {
        Path = new double[PathLength + 1];
        Path[0] = -1;
    }

    /**
     * Get path.
     *
     * @return the double [ ]
     */
    public double[] getPath() {
        return Path;
    }

    /**
     * Sets path.
     *
     * @param path the path
     */
    public void setPath(double[] path) {
        Path = path;
    }

}
