package com.learning.reactive;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.*;

public class FluxTestWithStepVerifier {

    @Test
    public void fluxOne() {
        Flux<String> flux = Flux.just("helloo");

        StepVerifier.create(flux)
                .assertNext(x -> assertThat(x).isEqualTo("helloo"))
                .verifyComplete();
    }


    @Test
    public void fluxDefaultIfEmptyTest() {
        Flux<String> f1 = Flux.empty();
        f1 = f1.defaultIfEmpty("flux1...");

        Mono<Void> x = f1.log().flatMap(r -> {
            System.out.println("hello");
            return Mono.empty();
        }).then();

        StepVerifier.create(x).expectNextCount(0).verifyComplete();
    }

    @Test
    public void fluxZip() {
        Flux<String> f1 = Flux.just("f1");
        Flux<String> f2 = Flux.just("f2");
        Flux<String> f3 = Flux.just("f3");
        Mono<String> m1 = Mono.just("m1");

        StepVerifier.create(Flux.zip(f1, f2, f3, m1))
                .assertNext(res -> {
                    assertThat(res.getT1()).isEqualTo("f1");
                    assertThat(res.getT2()).isEqualTo("f2");
                    assertThat(res.getT3()).isEqualTo("f3");
                    assertThat(res.getT4()).isEqualTo("m1");
                })
                .verifyComplete();
    }

    @Test
    public void fluxZipSameLength() {
        Flux<String> f1 = Flux.just("f11", "f12");
        Flux<String> f2 = Flux.just("f21", "f22");
        Flux<String> f3 = Flux.just("f31", "f32");

        Flux<String> zipped = Flux.zip(f1, f2, f3).log().map(x -> {
            String result = x.getT1()+","+x.getT2()+","+x.getT3();
            System.out.println("Zip emits "+result);
            return result;
        });

        //Checking count
        StepVerifier.create(zipped)
               .expectNextCount(2)
                .verifyComplete();

        //Checking values
        StepVerifier.create(zipped)
                .expectNext("f11,f21,f31")
                .expectNext("f12,f22,f32")
                .verifyComplete();
    }

    @Test
    public void fluxZipDifferentLength() {
        Flux<String> f1 = Flux.just("f11", "f12");
        Flux<String> f2 = Flux.just("f21", "f22");
        Flux<String> f3 = Flux.just("f31");

        Flux<String> zipped = Flux.zip(f1, f2, f3).log().map(x -> {
            String result = x.getT1()+","+x.getT2()+","+x.getT3();
            System.out.println("Zip emits "+result);
            return result;
        });

        //Checking count --> It will fail if you expect count 2
        StepVerifier.create(zipped)
                .expectNextCount(1)
                .verifyComplete();

        //Checking values
        StepVerifier.create(zipped)
                .expectNext("f11,f21,f31")
                .verifyComplete();
    }


    @Test
    public void fluxZipAndVoid() {
        Flux<Void> f1 = Flux.empty();
        Flux<String> f2 = Flux.just("f2");
        Flux<String> f3 = Flux.just("f3");

        Mono<Void> resultMono = Flux.zip(f1, f2, f3).log().map(r -> {
            System.out.println("hello");
            return Mono.empty();
        }).then();

        StepVerifier.create(resultMono).expectNextCount(0).verifyComplete();
    }

    @Test
    public void fluxMergeTestWithoutChaining() {
        Flux<String> f1 = Flux.just("f1");
        Flux<String> f2 = Flux.just("f2");

        Flux<String> merge = Flux.merge(f1, f2).log();

        StepVerifier.create(merge).expectNextCount(2).verifyComplete();

        StepVerifier.create(merge)
                .expectNext("f1")
                .expectNext("f2")
                .verifyComplete();
    }

    @Test
    public void fluxMergeTestWithVoid() {
        Flux<String> f1 = Flux.just("f1");
        Flux<String> f2 = Flux.just("f2");
        Flux<Void> f3 = Flux.empty();

        Mono<Void> result = Flux.merge(f1, f2, f3).flatMap(x -> {
            System.out.println("hello "+x);
            return Mono.empty();
        }).log().then();

        StepVerifier.create(result).expectNextCount(0).verifyComplete();

        Mono<Void> result1 = Flux.merge(f1, f2, f3).collectList().flatMap(x -> {
            System.out.println("hello collected list"+x);
            return Mono.empty();
        }).log().then();

        StepVerifier.create(result1).expectNextCount(0).verifyComplete();
    }


    @Test
    public void fluxMergeTest() {
        Flux<String> f1 = Flux.just("f1..");
        Flux<String> f2 = Flux.just("f2..");

        Mono<Void> m = Flux.merge(f1, f2).flatMap(x -> {
            System.out.println("hello");
            return Mono.empty();
        }).then();

        StepVerifier.create(m).expectNextCount(0).verifyComplete();
    }


    @Test
    public void fluxMergeCollectedTest() {
        Flux<String> f1 = Flux.just("f1..");
        Flux<String> f2 = Flux.just("f2..");

        Mono<Void> m = Flux.merge(f1, f2).collectList().flatMap(x -> {
            System.out.println("hello");
            return Mono.empty();
        }).then();

        StepVerifier.create(m).expectNextCount(0).verifyComplete();
    }

    @Test
    public void fluxConcatTestWithoutChaining() {
        Flux<String> f1 = Flux.just("f1");
        Flux<String> f2 = Flux.just("f2");

        Flux<String> concat = Flux.concat(f1, f2).log();

        StepVerifier.create(concat).expectNextCount(2).verifyComplete();

        StepVerifier.create(concat)
                .expectNext("f1")
                .expectNext("f2")
                .verifyComplete();
    }

    @Test
    public void fluxConcatTest() {
        Flux<String> f1 = Flux.just("f1");
        Flux<String> f2 = Flux.just("f2");

        Mono<Void> m = Flux.concat(f1, f2).flatMap(x -> {
            System.out.println("hello");
            return Mono.empty();
        }).then();

        StepVerifier.create(m).expectNextCount(0).verifyComplete();
    }

    @Test
    public void fluxConcatTestWithCollect() {
        Flux<String> f1 = Flux.just("f1");
        Flux<String> f2 = Flux.just("f2");

        Mono<Void> m = Flux.concat(f1, f2).collectList().flatMap(x -> {
            System.out.println("hello");
            return Mono.empty();
        }).then();

        StepVerifier.create(m).expectNextCount(0).verifyComplete();
    }
}
