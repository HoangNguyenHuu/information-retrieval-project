package handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import search.SearchEngine;

/**
 * Created by hoangnh on 30/10/2017.
 */
public class SearchPostHandler implements Handler<RoutingContext> {
    Logger logger = LoggerFactory.getLogger(SearchPostHandler.class);

    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response();

        routingContext.request().bodyHandler(buffer -> {
            String body = buffer.toString();
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(body);
            JsonObject queryObj = element.getAsJsonObject();
            String query = queryObj.get("query").getAsString();
            SearchEngine searchGet = new SearchEngine();
            try {
                int page = queryObj.get("page").getAsInt();
                JsonObject object = searchGet.searchInformation(query, page);

                response.putHeader("content-type", "application/json; charset=UTF-8");
                response.putHeader("Access-Control-Allow-Origin", "*");
                response.setStatusCode(200);
                response.end(object.toString());
            } catch (NumberFormatException e) {
                JsonObject object = new JsonObject();
                object.addProperty("Error", "Page number error");
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

        });
    }
}
