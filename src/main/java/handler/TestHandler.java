package handler;

import com.google.gson.JsonObject;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by hoangnh on 30/10/2017.
 */
public class TestHandler implements Handler<RoutingContext>{
    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response();
        String query = request.getParam("query");

        JsonObject object = new JsonObject();
        object.addProperty("query", query);

        response.putHeader("content-type", "application/json; charset=UTF-8");
        response.putHeader("Access-Control-Allow-Origin", "*");
        response.setStatusCode(200);
        response.end(object.toString());

    }
}
