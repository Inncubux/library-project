/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.controller;

import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.service.MemberService;
import io.javalin.config.JavalinConfig;


public final class MemberController {

    /**
     * The member service.
     */
    private final MemberService service;

    /**
     * The constructor.
     *
     * @param service The member service.
     */
    public MemberController(MemberService service) {
        this.service = service;
    }

    /**
     * Registers the member-related routes into the Javalin application configuration.
     *
     * Endpoints registered:
     * <ul>
     *   <li>{@code GET /members} - Retrieves all registered members.</li>
     *   <li>{@code POST /members} - Registers a new member in the system.</li>
     * </ul>
     *
     * @param config The Javalin configuration instance.
     */
    public void register(JavalinConfig config) {
        config.routes.get("/members", ctx -> ctx.json(service.findAll()));
        config.routes.post("/members", ctx -> ctx.json(service.register(ctx.bodyAsClass(Member.class))));
    }
}
