package server.hostInformation;

import server.database.Database;
import server.topology.Topology;
import server.topology.component.Host;
import server.topology.component.IPAddress;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import server.networkfirewall.FlowMatrix;

/**
 * Class that represent the information system
 *
 */
public class InformationSystem implements Cloneable {


    /**
     * The network topology of the information system
     */
    private Topology topology = new Topology();

    /**
     * Is the attacker located on the internet in addition to {@link #machinesOfAttacker} ?
     */
    private boolean attackerLocatedOnInternet = false;

    /**
     * The flow matrix of the Information System
     */
    private FlowMatrix flowMatrix;

    /**
     * The list of machines that are mastered by the attacker
     */
    private List<InformationSystemHost> machinesOfAttacker = new ArrayList<InformationSystemHost>();


    /**
     * Create an empty information system
     */
    public InformationSystem() {

    }

    @Override
    public InformationSystem clone() throws CloneNotSupportedException {
        InformationSystem copie = (InformationSystem) super.clone();
        copie.topology = copie.topology.clone();
        return copie;
    }

    /**
     * Get a machine by its name or IP address. If the machine doesn't exist, add it to the topology
     *
     * @param str the name or IP address of the machine
     * @return the created or existing machine.
     * @throws Exception
     */
    public InformationSystemHost getHostByNameOrIPAddress(String str) throws Exception {
        if (IPAddress.isAnIPAddress(str)) {
            return getMachineByIPAddress(new IPAddress(str));
        } else {
            InformationSystemHost existingMachine = existingMachineByName(str);
            if (existingMachine != null)
                return existingMachine;
            InformationSystemHost newMachine = new InformationSystemHost(str, this.topology);
            this.topology.getHosts().add(newMachine);
            return newMachine;
        }
    }

    /**
     * Get an existing machine of the information system with its name
     *
     * @param name the name of the machine
     * @return the machine if it exists else null
     */
    public InformationSystemHost existingMachineByName(String name) {
        for (int i = 0; i < this.topology.getHosts().size(); i++) {
            if (this.topology.getHosts().get(i).getName().equals(name))
                return (InformationSystemHost) this.topology.getHosts().get(i);
        }
        if (name.equals("internet"))
            return new InformationSystemHost("internet", topology);

        return null;
    }

    /**
     * Get an existing machine of the information system with its name or ip address
     *
     * @param str the name or ip address of the machine
     * @return the machine if it exists else null
     * @throws Exception
     */
    public InformationSystemHost existingMachineByNameOrIPAddress(String str) throws Exception {
        if (IPAddress.isAnIPAddress(str))
            return (InformationSystemHost) topology.existingHostByIPAddress(new IPAddress(str));
        else
            return existingMachineByName(str);
    }

    /**
     * Get a machine by its IP address, or create a new one if it does not exist
     *
     * @param ipAddress an IP Address
     * @return The machine in the topology that has this IP Address. If this machine doesn't exists, just add a new one.
     * @throws Exception
     */
    public InformationSystemHost getMachineByIPAddress(IPAddress ipAddress) throws Exception {
        InformationSystemHost existingMachine = (InformationSystemHost) topology.existingHostByIPAddress(ipAddress);
        if (existingMachine != null)
            return existingMachine;
        InformationSystemHost newMachine = new InformationSystemHost(ipAddress.getAddress(), topology);
        newMachine.addInterface("int1", ipAddress.getAddress());
        this.topology.getHosts().add(newMachine);
        return newMachine;
    }

    /**
     * Save the attack graph in an xml file
     *
     * @param filePath the file path where the attack graph will be save
     * @throws Exception
     */
    public void saveToXmlFile(String filePath) throws Exception {
        XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
        output.output(toDomXMLElement(), new FileOutputStream(filePath));
    }

    /**
     * Get the XML DOM element of this information system
     *
     * @return the dom element corresponding to this topology with the format of the tva report file
     */
    public Element toDomXMLElement() {
        Element root = new Element("topology");

        //machines
        for (int i = 0; i < this.topology.getHosts().size(); i++) {
            InformationSystemHost machine = (InformationSystemHost) this.topology.getHosts().get(i);
            root.addContent(machine.toDomXMLElement());
        }

        return root;
    }

    /**
     * Load a network topology from a dom element
     *
     * @param domElement the dom element of an xml file
     * @throws Exception
     */
    public void loadFromDomElement(Element domElement, Database db) throws Exception {
        if (domElement == null)
            return;
        List<Element> hostsElement = domElement.getChildren("machine");
        for (Element hostElement : hostsElement) {
            InformationSystemHost host = new InformationSystemHost(this.topology);
            host.loadFromDomElement(hostElement, this.topology, db);
            this.topology.getHosts().add(host);
        }
        this.flowMatrix = new FlowMatrix(domElement.getChild("flow-matrix"), this.topology);
    }


    /**
     * Load the topology from an xml file
     *
     * @param XMLFilePath the path to the xml file
     * @throws Exception
     */
    public void loadFromXMLFile(String XMLFilePath, Database db) throws Exception {
        FileInputStream file = new FileInputStream(XMLFilePath);
        SAXBuilder sxb = new SAXBuilder();
        Document document = sxb.build(file);
        Element root = document.getRootElement();

        this.loadFromDomElement(root, db);
    }

    /**
     * Generates the Json object relative to the hosts list
     * @return the Json Object containing the hosts list
     */
    public JSONObject getHostsListJson() {
        //Build the json list of hosts
        JSONObject json = new JSONObject();
        JSONArray hosts_array = new JSONArray();
        for (Host host : this.getTopology().getHosts()) {
            InformationSystemHost informationSystemHost = (InformationSystemHost) host;
            JSONObject host_object = new JSONObject();
            host_object.put("name", informationSystemHost.getName());
            hosts_array.put(host_object);
        }
        json.put("hosts", hosts_array);
        return json;
    }

    /**
     * Get the network topology
     *
     * @return the topology
     */
    public Topology getTopology() {
        return topology;
    }
    
}
