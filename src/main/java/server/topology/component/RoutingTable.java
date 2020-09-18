package server.topology.component;

import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

public class RoutingTable implements Cloneable {

    /**
     * The host related to this routing table;
     */
    private Host relatedHost;

    /**
     * The route list
     */
    private ArrayList<Route> routeList = new ArrayList<Route>();

    public RoutingTable(Host host) {
        this.relatedHost = host;
    }

    /**
     * @return the routeList
     */
    public ArrayList<Route> getRouteList() {
        return routeList;
    }

    /**
     * @param routeList the routeList to set
     */
    public void setRouteList(ArrayList<Route> routeList) {
        this.routeList = routeList;
    }

    /**
     * @return true if this routing table has a default route, else false
     */
    public boolean hasDefaultRoute() {
        return this.getRouteList().size() > 0 && this.getRouteList().get(this.getRouteList().size() - 1).getDestination().getAddress().equals("0.0.0.0") && this.getRouteList().get(this.getRouteList().size() - 1).getMask().getAddress().equals("0.0.0.0");
    }

    /**
     * Load the routing table from a DOM element extracted from an XML file
     *
     * @param domElement the routing table DOM root
     * @throws Exception
     */
    public void loadFromDomElement(Element domElement, Host host) throws Exception {
        List<Element> routesElements = domElement.getChildren("route");
        for (Element routeElement : routesElements) {
            Element destinationElement = routeElement.getChild("destination");
            Element maskElement = routeElement.getChild("mask");
            Element gatewayElement = routeElement.getChild("gateway");
            Element interfaceElement = routeElement.getChild("interface");

            if (destinationElement != null && maskElement != null && gatewayElement != null && interfaceElement != null) {
                Route route = new Route(new IPAddress(destinationElement.getText()), new IPAddress(gatewayElement.getText()), new IPAddress(maskElement.getText()), host.getInterfaces().get(interfaceElement.getText()));
                this.getRouteList().add(route);
            }
        }
    }

    @Override
    public RoutingTable clone() throws CloneNotSupportedException {
        RoutingTable copie = (RoutingTable) super.clone();

        copie.setRouteList(new ArrayList<Route>(this.getRouteList()));
        for (int i = 0; i < copie.getRouteList().size(); i++) {
            copie.getRouteList().set(i, copie.getRouteList().get(i).clone());
        }

        return copie;
    }

    @Override
    public String toString() {
        String result = "Routing table :\n";
        for (int i = 0; i < getRouteList().size(); i++) {
            result += "\t -" + getRouteList().get(i);
        }
        return result;
    }

}
