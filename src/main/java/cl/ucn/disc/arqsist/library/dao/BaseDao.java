/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Generic base DAO providing core CRUD operations and transaction handling.
 *
 * @param <T> The entity type handled by this DAO.
 */
public abstract class BaseDao<T> {

    /**
     * The internal ORMLite DAO instance used to perform database operations.
     */
    protected Dao<T, Integer> dao;

    /**
     * The constructor
     *
     * @param connectionSource  The database connection source.
     * @param clazz             The entity class type.
     * @throws RuntimeException If an error occurs while creating DAO.
     */
    public BaseDao(ConnectionSource connectionSource, Class<T> clazz) {
        try {
            this.dao = DaoManager.createDao(connectionSource, clazz);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves all records of type {@code T} from database.
     *
     * @return A list containing all persistent entities found.
     * @throws RuntimeException If a database access error occurs.
     */
    public List<T> findAll(){
        try {
            return dao.queryForAll();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a single entity by its primary key identifier.
     *
     * @param id The ID of the entity.
     * @return The entity matching the given ID.
     * @throws RuntimeException If a database access error occurs.
     */
    public T findById(Integer id) {
        try {
            return dao.queryForId(id);
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Persists a new entity into the database.
     *
     * @param entity The entity instance to create.
     * @return The number of rows affected.
     * @throws RuntimeException If a database access error occurs.
     */
    public int create(T entity) {
        try{
            return dao.create(entity);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates an existing entity in the database.
     *
     * @param entity The entity instance containing updated values.
     * @return The number of rows affected.
     * @throws RuntimeException If a database access error occurs.
     */
    public int update (T entity) {
        try {
            return dao.update(entity);
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes an entity from the database.
     *
     * @param entity
     * @return The number of rows affected.
     * @throws RuntimeException If a database access error occurs.
     */
    public int delete (T entity) {
        try{
            return dao.delete(entity);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes an operation within a database transaction.
     *
     * @param <R>       The return type of the callable task.
     * @param callable  The transactional block of code to execute.
     * @return The result produced by the callable task.
     * @throws RuntimeException If a database access error occurs.
     * @throws SQLException     If a database access error occurs during the transaction.
     */
    public <R> R transaction(Callable<R> callable) throws SQLException {
        try {
            return TransactionManager.callInTransaction(dao.getConnectionSource(), callable);
        }
        catch (SQLException e) {
            if (e.getCause() instanceof RuntimeException cause) {
                throw cause;
            }
            throw e;
        }

    }
}
