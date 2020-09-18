package server.hostInformation;

import server.attackgraph.SecurityRequirement;
import server.database.Database;
import server.topology.Topology;
import server.topology.component.Host;
import server.topology.component.IPAddress;
import server.topology.component.Network;
import server.topology.component.Interface;
import server.topology.component.Route;
import server.vulnerability.Vulnerability;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import server.topology.component.Protocol;

/**
 * Class that represents a host
 *
 */
public class InformationSystemHost extends Host {

    /**
     * The network services running on this host
     */
    private Map<String, Service> services = new HashMap<String, Service>();

    /**
     * The accounts on this host
     */
    private Map<String, HostAccount> accounts = new HashMap<String, HostAccount>();

    /**
     * The security requirements that needs this machine
     */
    private List<SecurityRequirement> securityRequirements = new ArrayList<SecurityRequirement>();

    /**
     * Create an empty host
     *
     * @param topology the network topology
     */
    public InformationSystemHost(Topology topology) {
        super(topology);
    }

    /**
     * Create a host with its name
     *
     * @param str      the name of the host
     * @param topology the topology
     */
    public InformationSystemHost(String str, Topology topology) {
        super(str, topology);
    }

    /**
     * @return the services
     */
    public Map<String, Service> getServices() {
        return services;
    }

    /**
     * @return the dom element corresponding to this host with the format of the tva report file
     */
    public Element toDomXMLElement() {
        Element root = new Element("machine");

        Element nameElement = new Element("name");
        nameElement.setText(this.getName());
        root.addContent(nameElement);

        Element cpeElement = new Element("cpe");
        cpeElement.setText("cpe:/");
        root.addContent(cpeElement);

        //Interfaces
        Element interfacesElement = new Element("interfaces");
        root.addContent(interfacesElement);
        for (String key : getInterfaces().keySet()) {
            Interface intface = getInterfaces().get(key);
            interfacesElement.addContent(intface.toDomElement());
        }

        //Services
        Element servicesElement = new Element("services");
        root.addContent(servicesElement);
        for (String key : services.keySet()) {
            Service service = services.get(key);
            servicesElement.addContent(service.toDomXMLElement());
        }

        //Routes
        Element routesElement = new Element("routes");
        root.addContent(routesElement);
        for (int i = 0; i < this.getRoutingTable().getRouteList().size(); i++) {
            Route route = this.getRoutingTable().getRouteList().get(i);
            routesElement.addContent(route.toDomXMLElement());
        }

        return root;
    }

