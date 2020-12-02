package server.rest;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Response;

@ApplicationPath("/rest")
public class RestApplication extends ResourceConfig {

    /**
     * Register the package of the rest application
     */
    public RestApplication() {
        packages("server.rest");
        packages("org.glassfish.jersey.examples.multipart");
        register(MultiPartFeature.class);
    }

    /**
     * Returns the {@link javax.ws.rs.core.Response} object from a
     * {@link org.json.JSONObject}
     *
     * @param jsonObject the jsonObject to return
     * @return the relative {@link javax.ws.rs.core.Response} object
     */
    public static Response returnJsonObject(HttpServletRequest request, JSONObject jsonObject) {
        // client's origin
        String clientOrigin = request.getHeader("origin");
        return Response.ok(jsonObject.toString())
                .header("Access-Control-Allow-Origin", clientOrigin)
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Max-Age", "1209600")
                .build();
    }

    /**
     * Returns an error message, in a
     * {@link org.json.JSONObject} ({error:"the error message"}
     *
     * @param errorMessage the error message to return
     * @return the {@link javax.ws.rs.core.Response} to this
     * {@link org.json.JSONObject}
     */
    public static Response returnErrorMessage(HttpServletRequest request, String errorMessage) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error", errorMessage);

        return returnJsonObject(request, jsonObject);
    }

    /**
     * Returns a success message, in a
     * {@link org.json.JSONObject} ({success:"the success message"}
     *
     * @param successMessage the sucess message to return
     * @return the {@link javax.ws.rs.core.Response} to this
     * {@link org.json.JSONObject}
     */
    public static Response returnSuccessMessage(HttpServletRequest request, String successMessage) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", successMessage);

        return returnJsonObject(request, jsonObject);
    }
}
