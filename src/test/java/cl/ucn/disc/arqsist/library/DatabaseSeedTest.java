/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.ReservationDao;
import cl.ucn.disc.arqsist.library.db.Database;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Reservation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test verifying database seeding behavior and idempotency.
 */
class DatabaseSeedTest {

    /**
     * Verifies that the database seeding mechanism correctly populates initial data
     * and maintains idempotency when executed multiple times.
     *
     * <p>Test scenario:
     * <ul>
     *   <li>Initializes an in-memory database and invokes {@link Database#seedIfEmpty()}.</li>
     *   <li>Asserts that loans and reservations are successfully created and populated.</li>
     *   <li>Validates that at least one open loan is currently marked as overdue relative to the current date.</li>
     *   <li>Verifies state consistency: ensures books associated with open loans correctly reflect
     *       decremented available copies ({@code availableCopies < totalCopies}).</li>
     *   <li>Invokes {@link Database#seedIfEmpty()} a second time to test idempotency, ensuring
     *       that the total count of loans and reservations remains unchanged without duplicating records.</li>
     * </ul>
     * </p>
     */
    @Test
    void testDatabaseSeedAndIdempotency() throws Exception {
        Database db = new Database("jdbc:sqlite::memory:");
        db.seedIfEmpty();

        LoanDao loanDao = new LoanDao(db.connectionSource());
        ReservationDao reservationDao = new ReservationDao(db.connectionSource());
        BookDao bookDao = new BookDao(db.connectionSource());

        List<Loan> initialLoans = loanDao.findAll();
        List<Reservation> initialReservations = reservationDao.findAll();

        assertFalse(initialLoans.isEmpty(), "Loans should exist after seeding");
        assertFalse(initialReservations.isEmpty(), "Reservations should exist after seeding");

        // Verify that at least one loan is overdue
        boolean hasOverdueLoan = initialLoans.stream()
                .anyMatch(loan -> !loan.isReturned() && loan.getDueDate().isBefore(java.time.LocalDate.now()));
        assertTrue(hasOverdueLoan, "At least one loan should be overdue");

        // Verify that every book with an open loan has fewer available copies than total copies
        for (Loan loan : initialLoans) {
            if (!loan.isReturned()) {
                Book book = bookDao.findById(loan.getBook().getId());
                assertTrue(book.getAvailableCopies() < book.getTotalCopies(),
                        "Book with open loan should have fewer available copies");
            }
        }

        // Call seedIfEmpty() again to test idempotency
        db.seedIfEmpty();

        List<Loan> subsequentLoans = loanDao.findAll();
        List<Reservation> subsequentReservations = reservationDao.findAll();

        assertTrue(initialLoans.size() == subsequentLoans.size(),
                "Number of loans should not change after re-seeding");
        assertTrue(initialReservations.size() == subsequentReservations.size(),
                "Number of reservations should not change after re-seeding");
    }
}