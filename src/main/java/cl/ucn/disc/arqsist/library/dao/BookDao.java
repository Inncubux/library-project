/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.model.Book;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Data Access Object (DAO) for managing entities.
 * Inherits standard CRUD and transaction operations.
 */
public final class BookDao extends BaseDao<Book> {

    /**
     * Constructs a new instance with the specified connection source.
     *
     * @param connectionSource The database connection source used for operations.
     */
    public BookDao(ConnectionSource connectionSource) {
        super(connectionSource, Book.class);
    }
}