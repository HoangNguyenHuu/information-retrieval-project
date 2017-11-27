package service;

import config.Configuration;
import handler.GetAllHandler;
import handler.SearchGetHandler;
import handler.SearchPostHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hoangnh on 30/10/2017.
 */
public class SearchService extends AbstractVerticle{
    static int port = 9099;
    @Override
    public void start(){
        Router router = Router.router(vertx);
        router.get("/search").handler(new SearchGetHandler());
        router.get("/search-all").handler(new GetAllHandler());
        router.post("/search-post").handler(new SearchPostHandler());
        vertx.createHttpServer();
        vertx.createHttpServer().requestHandler(router::accept).listen(port);
    }

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        if(configuration.getProperty("port") != null){
            port = Integer.parseInt(configuration.getProperty("port"));
        }
        Logger logger = LoggerFactory.getLogger(SearchService.class);
        logger.info("Start Vertx Server");
        System.out.println(port);
        Vertx vertx = Vertx.vertx();
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setWorker(true);
        vertx.deployVerticle(new SearchService(), deploymentOptions);
    }

}
