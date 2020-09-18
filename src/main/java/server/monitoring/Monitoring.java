package server.monitoring;

import server.attackgraph.AttackPath;
import server.attackgraph.MulvalAttackGraph;
import server.hostInformation.InformationSystem;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the monitoring (an information system, an attack graph and attack paths).
 *
 */
public class Monitoring {

    /**
     * The information system
     */
    private InformationSystem informationSystem;

    /**
     * The MulVAL attack graph
     */
    private MulvalAttackGraph attackGraph;

    /**
     * The list of attack path
     */
    private List<AttackPath> attackPathList = new ArrayList<AttackPath>();


    /**
     * Create a monitoring object with the folder where the cost parameters may be stored
     */
    public Monitoring(){
        
    }

    /**
     * Get the information system
     *
     * @return the information system
     */
    public InformationSystem getInformationSystem() {
        return informationSystem;
    }

    /**
     * Set the information system
     *
     * @param informationSystem new information system
     */
    public void setInformationSystem(InformationSystem informationSystem) {
        this.informationSystem = informationSystem;
    }

    /**
     * Gets attack graph.
     *
     * @return the attack graph
     */
    public MulvalAttackGraph getAttackGraph() {
        return attackGraph;
    }

    /**
     * Sets attack graph.
     *
     * @param attackGraph the attack graph
     */
    public void setAttackGraph(MulvalAttackGraph attackGraph) {
        this.attackGraph = attackGraph;
    }

    /**
     * Gets attack path list.
     *
     * @return the attack path list
     */
    public List<AttackPath> getAttackPathList() {
        return attackPathList;
    }

    /**
     * Sets attack path list.
     *
     * @param attackPathList the attack path list
     */
    public void setAttackPathList(List<AttackPath> attackPathList) {
        this.attackPathList = attackPathList;
    }

}