package server.rest;

import server.api.AttackPathManagement;
import server.api.MulVALConnection;
import server.attackgraph.AttackGraph;
import server.attackgraph.MulvalAttackGraph;
import server.database.Database;
import server.hostInformation.InformationSystem;
import server.monitoring.Monitoring;
import server.properties.ProjectProperties;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.json.JSONObject;
import org.json.XML;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.attackgraph.AttackPath;


@Path("/json/")
public class RestJsonAPI {

    /**
     * Generates the attack graph and initializes the main objects for other API
     * calls (database, attack graph, attack paths,...)
     *
     * @param request the HTTP request
     * @return the HTTP response
     * @throws Exception
     */
    @GET
    @Path("initialize")
    @Produces(MediaType.APPLICATION_JSON)
    public Response initialise(@Context HttpServletRequest request) throws Exception {
        String databasePath = ProjectProperties.getInstance().getProperty("vulnerability-database");

        //Load the vulnerability and remediation database
        Database database = new Database(databasePath);

        String topologyFilePath = ProjectProperties.getInstance().getProperty("output-topology");

        Logger.getLogger(RestJsonAPI.class.getName()).log(Level.INFO, "Generating topology and mulval inputs " + topologyFilePath);
        MulVALConnection.prepareMulVALInputs();

        Logger.getLogger(RestJsonAPI.class.getName()).log(Level.INFO, "Loading topology " + topologyFilePath);
        InformationSystem informationSystem = MulVALConnection.loadTopologyXMLFile(topologyFilePath, database);

        AttackGraph attackGraph = MulVALConnection.generateAttackGraphWithMulValUsingAlreadyGeneratedMulVALInputFile();
        if (attackGraph == null) {
            return RestApplication.returnErrorMessage(request, "the attack graph is empty");
        }
        Logger.getLogger(RestJsonAPI.class.getName()).log(Level.INFO, "Launch scoring function");
        attackGraph.loadMetricsFromTopology(informationSystem);
        List<AttackPath> attackPaths = AttackPathManagement.scoreAttackPaths(attackGraph, attackGraph.getNumberOfVertices());

        //Delete attack paths that have less than 3 hosts (attacker that pown its own host).
        List<AttackPath> attackPathToKeep = new ArrayList<AttackPath>();
        for (AttackPath attackPath : attackPaths) {
            if (attackPath.vertices.size() > 3) {
                attackPathToKeep.add(attackPath);
            }
        }
        attackPaths = attackPathToKeep;

        Logger.getLogger(RestJsonAPI.class.getName()).log(Level.INFO, attackPaths.size() + " attack paths scored");

        Monitoring monitoring = new Monitoring();
        monitoring.setAttackPathList(attackPaths);
        monitoring.setInformationSystem(informationSystem);
        monitoring.setAttackGraph((MulvalAttackGraph) attackGraph);
        request.getSession(true).setAttribute("database", database);
        request.getSession(true).setAttribute("monitoring", monitoring);

        return RestApplication.returnJsonObject(
                request, new JSONObject().put("status", "AttackGraph/AttackPath Genereted Successfull"));
    }

    /**
     * OPTIONS call necessary for the Access-Control-Allow-Origin of the POST
     *
     * @return the HTTP response
     */
    @OPTIONS
    @Path("/initialize")
    public Response initializeOptions(@Context HttpServletRequest request) {
        return RestApplication.returnJsonObject(request, new JSONObject());
    }

    /**
     * Generates the attack graph and initializes the main objects for other API
     * calls (database, attack graph, attack paths,...). Load the objects from
     * the POST XML file describing the whole network topology
     *
     * @param request the HTTP request
     * @return the HTTP response
     * @throws Exception
     */
    @POST
    @Path("/initialize")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Response initializeFromXMLText(@Context HttpServletRequest request, String xmlString) throws Exception {
        String nussusScanFilePath = ProjectProperties.getInstance().getProperty("nessus-scan-path");
        Logger.getLogger(RestJsonAPI.class.getName()).log(Level.INFO, "Storing topology in " + nussusScanFilePath);
        PrintWriter out = new PrintWriter(nussusScanFilePath);
        out.print(xmlString);
        out.close();
        return RestApplication.returnJsonObject(request, new JSONObject().put("status", "Loaded"));
    }

    /**
     * Get the XML topology
     *
     * @param request the HTTP Request
     * @return the HTTP Response
     */
    @GET
    @Path("/topology")
    @Produces(MediaType.APPLICATION_XML)
    public Response getTopology(@Context HttpServletRequest request) {
        Monitoring monitoring = ((Monitoring) request.getSession(true).getAttribute("monitoring"));

        if (monitoring == null) {
            return Response.ok("Somethings Wrong check Server logs pls").build();
        }
        return Response.ok(new XMLOutputter(Format.getPrettyFormat()).outputString(monitoring.getInformationSystem().toDomXMLElement())).build();
    }

    /**
     * Get the hosts list
     *
     * @param request the HTTP Request
     * @return the HTTP Response
     */
    @GET
    @Path("host/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHostList(@Context HttpServletRequest request) {
        Monitoring monitoring = ((Monitoring) request.getSession(true).getAttribute("monitoring"));

        if (monitoring == null) {
            return RestApplication.returnErrorMessage(request, "Somethings Wrong check Server logs pls");
        }
        return RestApplication.returnJsonObject(request, monitoring.getInformationSystem().getHostsListJson());
    }

