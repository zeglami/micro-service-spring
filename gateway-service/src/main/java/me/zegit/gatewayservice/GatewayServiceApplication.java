package me.zegit.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }


    //routage static
    //lb: load balancer
    /*
     * What Is Round-Robin Load Balancing? Round‑robin load balancing is one of
     * the simplest methods for distributing client requests across a group of servers.
     *  Going down the list of servers in the group, the round‑robin load balancer
     * forwards a client request to each server in turn.
     *
     *  //.route(r -> r.path("/products/**").uri("lb://INVENTORY-SERVICE"))
     *
     * */
    @Bean
    RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {

        return builder.routes()
                .route(r -> r.path("/publicCities/**")
                        .filters(f -> f
                                .addRequestHeader("X-RapidAPI-Key", "2f2a449896msh690958a5bde4506p13c17cjsn65cf1e6106ed")
                                .addRequestHeader("X-RapidAPI-Host", "wft-geo-db.p.rapidapi.com")
                                .rewritePath("/publicCities/(?<segment>.*)", "/${segment}")

                        )
                        .uri("https://wft-geo-db.p.rapidapi.com/v1/geo/cities"))


                .route(r -> r.path("/muslim/**")
                        .filters(f -> f
                                .addRequestHeader("X-RapidAPI-Key", "2f2a449896msh690958a5bde4506p13c17cjsn65cf1e6106ed")
                                .addRequestHeader("X-RapidAPI-Host", "muslimsalat.p.rapidapi.com")
                                .rewritePath("/muslim/(?<segment>.*)", "/${segment}")

                        )
                        .uri("https://muslimsalat.p.rapidapi.com"))

                .build();
    }


    @Bean
    DiscoveryClientRouteDefinitionLocator dynamicRoutes(ReactiveDiscoveryClient rdc, DiscoveryLocatorProperties dlp) {
        return new DiscoveryClientRouteDefinitionLocator(rdc, dlp);
    }


    //routage static
    //lb: load balancer
    /*
     * What Is Round-Robin Load Balancing? Round‑robin load balancing is one of
     * the simplest methods for distributing client requests across a group of servers.
     *  Going down the list of servers in the group, the round‑robin load balancer
     * forwards a client request to each server in turn.
     *
     * */
 /*   @Bean
    RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {

        return builder.routes()
                .route(r -> r.path("/customers/**").uri("lb://CUSTOMER-SERVICE"))
                .route(r -> r.path("/products/**").uri("lb://INVENTORY-SERVICE"))
                .build();
    }
    */

}
