/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.controller;

import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.service.BookService;
import io.javalin.config.JavalinConfig;

/**
 * Controller for managing books.
 */
public final class BookController {

    /**
     * The book service.
     */
    private final BookService service;

    /**
     * The constructor
     *
     * @param service The book service.
     */
    public BookController(BookService service) {
        this.service = service;
    }

    /**
     * Registers the book-related routes into the Javalin application configuration.
     *
     * Endpoints registered:
     * <ul>
     *   <li>{@code GET /books} - Retrieves a list of all books.</li>
     *   <li>{@code GET /books/{id}} - Retrieves a specific book by its ID.</li>
     *   <li>{@code POST /books} - Creates and persists a new book.</li>
     * </ul>
     *
     * @param config The Javalin configuration instance.
     */
    public void register(JavalinConfig config) {
        config.routes.get("/books", ctx -> ctx.json(service.findAll()));
        config.routes.get("/books/{id}", ctx -> ctx.json(service.findById(Integer.parseInt(ctx.pathParam("id")))));
        config.routes.post("/books", ctx -> ctx.json(service.create(ctx.bodyAsClass(Book.class))));
    }
}
