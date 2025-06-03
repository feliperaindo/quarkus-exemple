package org.context;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.subscription.Cancellable;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.IllegalProductException;
import jakarta.ws.rs.core.Context;

import java.time.Duration;

@RequestScoped
public class GreetingService {

    @Context
    HttpServerRequest httpServerRequest;

    public MultiMap captureHeadersSync() {
        MultiMap headers = httpServerRequest.headers();
        Log.infof("Request Headers: %s", headers);
        return headers;
    }

    public void captureHeaderAsync() {
        Cancellable data = Uni
                .createFrom()
                .item(httpServerRequest.headers())
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .onItem()
                .delayIt()
                .by(Duration.ofSeconds(5))
                .subscribe()
                .with(
                        item -> {
                            try {
                                Log.infof("Request Headers async sent by parameters: %s", item);
                                Log.infof("Request Headers async get from context: %s", httpServerRequest.headers());
                            } catch (IllegalProductException ignored) {
                                Log.info("Fail to capture Headers.");
                            }
                        },
                        failure -> Log.info("Request failed.")
                );
    }
}