    /**
     * Get the attack paths list
     *
     * @param request the HTTP Request
     * @return the HTTP Response
     */
    @GET
    @Path("attack_path/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getList(@Context HttpServletRequest request) {
        Monitoring monitoring = ((Monitoring) request.getSession(true).getAttribute("monitoring"));

        if (monitoring == null) {
            return RestApplication.returnErrorMessage(request, "Somethings Wrong check Server logs pls");
        }

        Element attackPathsXML = AttackPathManagement.getAttackPathsXML(monitoring);
        XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
        return RestApplication.returnJsonObject(request, XML.toJSONObject(output.outputString(attackPathsXML)));

    }

    /**
     * Get the number of attack paths
     *
     * @param request the HTTP Request
     * @return the HTTP Response
     */
    @GET
    @Path("attack_path/number")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNumber(@Context HttpServletRequest request) {
        Monitoring monitoring = ((Monitoring) request.getSession(true).getAttribute("monitoring"));

        if (monitoring == null) {
            return RestApplication.returnErrorMessage(request, "Somethings Wrong check Server logs pls");
        }

        return RestApplication.returnJsonObject(request, new JSONObject().put("number", monitoring.getAttackPathList().size()));
    }

    /**
     * Get one attack path (id starting from 0)
     *
     * @param request the HTTP Request
     * @param id the id of the attack path to get
     * @return the HTTP Response
     */
    @GET
    @Path("attack_path/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAttackPath(@Context HttpServletRequest request, @PathParam("id") int id) {
        Monitoring monitoring = ((Monitoring) request.getSession(true).getAttribute("monitoring"));

        if (monitoring == null) {
            return RestApplication.returnErrorMessage(request, "Somethings Wrong check Server logs pls");
        }

        int numberAttackPaths = monitoring.getAttackPathList().size();

        if (id >= numberAttackPaths) {
            return RestApplication.returnErrorMessage(request, "The attack path " + id + " does not exist. There are only"
                    + numberAttackPaths + " attack paths (0 to "
                    + (numberAttackPaths - 1) + ")");
        }

        Element attackPathXML = AttackPathManagement.getAttackPathXML(monitoring, id);
        XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());

        return RestApplication.returnJsonObject(request, XML.toJSONObject(output.outputString(attackPathXML)));
    }

    /**
     * Get one attack path (id starting from 0) in its topological form
     *
     * @param request the HTTP Request
     * @param id the id of the attack path to get
     * @return the HTTP Response
     */
    @GET
    @Path("attack_path/{id}/topological")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopologicalAttackPath(@Context HttpServletRequest request, @PathParam("id") int id) {
        Monitoring monitoring = ((Monitoring) request.getSession(true).getAttribute("monitoring"));

        if (monitoring == null) {
            return RestApplication.returnErrorMessage(request, "Somethings Wrong check Server logs pls");
        }

        int numberAttackPaths = monitoring.getAttackPathList().size();

        if (id >= numberAttackPaths) {
            return RestApplication.returnErrorMessage(request, "The attack path " + id + " does not exist. There are only"
                    + numberAttackPaths + " attack paths (0 to "
                    + (numberAttackPaths - 1) + ")");
        }

        return RestApplication.returnJsonObject(request, AttackPathManagement.getAttackPathTopologicalJson(monitoring, id));
    }

    /**
     * Get the whole attack graph
     *
     * @param request the HTTP Request
     * @return the HTTP Response
     */
    @GET
    @Path("attack_graph")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAttackGraph(@Context HttpServletRequest request) {
        Monitoring monitoring = ((Monitoring) request.getSession(true).getAttribute("monitoring"));

        if (monitoring == null) {
            return RestApplication.returnErrorMessage(request, "Somethings Wrong check Server logs pls");
        }

        Element attackGraphXML = monitoring.getAttackGraph().toDomElement();
        XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
        return RestApplication.returnJsonObject(request, XML.toJSONObject(output.outputString(attackGraphXML)));
    }

    /**
     * Get the attack graph score
     *
     * @param request the HTTP Request
     * @return the HTTP Response
     */
    @GET
    @Path("attack_graph/score")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAttackGraphScore(@Context HttpServletRequest request) {
        Monitoring monitoring = ((Monitoring) request.getSession(true).getAttribute("monitoring"));

        if (monitoring == null) {
            return RestApplication.returnErrorMessage(request, "Somethings Wrong check Server logs pls");
        }

        return RestApplication.returnJsonObject(request, new JSONObject().put("score", monitoring.getAttackGraph().globalScore));
    }

    /**
     * Get the topological representation of the whole attack graph
     *
     * @param request the HTTP Request
     * @return the HTTP Response
     */
    @GET
    @Path("attack_graph/topological")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopologicalAttackGraph(@Context HttpServletRequest request) {
        Monitoring monitoring = ((Monitoring) request.getSession(true).getAttribute("monitoring"));

        if (monitoring == null) {
            return RestApplication.returnErrorMessage(request, "Somethings Wrong check Server logs pls");
        }

        return RestApplication.returnJsonObject(request, AttackPathManagement.getAttackGraphTopologicalJson(monitoring));
    }
}
