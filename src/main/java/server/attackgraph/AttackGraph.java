package server.attackgraph;

import server.attackgraph.fact.DataLogCommand;
import server.attackgraph.fact.FactType;
import server.hostInformation.InformationSystem;
import server.hostInformation.InformationSystemHost;
import server.hostInformation.graph.InformationSystemGraph;
import server.hostInformation.graph.InformationSystemGraphArc;
import server.hostInformation.graph.InformationSystemGraphVertex;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.scoring.ScoringAttackPath;


public class AttackGraph implements Cloneable {
    /**
     * A list of vertices
     */
    public HashMap<Integer, Vertex> vertices = new HashMap<Integer, Vertex>();

    /**
     * A list of arcs between the vertices
     */
    public ArrayList<Arc> arcs = new ArrayList<Arc>();

    /**
     * The global score of the attack graph
     */
    public double globalScore = 0;

    /**
     * Check if a vertex exists, if it doesn't, creates a new one
     *
     * @param id_vertex the id of the vertex to check
     * @return a vertex of identifier id_vertex
     */
    public Vertex getExistingOrCreateVertex(int id_vertex) {
        Vertex result = vertices.get(id_vertex);
        if (result == null) {
            result = new Vertex(id_vertex);
            vertices.put(id_vertex, result);
        }
        return result;
    }

    /**
     * @param id the identifier of the vertex in the attack graph
     * @return the vertex from the attack graph
     * @throws Exception
     */
    public Vertex getVertexFromId(int id) throws Exception {
        Vertex vertex = this.vertices.get(id);
        if (vertex == null)
            throw new Exception("The vertex " + id + " is not in this attack graph");
        return vertex;
    }
    
        /**
     * @param outputPath the path in which the XML attack paths are saved
     * @return The list of attack path extracted from this attack graph
     * @throws Exception
     */
    public List<AttackPath> scoreAttackGraphAndGetAttackPaths(String outputPath, double previousMaxScore) throws Exception {
        double[] vertexIDTable = new double[this.getNumberOfVertices()];
        String[] vertexFactTable = new String[this.getNumberOfVertices()];
        double[] vertexMulvalMetricTable = new double[this.getNumberOfVertices()];
        String[] vertexTypeTable = new String[this.getNumberOfVertices()];

        double[] arcSrcTable = new double[this.arcs.size()];
        double[] arcDstTable = new double[this.arcs.size()];
        ImpactMetric[][] impactMetrics = new ImpactMetric[this.getNumberOfVertices()][];

        int i = 0;
        Logger.getLogger(AttackGraph.class.getName()).log(Level.INFO , "Generating inputs for scoring function");
        for (Integer key : this.vertices.keySet()) {
            Vertex vertex = this.vertices.get(key);

            vertexIDTable[i] = vertex.id;
            vertexFactTable[i] = vertex.fact.factString;
            vertexMulvalMetricTable[i] = vertex.mulvalMetric;
            vertexTypeTable[i] = vertex.type.toString().toUpperCase();
            impactMetrics[i] = new ImpactMetric[vertex.impactMetrics.size()];
            for(int j = 0; j < vertex.impactMetrics.size() ; j++) {
                impactMetrics[i][j] = vertex.impactMetrics.get(j);
            }

            i++;
        }

        for (int j = 0; j < this.arcs.size(); j++) {
            Arc arc = this.arcs.get(j);
            arcDstTable[j] = arc.source.id;
            arcSrcTable[j] = arc.destination.id;
        }
        System.out.println("Compute global score and compute attack paths");
        this.globalScore = ScoringAttackPath.ScoreCalculator(vertexIDTable, vertexFactTable, vertexMulvalMetricTable, vertexTypeTable,
                arcSrcTable, arcDstTable, impactMetrics, outputPath, previousMaxScore);

        return AttackPath.loadAttackPathsFromFile(outputPath, this);
    }
    
     /**
     * Save the attack graph in an xml file
     *
     * @param filePath the path in which the attack graph is saved
     * @throws Exception
     */
    public void saveToXmlFile(String filePath) throws Exception {
        XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
        output.output(toDomElement(), new FileOutputStream(filePath));
    }

    /**
     * Delete all arcs from a vertex to another
     *
     * @param fromVertex the source vertex
     * @param toVertex   the destination vertex
     */
    public void deleteArc(Vertex fromVertex, Vertex toVertex) {
        for (int i = 0; i < this.arcs.size(); i++) {
            Arc arc = this.arcs.get(i);
            if (arc.source.id == fromVertex.id && arc.destination.id == toVertex.id) {
                this.arcs.remove(i);
            }
        }
    }

