package server.scoring;


public class Graph {

    /**
     * The arcs of the graph
     */
    private Arc[] Arcs;

    /**
     * The vertices of the graph
     */
    private Vertex[] Vertices;

    /**
     * Instantiates a new Graph.
     *
     * @param arcs     the arcs
     * @param vertices the vertices
     */
    public Graph(Arc[] arcs, Vertex[] vertices) {
        setArcs(arcs);
        setVertices(vertices);
    }

    /**
     * Get predecessors.
     *
     * @param arcs     the arcs
     * @param vertexID the vertex iD
     * @return the double [ ]
     */
    public static double[] getPredecessors(Arc[] arcs, double vertexID) {
        int counter = 0;
        double[] result = null;
        //the first for loop is to get the cardinality of the query result
        for (Arc arc : arcs) {
            if (arc.getSource() == vertexID) {
                counter++;
            }
        }
        if (counter != 0) {
            result = new double[counter];
            counter = 0;
            for (Arc arc : arcs) {
                if (arc.getSource() == vertexID) {
                    result[counter] = arc.getDestination();
                    counter++;
                }
            }
        }
        return result;
    }

    /**
     * Get vertices on type and fact.
     *
     * @param vertices the vertices
     * @param type     the type
     * @return the vertex [ ]
     */
    public static Vertex[] getVerticesOnTypeAndFact(Vertex[] vertices, String type) {
        int counter = 0;
        Vertex[] result = null;
        //the first for loop is to get the cardinality of the query result
        for (Vertex vertice1 : vertices) {
            if (vertice1.getType().equals(type) && vertice1.getFact().startsWith("execCode")) {
                counter++;
            }
        }
        if (counter != 0) {
            result = new Vertex[counter];
            counter = 0;
            for (Vertex vertice : vertices) {
                if (vertice.getType().equals(type) && vertice.getFact().startsWith("execCode")) {
                    result[counter] = vertice;
                    counter++;
                }
            }
        }
        return result;
    }

    /**
     * Get predecessors.
     *
     * @param arcs     the arcs
     * @param vertices the vertices
     * @param vertexID the vertex iD
     * @return the vertex [ ]
     */
    public static Vertex[] getPredecessors(Arc[] arcs, Vertex[] vertices, double vertexID) {
        int counter = 0;
        Vertex[] result = null;
        //the first for loop is to get the cardinality of the query result
        for (Arc arc1 : arcs) {
            if (arc1.getSource() == vertexID) {
                counter++;
            }
        }
        if (counter != 0) {
            result = new Vertex[counter];
            counter = 0;
            for (Arc arc : arcs) {
                if (arc.getSource() == vertexID) {
                    result[counter] = getVertexOnID(vertices, arc.getDestination());
                    counter++;
                }
            }
        }
        return result;
    }

    /**
     * Get ingoing arcs number.
     *
     * @param arcs     the arcs
     * @param vertexID the vertex iD
     * @return the double
     */
    public static double getIngoingArcsNumber(Arc[] arcs, double vertexID) {
        double counter = 0;
        for (Arc arc : arcs) {
            if (arc.getSource() == vertexID) {
                counter++;
            }
        }

        return counter;
    }

    /**
     * Get outgoing arcs number.
     *
     * @param arcs     the arcs
     * @param vertexID the vertex iD
     * @return the double
     */
    public static double getOutgoingArcsNumber(Arc[] arcs, double vertexID) {
        double counter = 0;
        for (Arc arc : arcs) {
            if (arc.getDestination() == vertexID) {
                counter++;
            }
        }

        return counter;
    }

    /**
     * Get vertices on type.
     *
     * @param vertices the vertices
     * @param type     the type
     * @return the vertex [ ]
     */
    public static Vertex[] getVerticesOnType(Vertex[] vertices, String type) {
        int counter = 0;
        Vertex[] result = null;
        //the first for loop is to get the cardinality of the query result
        for (Vertex vertice1 : vertices) {
            //System.out.println("i:"+i);
            if (vertice1.getType().equals(type)) {
                counter++;
            }
        }
        if (counter != 0) {
            result = new Vertex[counter];
            counter = 0;
            for (Vertex vertice : vertices) {
                if (vertice.getType().equals(type)) {
                    result[counter] = vertice;
                    counter++;
                }
            }
        }
        return result;
    }

    /**
     * Get vertex on iD.
     *
     * @param vertices the vertices
     * @param ID       the iD
     * @return the vertex
     */
    public static Vertex getVertexOnID(Vertex[] vertices, double ID) {
        //the first for loop is to get the cardinality of the query result
        for (Vertex vertice : vertices) {
            if (vertice.getID() == ID) {
                return vertice;
            }
        }
        return null;
    }

    /**
     * Get arcs.
     *
     * @return the arc [ ]
     */
    public Arc[] getArcs() {
        return Arcs;
    }

    /**
     * Sets arcs.
     *
     * @param arcs the arcs
     */
    public void setArcs(Arc[] arcs) {
        Arcs = arcs;
    }

    /**
     * Get vertices.
     *
     * @return the vertex [ ]
     */
    public Vertex[] getVertices() {
        return Vertices;
    }

    /**
     * Sets vertices.
     *
     * @param vertices the vertices
     */
    public void setVertices(Vertex[] vertices) {
        Vertices = vertices;
    }

}
