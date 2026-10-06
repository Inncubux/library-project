/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.model.Book;

import java.util.List;

/**
 * Service class that manages operations and business logic for book.
 */
public final class BookService {

    private final BookDao dao;

    /**
     * Constructs a new BookService.
     *
     * @param dao The DAO used for book persistence operations.
     */
    public BookService(BookDao dao) {
        this.dao = dao;
    }

    /**
     * Retrieves all books from the database.
     *
     * @return A list of all existing books.
     */
    public List<Book> listAll() {
        return dao.findAll();
    }

    /**
     * Finds a book by its identifier.
     *
     * @param id The book ID.
     * @return The book if found.
     * @throws NotFoundException If no book exists with the given ID.
     */
    public Book findById(int id) throws NotFoundException {
        Book book = dao.findById(id);
        if (book == null) {
            throw new NotFoundException("Book not found: " + id);
        }
        return book;
    }

    /**
     * Registers a new book in the database, setting its initial available copies
     * to match the total copies.
     *
     * @param book The book entity to create.
     * @return The newly created book.
     */
    public Book create(Book book) {
        book.setAvailableCopies(book.getTotalCopies());
        dao.create(book);
        return book;
    }

    /**
     * Borrows a copy of a book by decrementing its available copy count.
     *
     * @param bookId The ID of the book to borrow.
     * @throws NotFoundException     If the book is not found.
     * @throws IllegalStateException If there are no available copies left to borrow.
     */
    public void borrow(int bookId) throws NotFoundException {
        Book book = dao.findById(bookId);
        if (book == null) {
            throw new NotFoundException("Book not found: " + bookId);
        }
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No available copies of book " + bookId);
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        dao.update(book);
    }

    /**
     * Returns a copy of a book by incrementing its available copy count.
     *
     * @param bookId The ID of the book to return.
     * @throws NotFoundException If the book is not found.
     */
    public void returnCopy(int bookId) throws NotFoundException {
        Book book = dao.findById(bookId);
        if (book == null) {
            throw new NotFoundException("Book not found: " + bookId);
        }

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        dao.update(book);
    }
}