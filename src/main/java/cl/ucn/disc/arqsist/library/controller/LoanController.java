/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.controller;

import cl.ucn.disc.arqsist.library.service.LoanService;
import cl.ucn.disc.arqsist.library.service.MemberService;
import io.javalin.config.JavalinConfig;

import java.util.Objects;

/**
 * Controller for managing loans.
 */
public final class LoanController {

    /**
     * The loan service.
     */
    private final LoanService loanService;

    /**
     * The constructor.
     *
     * @param loanService The loan service.
     */
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    /**
     * Registers the loan-related routes into the Javalin application configuration.
     *
     * Endpoints registered:
     * <ul>
     *   <li>{@code POST /loans} - Processes a book checkout for a member.</li>
     *   <li>{@code GET /loans} - Retrieves all registered loans with accrued fees.</li>
     *   <li>{@code POST /loans/{id}/return} - Processes the return of a borrowed book.</li>
     *   <li>{@code GET /loans/overdue} - Retrieves all currently overdue loans.</li>
     * </ul>
     *
     * @param config The Javalin configuration instance.
     */
    public void register(JavalinConfig config) {
        config.routes.post("/loans", ctx -> {
            int memberId = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("memberId")));
            int bookId = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("bookId")));
            ctx.json(loanService.checkout(memberId, bookId));
        });
        config.routes.get("/loans", ctx -> ctx.json(loanService.findAll()));
        config.routes.post("/loans/{id}/return", ctx -> ctx.json(loanService.returnLoan(Integer.parseInt(ctx.pathParam("id")))));
        config.routes.get("/loans/overdue", ctx -> ctx.json(loanService.overdueLoans()));
    }
}
