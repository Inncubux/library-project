/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.controller;

import cl.ucn.disc.arqsist.library.service.ReservationService;
import io.javalin.config.JavalinConfig;

import java.util.Objects;

public final class ReservationController {

    /**
     * The reservation service.
     */
    private final ReservationService service;

    /**
     * The constructor
     *
     * @param service The reservation service.
     */
    public ReservationController(ReservationService service) {
        this.service = service;
    }

    /**
     * Registers the reservation-related routes into the Javalin application configuration.
     *
     * Endpoints registered:
     * <ul>
     *   <li>{@code POST /reservations} - Creates a new book reservation for a member.</li>
     *   <li>{@code GET /reservations} - Retrieves all system reservations.</li>
     *   <li>{@code POST /reservations/{id}/fulfill} - Converts an active reservation into a loan.</li>
     * </ul>
     *
     * @param config The Javalin configuration instance.
     */
    public void register(JavalinConfig config) {
        config.routes.post("/reservations", ctx -> {
            int memberId = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("memberId")));
            int bookId = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("bookId")));
            ctx.json(service.reserve(bookId, memberId));
        });
        config.routes.get("/reservations", ctx -> ctx.json(service.findAll()));
        config.routes.post("/reservations/{id}/fulfill", ctx -> ctx.json(service.fulfill(Integer.parseInt(ctx.pathParam("id")))));
    }
}
