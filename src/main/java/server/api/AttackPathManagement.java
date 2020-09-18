package server.api;

import java.util.List;
import server.attackgraph.AttackGraph;
import server.attackgraph.AttackPath;
import server.monitoring.Monitoring;
import server.properties.ProjectProperties;
import org.jdom2.Element;
import org.json.JSONObject;


public class AttackPathManagement {
    
     /**
     * Extract
     *
     * @param attackGraph the attack graph
     * @return the scores extracted from the attack graph 
     */
    public static List<AttackPath> scoreAttackPaths(AttackGraph attackGraph, double previousMaxScore) {
        try {
            String outputFolderPath = ProjectProperties.getInstance().getProperty("tmp-output");
            attackGraph.saveToXmlFile(outputFolderPath + "/attack-graph-to-score.xml");
            return attackGraph.scoreAttackGraphAndGetAttackPaths(outputFolderPath + "/scored-attack-paths.xml", previousMaxScore);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    /**
     * @param monitoring a monitoring object
     * @return the XML contained all the attack paths 
     */
    public static Element getAttackPathsXML(Monitoring monitoring) {
        if (monitoring == null)
            return null;
        List<AttackPath> attackPaths = monitoring.getAttackPathList();
        Element root = new Element("attack_paths");
        for (AttackPath attackPath : attackPaths) {
            Element element = attackPath.toDomXMLElement();
            root.addContent(element);
        }

        return root;
    }
    
        /**
     * @param monitoring a monitoring object
     * @param id the attack path id
     * @return the XML contained special attack paths 
     */
    public static Element getAttackPathXML(Monitoring monitoring, Integer id) {
        if (monitoring == null)
            return null;
        List<AttackPath> attackPaths = monitoring.getAttackPathList();

        if (id >= 0 && id < attackPaths.size()) {
            return attackPaths.get(id).toDomXMLElement();
        }
        return null;
    }

    /**
     * @param monitoring a monitoring object
     * @param id  the the attack path id  
     * @return the JSON contained special attack paths 
     */
    public static JSONObject getAttackPathTopologicalJson(Monitoring monitoring, Integer id) {
        if (monitoring == null)
            throw new IllegalStateException("The monitoring object is null");
        List<AttackPath> attackPaths = monitoring.getAttackPathList();

        if (id >= 0 && id < attackPaths.size()) {
            AttackPath attackPath = attackPaths.get(id);
            try {
                return attackPath.getRelatedTopologyGraph(monitoring.getInformationSystem()).toJsonObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new IllegalStateException("This attack path can not be found.");
    }

    /**
     * @param monitoring a monitoring object
     * @return the JSON contain topological attack graph 
     */
    public static JSONObject getAttackGraphTopologicalJson(Monitoring monitoring) {
        if (monitoring == null)
            throw new IllegalStateException("The monitoring object is null");
        AttackGraph attackGraph = monitoring.getAttackGraph();

        try {
            return attackGraph.getRelatedTopologyGraph(monitoring.getInformationSystem()).toJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new IllegalStateException("This attack path can not be found.");
    }
}
