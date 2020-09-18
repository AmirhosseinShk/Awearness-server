package server.rest;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/version")
public class RestAPIVersion {
	/**
	 * The version of the prototype
	 */
	private static final String version = "1.0";
	
	@GET
	@Produces("text/plain")
	public String getVersion() {
		return version;
	}
	
	@GET
	@Path("/detailed")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDetailedVersion(@Context HttpServletRequest request) {
        return RestApplication.returnJsonObject(request, new JSONObject().put("version", version));
    }
}
