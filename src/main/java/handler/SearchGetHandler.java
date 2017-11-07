package handler;

import com.google.gson.JsonObject;
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
public class SearchGetHandler implements Handler<RoutingContext> {
    Logger logger = LoggerFactory.getLogger(SearchGetHandler.class);

    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response();
        try {
            String query = request.getParam("query");
            int page = Integer.parseInt(request.getParam("page").trim());

            logger.info("query: " + query);

            SearchEngine searchGet = new SearchEngine();

            JsonObject object = searchGet.searchInformation(query, page);

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