    /**
     * @return the number of vertices in the attack graph
     */
    public int getNumberOfVertices() {
        return this.vertices.size();
    }

    /**
     * @return the adjacency matrix related to the attack graph
     */
    public int[][] getAdjacencyMatrix() {
        int numberOfVertices = this.getNumberOfVertices();
        int[][] adjacencyMatrix = new int[numberOfVertices][numberOfVertices];
        for (Arc arc : this.arcs) {
            adjacencyMatrix[arc.source.id - 1][arc.destination.id - 1] = 1;
        }
        return adjacencyMatrix;
    }

    protected void explore(List<List<Vertex>> attackPathsList, int[] path, int target, boolean[] taboo, int[][] adjacencymatrix, int n, int position, int depth) throws Exception {
        path[depth] = position;
        // end
        if (position == target) {
            List<Vertex> attackPath = new ArrayList<Vertex>();
            for (int i = 0; i <= depth; i++) {
                attackPath.add(this.getVertexFromId(path[i] + 1));
            }
            attackPathsList.add(attackPath);
            return;
        }

        taboo[position] = true; // add a taboo

        // explore the remaining paths
        for (int i = 0; i < n; i++) {
            if (adjacencymatrix[position][i] == 0 || taboo[i]) continue;
            explore(attackPathsList, path, target, taboo, adjacencymatrix, n, i, depth + 1);
        }

        taboo[position] = false; // remove the taboo
    }

    /**
     * @return the highest vertex id number
     */
    public int getHighestVertexId() {
        int max = 0;
        for (int i : vertices.keySet()) {
            if (i >= max)
                max = i;
        }
        return max;
    }

    /**
     * @return the dom element corresponding to this attack graph XML file
     */
    public Element toDomElement() {
        Element root = new Element("attack_graph");

        //arcs
        Element arcsElement = new Element("arcs");
        root.addContent(arcsElement);
        for (Arc arc : arcs) {
            Element arcElement = new Element("arc");
            arcsElement.addContent(arcElement);
            Element srcElement = new Element("src");
            srcElement.setText(arc.destination.id + "");
            arcElement.addContent(srcElement);
            Element dstElement = new Element("dst");
            dstElement.setText(arc.source.id + "");
            arcElement.addContent(dstElement);
        }

        //vertices
        Element verticesElement = new Element("vertices");
        root.addContent(verticesElement);
        for (int key : vertices.keySet()) {
            Vertex vertex = vertices.get(key);

            Element vertexElement = new Element("vertex");
            verticesElement.addContent(vertexElement);

            Element idElement = new Element("id");
            idElement.setText(vertex.id + "");
            vertexElement.addContent(idElement);

            Element factElement = new Element("fact");
            factElement.setText(vertex.fact.factString);
            vertexElement.addContent(factElement);

            Element metricElement = new Element("metric");
            metricElement.setText(vertex.mulvalMetric + "");
            vertexElement.addContent(metricElement);

            Element typeElement = new Element("type");
            typeElement.setText(vertex.type.toString().toUpperCase());
            vertexElement.addContent(typeElement);
        }

        return root;
    }


    @Override
    public AttackGraph clone() throws CloneNotSupportedException {
        AttackGraph copie = (AttackGraph) super.clone();

        //Copie the vertices

        copie.vertices = new HashMap<Integer, Vertex>();
        for (Integer i : this.vertices.keySet()) {
            copie.vertices.put(i, this.vertices.get(i).clone());
        }

        copie.arcs = new ArrayList<Arc>();

        //Change all the vertices references in the arcs
        for (int i = 0; i < this.arcs.size(); i++) {
            Vertex destination = copie.vertices.get(this.arcs.get(i).destination.id);
            Vertex source = copie.vertices.get(this.arcs.get(i).source.id);
            Arc arc = new Arc(source, destination);
            copie.arcs.add(arc);
        }


        //change all the references in the vertices
        for (Integer i : copie.vertices.keySet()) {
            Vertex copieVertex = copie.vertices.get(i);
            copieVertex.computeParentsAndChildren(copie);
        }

        return copie;
    }

    @Override
    public String toString() {
        String result = "";

        result += "\nVertices = \n";
        for (Integer key : vertices.keySet()) {
            result += vertices.get(key) + "\n";
        }

        result += "AttackGraph : \n Arcs=\n";
        for (Arc arc : arcs) {
            result += arc + "\n";
        }

        return result;
    }