    /**
     * Load the host from a DOM element (extracted from an XML file)
     *
     * @param domElement the host root dom element
     * @param topology   the current network topology
     * @throws Exception
     */
    public void loadFromDomElement(Element domElement, Topology topology, Database db) throws Exception {
        Element nameElement = domElement.getChild("name");
        if (nameElement != null)
            this.setName(nameElement.getText());

        //security requirement
        Element securityRequirementElement = domElement.getChild("security_requirement");
        if (securityRequirementElement != null) {
            double securityRequirementValue = Double.parseDouble(securityRequirementElement.getText());
            SecurityRequirement securityRequirement = new SecurityRequirement("sec-req-xml", securityRequirementValue);
            this.addSecurityRequirements(securityRequirement);
        }

        //Host interfaces
        Element interfacesElement = domElement.getChild("interfaces");
        if (interfacesElement != null) {
            List<Element> interfaceListElement = interfacesElement.getChildren("interface");
            for (Element interfaceElement : interfaceListElement) {
                Element interfaceNameElement = interfaceElement.getChild("name");
                Element interfaceAddressElement = interfaceElement.getChild("ipaddress");
                Element interfaceVlanElement = interfaceElement.getChild("vlan");
                if (interfaceNameElement != null && interfaceAddressElement != null) {
                    String interfaceName = interfaceNameElement.getText();
                    String interfaceAddress = interfaceAddressElement.getText();
                    Interface inface;

                    if (interfaceVlanElement != null && !interfaceVlanElement.getText().isEmpty()) {
                        inface = new Interface(interfaceName, interfaceAddress, this, topology.getNewOrExistingVlan(interfaceVlanElement.getChildText("label")));
                    } else {
                        inface = new Interface(interfaceName, interfaceAddress, this);
                    }

                    this.getInterfaces().put(interfaceName, inface);

                    Element networkElement = interfaceElement.getChild("network");
                    Element maskElement = interfaceElement.getChild("mask");
                    if (networkElement != null && maskElement != null) {
                        inface.setNetwork(new Network(new IPAddress(networkElement.getText()), new IPAddress(maskElement.getText())));
                    }

                    Element directlyConnectedElement = interfaceElement.getChild("directly-connected");
                    if (directlyConnectedElement != null) {
                        Element connectedToInternetElement = directlyConnectedElement.getChild("internet");
                        if (connectedToInternetElement != null) {
                            inface.setConnectedToTheInternet(true);
                        }
                    }
                }
            }
        }

        //Machine services
        Element servicesElement = domElement.getChild("services");
        if (servicesElement != null) {
            List<Element> servicesElementList = servicesElement.getChildren("service");
            for (Element serviceElement : servicesElementList) {
                Element serviceNameElement = serviceElement.getChild("name");
                if (serviceNameElement != null) {
                    String serviceName = serviceNameElement.getText();
                    Service service;
                    if (!this.services.containsKey(serviceName)) {
                        service = new Service(serviceName);
                        this.services.put(serviceName, service);
                        service.setMachine(this);
                    } else {
                        service = this.services.get(serviceName);
                    }


                    Element serviceIPElement = serviceElement.getChild("ipaddress");
                    if (serviceIPElement != null && !serviceIPElement.getText().isEmpty() && service.getIpAddress() == null) {
                        service.setIpAddress(new IPAddress(serviceIPElement.getText()));
                    }

                    Element servicePortElement = serviceElement.getChild("port");
                    if (servicePortElement != null && !servicePortElement.getText().isEmpty() && service.getPortNumber() == 0)
                        service.setPortNumber(Service.portStringToInt(servicePortElement.getText()));

                    Element serviceProtocolElement = serviceElement.getChild("protocol");
                    if (serviceProtocolElement != null && !serviceProtocolElement.getText().isEmpty() && service.getProtocol() == null)
                        service.setProtocol(Protocol.getProtocolFromString(serviceProtocolElement.getText()));

                    Element vulnerabilitiesElement = serviceElement.getChild("vulnerabilities");
                    if (vulnerabilitiesElement != null) {
                        List<Element> vulnsElements = vulnerabilitiesElement.getChildren("vulnerability");
                        for (Element vulnElement : vulnsElements) {
                            Element typeElement = vulnElement.getChild("type");
                            Element goalElement = vulnElement.getChild("goal");
                            Element cveElement = vulnElement.getChild("cve");

                            Vulnerability vuln = new Vulnerability(cveElement.getText());
                            vuln.exploitGoal = goalElement.getText();
                            vuln.exploitType = typeElement.getText();
                            vuln.loadParametersFromDatabase(db.getConn());

                            service.getVulnerabilities().put(vuln.cve, vuln);

                        }
                    }
                }
            }
        }

        //routes
        Element routesElement = domElement.getChild("routes");
        if (routesElement != null)
            this.getRoutingTable().loadFromDomElement(routesElement, this);

    }

    /**
     * @param text the name of the account to find
     * @return the account whose name is text on this host
     */
    private HostAccount getAccountByName(String text) {
        if (this.accounts.get(text) != null)
            return this.accounts.get(text);
        else {
            HostAccount account = new HostAccount(text);
            account.setMachine(this);
            this.accounts.put(text, account);
            return account;
        }
    }

    @Override
    public String toString() {
        if (getFirstIPAddress() != null)
            return getName() + "(" + getFirstIPAddress().getAddress() + ")";
        return getName();
    }

    /**
     * @return the metric
     */
    public double getMetric() {
        double result = 0.;
        for (SecurityRequirement securityRequirement : securityRequirements) {
            result += securityRequirement.getMetric();
        }
        return result;
    }

    /**
     * @param securityRequirement the securityRequirements to add
     */
    public void addSecurityRequirements(SecurityRequirement securityRequirement) {
        this.securityRequirements.add(securityRequirement);
    }

}
