package org.context;

import io.vertx.core.MultiMap;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;

@Path("hello")
public class GreetingResource {

    @Inject
    GreetingService greetingService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public RestResponse<MultiMap> hello() {
        return RestResponse.ok(greetingService.captureHeadersSync());
    }

    @GET()
    @Path("async")
    public RestResponse<Void> hello2() {
        greetingService.captureHeaderAsync();
        return RestResponse.ok();
    }
}
