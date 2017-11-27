package handler;

import com.google.gson.JsonObject;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import search.SearchEngine;

/**
 * Created by hoangnh on 27/11/2017.
 */
public class GetAllHandler implements Handler<RoutingContext> {
    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response();
        try {
            String query = request.getParam("query");

            SearchEngine searchGet = new SearchEngine();

            JsonObject object = searchGet.searchAll(query);

            response.putHeader("content-type", "application/json; charset=UTF-8");
            response.putHeader("Access-Control-Allow-Origin", "*");
            response.setStatusCode(200);
            response.end(object.toString());
        } catch (NumberFormatException e) {
            JsonObject object = new JsonObject();
            object.addProperty("Error", "Page number error, must be an positive integer (>=1)");
            response.putHeader("content-type", "application/json; charset=UTF-8");
            response.putHeader("Access-Control-Allow-Origin", "*");
            response.setStatusCode(200);
            response.end(object.toString());
            e.printStackTrace();
        } catch (Exception e) {
            JsonObject object = new JsonObject();
            object.addProperty("Error", "Server error");
            response.putHeader("content-type", "application/json; charset=UTF-8");
            response.putHeader("Access-Control-Allow-Origin", "*");
            response.setStatusCode(200);
            response.end(object.toString());
            e.printStackTrace();
        }
    }
}