    /**
     * Browse all vertices and check if the vertex can have a metric (is an execCode)
     *
     * @param informationSystem the information system
     * @throws Exception
     */
    public void loadMetricsFromTopology(InformationSystem informationSystem) throws Exception {
        for (Integer key : vertices.keySet()) {
            Vertex vertex = vertices.get(key);
            if (vertex.fact != null && vertex.fact.type == FactType.DATALOG_FACT && vertex.fact.datalogCommand.command.equals("execCode")) {
                //We are in an execCode
                String hostName = vertex.fact.datalogCommand.params[0];
                if (hostName != null && !hostName.isEmpty()) {
                    InformationSystemHost host = informationSystem.getHostByNameOrIPAddress(hostName);
                    if (host != null) {
                        ImpactMetric metric = new ImpactMetric(host.getMetric(), 1);
                        vertex.impactMetrics.add(metric);
                    }
                }

            }
        }
    }

    /**
     * @param informationSystem the information system
     * @return The topology Graph associated to this attack path
     * @throws Exception
     */
    public InformationSystemGraph getRelatedTopologyGraph(InformationSystem informationSystem) throws Exception {
        InformationSystemGraph result = new InformationSystemGraph();

        List<Vertex> vertices = new ArrayList<Vertex>(this.vertices.values());
        for (Vertex vertex : vertices) {
            if (vertex.fact.type == FactType.DATALOG_FACT && vertex.fact.datalogCommand != null) {
                DataLogCommand command = vertex.fact.datalogCommand;
                switch (command.command) {
                    case "hacl":
                        InformationSystemGraphVertex from = null;
                        InformationSystemGraphVertex to = null;
                        String relatedVulneravility = null;
                        if (command.params[0].equals("internet") || command.params[0].equals("1.1.1.1") || command.params[0].equals("internet_host")) {
                            from = result.getMachineVertex(informationSystem.getHostByNameOrIPAddress("internet_host"));
                        } else {
                            InformationSystemHost machine = informationSystem.getHostByNameOrIPAddress(command.params[0]);
                            if (machine != null) {
                                from = result.getMachineVertex(machine);
                            }
                        }
                        if (command.params[1].equals("internet") || command.params[1].equals("1.1.1.1"))
                            to = result.getMachineVertex(informationSystem.getHostByNameOrIPAddress("1.1.1.1"));
                        else {
                            InformationSystemHost machine = informationSystem.getHostByNameOrIPAddress(command.params[1]);
                            if (machine != null) {
                                to = result.getMachineVertex(machine);
                            }
                        }
                        if (from != null && to != null) {
                            InformationSystemGraphArc arc = new InformationSystemGraphArc();
                            arc.setSource(from);
                            arc.setDestination(to);
                            vertex.computeParentsAndChildren(this);
                            //Try to find (if applicable) the related vulnerability
                            Vertex directAccessChild = vertex.childOfType(true, "direct network access");
                            if (directAccessChild == null) {
                                directAccessChild = vertex.childOfType(true, "multi-hop access");
                            }
                            if (directAccessChild != null) {
                                directAccessChild.computeParentsAndChildren(this);
                                Vertex netAccessChild = directAccessChild.childOfType(false, "netAccess");
                                if (netAccessChild != null) {
                                    netAccessChild.computeParentsAndChildren(this);
                                    Vertex remoteExploitChild = netAccessChild.childOfType(true, "remote exploit of a server program");
                                    if (remoteExploitChild != null) {
                                        remoteExploitChild.computeParentsAndChildren(this);
                                        Vertex vulnExistParent = remoteExploitChild.parentOfType(false, "vulExists");
                                        if (vulnExistParent != null && vulnExistParent.fact.datalogCommand.params.length > 2) {
                                            relatedVulneravility = vulnExistParent.fact.datalogCommand.params[1];
                                        }
                                    }
                                }

                            }

                            if (relatedVulneravility != null)
                                arc.setRelatedVulnerability(relatedVulneravility);

                            if (!result.getArcs().contains(arc))
                                result.getArcs().add(arc);
                        }
                        break;
                    case "attackerLocated":
                        InformationSystemGraphVertex attackerVertex = null;
                        if (command.params[0].equals("internet") || command.params[0].equals("1.1.1.1"))
                            attackerVertex = result.getMachineVertex(informationSystem.getHostByNameOrIPAddress("1.1.1.1"));
                        else {
                            InformationSystemHost machine = informationSystem.getHostByNameOrIPAddress(command.params[0]);
                            if (machine != null) {
                                attackerVertex = result.getMachineVertex(machine);
                            }
                        }
                        if (attackerVertex != null)
                            attackerVertex.setMachineOfAttacker(true);
                        break;
                    case "vulExists":
                        InformationSystemHost machine = informationSystem.getHostByNameOrIPAddress(command.params[0]);
                        if (machine != null) {
                            result.getMachineVertex(machine).setCompromised(true);
                        }
                        break;
                }
            }
        }

        return result;
    }
}
