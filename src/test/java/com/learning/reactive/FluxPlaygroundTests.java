package com.learning.reactive;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

public class FluxPlaygroundTests {

    @Test
    public void fluxTest() {
      Flux.just("one", "two", "three")
              .log()
              .subscribe(System.out::println);
    }
    @Test
    public void fluxConcatWith() {
        Flux.just("one", "two", "three")
                .concatWith(Flux.just("four"))
                .log()
                .subscribe(System.out::println);
    }

    @Test
    public void fluxWithError() {
        Flux.just("one", "two", "three")
                .concatWith(Flux.error(new RuntimeException("Some exception occurred.")))
                .log()
                .subscribe(System.out::println, throwable -> System.out.println(throwable.getMessage()));
    }
    @Test
    public void fluxWithErrorAndcomplete() {
        Flux.just("one", "two", "three")
                .concatWith(Flux.error(new RuntimeException("Some exception occurred.")))
                .log()
                .subscribe(data -> System.out.println(data),
                           throwable -> System.out.println(throwable.getMessage()),
                           () -> System.out.println("Complete consumer called!!"));
    }

    @Test
    public void fluxFromIterable() {
        Flux.fromIterable(List.of(1, 2, 3))
                .log()
                .subscribe(System.out::println);
    }

    @Test
    public void fluxInterval() throws InterruptedException {
        Flux.interval(Duration.ofSeconds(1))
                .log()
                .subscribe(System.out::println);
        Thread.sleep(5000);
    }

    @Test
    public void fluxWithRequest() throws InterruptedException {
        Flux.just(1, 2, 3, 4, 5)
                .log().subscribe(System.out::println, throwable -> {
        }, () -> {
        }, subscription -> {
            subscription.request(2);
        });
    }

    @Test
    public void fluxWithErrorHandling() {
        Flux.just(1, 2, 3, 4, 5)
                .concatWith(Flux.error(new RuntimeException("Some error")))
                .concatWith(Flux.just(6))
                .onErrorReturn(10)
                .log()
                .subscribe(System.out::println);
    }
}
