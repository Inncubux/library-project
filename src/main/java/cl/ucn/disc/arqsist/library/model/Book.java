/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * The book entity.
 */
@DatabaseTable(tableName = "books")
public final class Book {

    /**
     * The ID.
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * The title.
     */
    @DatabaseField(canBeNull = false)
    private String title;

    /**
     * The author.
     */
    @DatabaseField(canBeNull = false)
    private String author;

    /**
     * The isbn.
     */
    @DatabaseField(canBeNull = false)
    private String isbn;

    /**
     * The total copies.
     */
    @DatabaseField(canBeNull = false)
    private int totalCopies;

    /**
     * The available copies.
     */
    @DatabaseField(canBeNull = false)
    private int availableCopies;

    /**
     * Empty constructor for ORMLite.
     */
    public Book() {
    }

    /**
     * The constructor.
     *
     * @param title         The title.
     * @param author        The author.
     * @param isbn          The ISBN.
     * @param totalCopies   The total copies.
     */
    public Book(String title, String author, String isbn, int totalCopies) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
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
     * @return The title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title.
     * @param title The new title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The author.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Set the author.
     * @param author The new author.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return The ISBN.
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Set the ISBN.
     * @param isbn The ISBN.
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * @return The total copies.
     */
    public int getTotalCopies() {
        return totalCopies;
    }

    /**
     * Set the total copies.
     * @param totalCopies The new cant of total copies.
     */
    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    /**
     * @return The available copies.
     */
    public int getAvailableCopies() {
        return availableCopies;
    }

    /**
     * Set the available copies.
     * @param availableCopies The new available copies.
     */
    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }
}
