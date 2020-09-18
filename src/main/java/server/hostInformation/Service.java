package server.hostInformation;


import server.topology.component.IPAddress;
import server.vulnerability.Vulnerability;
import org.jdom2.Element;

import java.util.HashMap;
import server.topology.component.Protocol;


/**
 * Class representing a service running on a machine
 *
 */
public class Service implements Cloneable {
    /**
     * The machine on which the service is running
     */
    private InformationSystemHost machine = null;

    /**
     * The name of the service
     */
    private String name = "";

    /**
     * The IP address listening to the service
     */
    private IPAddress ipAddress = null;

    /**
     * The port number concerned by the service
     */
    private int portNumber = 0;

    /**
     * The protocol used by the service
     */
    private Protocol protocol = null;

    /**
     * The user of the service
     */
    private HostAccount account = null;

    /**
     * The version of the service
     */
    private String version = "";

    /**
     * The CPE of the service
     */
    private String CPE = "cpe:/";

    /**
     * The vulnerabilities on this service
     */
    private HashMap<String, Vulnerability> vulnerabilities = new HashMap<String, Vulnerability>();


    /**
     * Create a service from its name
     *
     * @param serviceName the name of the service
     */
    public Service(String serviceName) {
        this.setName(serviceName);
    }

    /**
     * Create a service from its protocol and port number
     *
     * @param protocol the protocol of the service
     * @param port     the port number of the service
     */
    public Service(Protocol protocol, int port) {
        this.setProtocol(protocol);
        this.setPortNumber(port);
    }

    /**
     * @param port a string related to a port (ex: "httPort")
     * @return the port number (ex : 80)
     */
    public static int portStringToInt(String port) {
        try {
            return Integer.parseInt(port);
        } catch (Exception ignored) {

        }
        if (port.equals("httpPort"))
            return 80;

        return 0;
    }

    /**
     * Create the XML DOM element from this service
     *
     * @return the dom element corresponding to this service
     */
    public Element toDomXMLElement() {
        Element root = new Element("service");

        Element nameElement = new Element("name");
        nameElement.setText(this.getName());
        root.addContent(nameElement);

        if (this.getIpAddress() != null) {
            Element ipaddressElement = new Element("ipaddress");
            ipaddressElement.setText(this.getIpAddress().getAddress());
            root.addContent(ipaddressElement);
        }

        if (this.getProtocol() != null) {
            Element protocolElement = new Element("protocol");
            protocolElement.setText(this.getProtocol().toString().toUpperCase());
            root.addContent(protocolElement);
        }

        if (this.getPortNumber() != 0) {
            Element portElement = new Element("port");
            portElement.setText(this.getPortNumber() + "");
            root.addContent(portElement);
        }

        if (this.getAccount() != null) {
            Element userElement = new Element("user");
            userElement.setText(this.getAccount().getName());
            root.addContent(userElement);
        }

        if (this.getCPE() != null) {
            Element cpeElement = new Element("CPE");
            cpeElement.setText(this.getCPE());
            root.addContent(cpeElement);
        }

        if (this.getVulnerabilities().size() > 0) {
            Element vulnerabilitiesElement = new Element("vulnerabilities");
            root.addContent(vulnerabilitiesElement);
            for (String key : getVulnerabilities().keySet()) {
                Vulnerability vuln = getVulnerabilities().get(key);
                if (vuln == null)
                    break;
                Element vulnerabilitiyElement = new Element("vulnerability");

                Element typeElement = new Element("type");
                typeElement.addContent(vuln.exploitType);
                vulnerabilitiyElement.addContent(typeElement);

                Element goalElement = new Element("goal");
                goalElement.addContent(vuln.exploitGoal);
                vulnerabilitiyElement.addContent(goalElement);

                Element cveElement = new Element("cve");
                cveElement.addContent(vuln.cve);
                vulnerabilitiyElement.addContent(cveElement);

                vulnerabilitiesElement.addContent(vulnerabilitiyElement);
            }
        }

        return root;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((getIpAddress() == null) ? 0 : getIpAddress().hashCode());
        result = prime * result + ((getMachine() == null) ? 0 : getMachine().hashCode());
        result = prime * result + getPortNumber();
        result = prime * result
                + ((getProtocol() == null) ? 0 : getProtocol().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
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
        Service other = (Service) obj;
        if (getIpAddress() == null) {
            if (other.getIpAddress() != null)
                return false;
        } else if (!getIpAddress().equals(other.getIpAddress()))
            return false;
        if (getMachine() == null) {
            if (other.getMachine() != null)
                return false;
        } else if (!getMachine().equals(other.getMachine()))
            return false;
        if (getPortNumber() != other.getPortNumber())
            return false;
        if (getProtocol() == null) {
            if (other.getProtocol() != null)
                return false;
        } else if (!getProtocol().equals(other.getProtocol()))
            return false;
        if (getName() == null) {
            if (other.getName() != null)
                return false;
        } else if (!getName().equals(other.getName()))
            return false;
        return true;
    }

    @Override
    public Service clone() throws CloneNotSupportedException {
        Service copie = (Service) super.clone();
        if (this.getIpAddress() != null)
            copie.setIpAddress(this.getIpAddress().clone());
        copie.setVulnerabilities(new HashMap<String, Vulnerability>(this.getVulnerabilities()));
        copie.setMachine(null);
        return copie;
    }

    @Override
    public String toString() {
        return "Service [machine=" + getMachine().getName()
                + ", portNumber=" + getPortNumber()
                + ", protocol=" + getProtocol() + ", service=" + getName()
                + ", vulnerabilities=" + getVulnerabilities() + "]";
    }

    /**
     * Get the machine on which the service is running
     */
    public InformationSystemHost getMachine() {
        return machine;
    }

    /**
     * Set the machine on which the service is running
     *
     * @param machine the new machine
     */
    public void setMachine(InformationSystemHost machine) {
        this.machine = machine;
    }

    /**
     * Get the name of the service
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the service
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the IP address listening to the service
     */
    public IPAddress getIpAddress() {
        return ipAddress;
    }

    /**
     * Set the IP address of the service
     *
     * @param ipAddress the new IP address
     */
    public void setIpAddress(IPAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Get the port number of the service
     */
    public int getPortNumber() {
        return portNumber;
    }

    /**
     * Set the port number  of the service
     *
     * @param portNumber the new port number
     */
    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    /**
     * Get the protocol used by the service
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * Set the protocol of the service
     *
     * @param protocol the new protocol
     */
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    /**
     * Get the user of the service
     */
    public HostAccount getAccount() {
        return account;
    }

    /**
     * Set the user of the service
     *
     * @param account the new account
     */
    public void setAccount(HostAccount account) {
        this.account = account;
    }

    /**
     * Get the version of the service
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set the version of the service
     *
     * @param version the new version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Get the CPE of the service
     */
    public String getCPE() {
        return CPE;
    }

    /**
     * Set the CPE of the service
     *
     * @param CPE the new CPE
     */
    public void setCPE(String CPE) {
        this.CPE = CPE;
    }

    /**
     * Get the vulnerabilities of this service
     */
    public HashMap<String, Vulnerability> getVulnerabilities() {
        return vulnerabilities;
    }

    /**
     * Set the vulnerabilites of this service
     *
     * @param vulnerabilities the new vulnerabilities
     */
    public void setVulnerabilities(HashMap<String, Vulnerability> vulnerabilities) {
        this.vulnerabilities = vulnerabilities;
    }

}
