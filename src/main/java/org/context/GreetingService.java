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
        Log.infof("Cabeçalhos da requisição: %s", headers);
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
                                Log.infof("Cabeçalhos da requisição assíncrona recebidos por parâmetros: %s", item);
                                Log.infof("Cabeçalhos da requisição assíncrona: %s", httpServerRequest.headers());
                            } catch (IllegalProductException ignored) {
                                Log.info("falhou a captura dos cabeçalhos");
                            }
                        },
                        failure -> Log.info("falhou a requisição")
                );
    }
}
