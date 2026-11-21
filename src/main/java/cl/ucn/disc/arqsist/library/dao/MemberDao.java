/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.dao;

import cl.ucn.disc.arqsist.library.model.Member;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.field.SqlType;

import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object (DAO) for managing entities.
 * Inherits standard CRUD and transaction operations.
 */
public final class MemberDao extends BaseDao<Member> {

    /**
     * Constructs a new instance with the specified connection source.
     *
     * @param connectionSource The database connection source used for operations.
     */
    public MemberDao(ConnectionSource connectionSource) {
        super(connectionSource, Member.class);
    }

    /**
     * Find members whose name contains the literal query string (case-insensitive ASCII),
     * ordered by ID ascending.
     *
     * @param query the trimmed search fragment
     * @return matching members
     */
    public List<Member> findByNameContaining(String query) {
        try {
            return dao.queryBuilder()
                    .orderBy("id", true)
                    .where()
                    .raw("instr(lower(name), lower(?)) > 0", new SelectArg("name", query))
                    .query();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search members", e);
        }
    }
}