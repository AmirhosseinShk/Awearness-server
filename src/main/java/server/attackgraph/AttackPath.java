package server.attackgraph;

import server.hostInformation.InformationSystem;
import server.hostInformation.graph.InformationSystemGraph;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.FileInputStream;
import java.util.*;


public class AttackPath extends MulvalAttackGraph implements Cloneable {

    /**
     * The scoring of the attack path (should be between 0 and 1)
     */
    public double scoring = 0;
    /**
     * The goal of the attacker
     */
    Vertex goal = null;


    public static List<AttackPath> loadAttackPathsFromFile(String attackPathsFilePath, AttackGraph relatedAttackGraph) throws Exception {
        FileInputStream file = new FileInputStream(attackPathsFilePath);
        SAXBuilder sxb = new SAXBuilder();
        Document document = sxb.build(file);
        Element root = document.getRootElement();

        List<AttackPath> result = new ArrayList<AttackPath>();

        List<Element> attackPathsElements = root.getChildren("attack_path");
        if (!attackPathsElements.isEmpty()) {
            for (Element attackPathElement : attackPathsElements) {
                if (attackPathElement != null) {
                    AttackPath attackPath = new AttackPath();
                    attackPath.loadFromDomElementAndAttackGraph(attackPathElement, relatedAttackGraph);
                    result.add(attackPath);
                }
            }
        }
        sortAttackPaths(result);

        return result;

    }

    /**
     * Sort attack paths with their scoring in descending order
     */
    public static void sortAttackPaths(List<AttackPath> attackPathList) {
        Collections.sort(attackPathList, new AttackPath.AttackPathComparator());
    }

    /**
     * @return the goal of the attack graph
     */
    public Vertex getGoal() {
        if (goal == null) {
            for (int i : this.vertices.keySet()) {
                Vertex vertex = this.vertices.get(i);
                vertex.computeParentsAndChildren(this);
                if (vertex.children.size() == 0)
                    goal = vertex;
            }
        }
        return goal;
    }

    /**
     * Load the attack path from a DOM element of a XML file (the XML file contains only the arcs, the vertices are in the attack graph)
     *
     * @param root        the DOM element
     * @param attackGraph the corresponding attack graph
     */
    public void loadFromDomElementAndAttackGraph(Element root, AttackGraph attackGraph) {
        Element scoringElement = root.getChild("scoring");
        if (scoringElement != null) {
            this.scoring = Double.parseDouble(scoringElement.getText());
        }

		/* Add all the arcs */
        Element arcs_element = root.getChild("arcs");
        if (arcs_element != null) {
            List<Element> arcs = arcs_element.getChildren("arc");
            for (Element arc_element : arcs) { //All arcs
                Element src_element = arc_element.getChild("dst"); //MULVAL XML FILES INVERSE DESTINATION AND DESTINATION
                Element dst_element = arc_element.getChild("src"); //MULVAL XML FILES INVERSE DESTINATION AND DESTINATION
                if (src_element != null && dst_element != null) {
                    Vertex destination = getVertexFromAttackGraph((int) Double.parseDouble(dst_element.getText()), attackGraph);
                    Vertex source = getVertexFromAttackGraph((int) Double.parseDouble(src_element.getText()), attackGraph);
                    Arc arc = new Arc(source, destination);
                    this.arcs.add(arc);
                }
            }
        }
    }

    /**
     * @param vertexID    the vertex number
     * @param attackGraph an attack graph
     * @return the existing vertex if it is already in the attack path else add this vertex from the attack graph
     */
    public Vertex getVertexFromAttackGraph(int vertexID, AttackGraph attackGraph) {
        if (this.vertices.containsKey(vertexID))
            return this.vertices.get(vertexID);
        else {
            Vertex result = attackGraph.getExistingOrCreateVertex(vertexID);
            this.vertices.put(vertexID, result);
            return result;
        }
    }

    /**
     * @return the dom element corresponding to this attack path XML file
     */
    public Element toDomXMLElement() {
        Element root = new Element("attack_path");

        Element scoringElement = new Element("scoring");
        scoringElement.setText(this.scoring + "");
        root.addContent(scoringElement);
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

        return root;
    }

    @Override
    public String toString() {
        String result = "AttackPath : ";
        for (int i : this.vertices.keySet()) {
            result += this.vertices.get(i).id + " - ";
        }
        return result;
    }

    /**
     * @param informationSystem the information system
     * @return The topology Graph associated to this attack path
     * @throws Exception
     */
    public InformationSystemGraph getRelatedTopologyGraph(InformationSystem informationSystem) throws Exception {
        InformationSystemGraph result = super.getRelatedTopologyGraph(informationSystem);

        result.addTarget(this.getGoal().getRelatedMachine(informationSystem));

        return result;
    }

    @Override
    public AttackPath clone() throws CloneNotSupportedException {
        AttackPath copie = (AttackPath) super.clone();

        if (this.goal != null)
            copie.goal = copie.vertices.get(this.goal.id);

        return copie;
    }

    public static class AttackPathComparator implements Comparator<AttackPath> {
        public int compare(AttackPath a1, AttackPath a2) {
            //descending order
            if (a1.scoring == a2.scoring) {
                return 0;
            } else if (a1.scoring < a2.scoring) {
                return 1;
            } else {
                return -1;
            }
        }
    }

}
