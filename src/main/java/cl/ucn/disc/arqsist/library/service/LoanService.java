/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service class that manages operations and business logic for loan.
 */
public final class LoanService {
    /**
     * The DAO used for loan persistence operations.
     */
    private final LoanDao loanDao;

    /**
     * The DAO used for nook persistence operations.
     */
    private final BookDao bookDao;

    /**
     * The constructor.
     * @param loanDao The DAO used to handle loans.
     * @param bookDao The DAO used to handle books.
     */
    public LoanService(LoanDao loanDao, BookDao bookDao) {
        this.loanDao = loanDao;
        this.bookDao = bookDao;
    }

    /**
     * Retrieves all loans from the database.
     *
     * @return A list containing all loans.
     * @throws SQLException If an error occurs during database operations.
     */
    public List<Loan> findAll() throws SQLException {
        return loanDao.findAll();
    }

    /**
     * Processes the return of a borrowed book, marks the loan as returned,
     * calculates any overdue fees, and increment the available copies of the book.
     * @param loanId The loan ID.
     * @return The updated loan.
     * @throws SQLException If an error occurs during database operations.
     */
    public Loan returnLoan(int loanId) throws SQLException {
        Loan loan = loanDao.findById(loanId);
        if (loan == null || loan.isReturned()) {
            return loan;
        }

        loan.setReturned(true);
        loan.setReturnDate(LocalDate.now());

        LocalDate due = loan.getDueDate();
        LocalDate today = LocalDate.now();

        // Calculate overdue fee if the book is returned after de due date.
        if (today.isAfter(due)) {
            long daysOverdue = ChronoUnit.DAYS.between(due, today);
            loan.setOverdueFee(daysOverdue * 1.0);
        }

        loanDao.update(loan);

        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookDao.update(book);

        return loan;
    }

    /**
     * Find all overdue loans.
     *
     * @return A list of all overdue loans.
     */
    public List<Loan> overdueLoans() throws SQLException {
        LocalDate today = LocalDate.now();

        // Filter the loans to find those that are overdue.
        return loanDao.findAll().stream()
                .filter(loan -> !loan.isReturned()) // Only consider loans that have not been returned.
                .filter(loan -> loan.getDueDate().isBefore(today)) // Only consider loans that are overdue.
                .toList();
    }
}
