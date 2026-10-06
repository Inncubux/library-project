/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * The member entity.
 */
@DatabaseTable(tableName = "members")
public final class Member {

    /**
     * The ID.
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * The name.
     */
    @DatabaseField(canBeNull = false)
    private String name;

    /**
     * The email.
     */
    @DatabaseField(canBeNull = false)
    private String email;

    /**
     * The empty constructor for ORMLite.
     */
    public Member() {
    }

    /**
     * The constructor.
     *
     * @param name The name.
     * @param email The email.
     */
    public Member(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * @return The ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Set the ID.
     *
     * @param id The new ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     * @param name The new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email.
     * @param email The new email.
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
