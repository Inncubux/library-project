/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.db.Database;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.service.BookService;
import cl.ucn.disc.arqsist.library.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test verifying accrued fee calculations for overdue and active loans.
 */
class AccruedFeeTest {

    /**
     * The loan service
     */
    private LoanService loanService;

    /**
     * The overdue loan;
     */
    private Loan overdueLoan;

    /**
     * The active loan;
     */
    private Loan activeLoan;

    /**
     * Sets up an in-memory database and test fixtures before each test execution.
     *
     * @throws Exception If an error occurs during database setup or data creation.
     */
    @BeforeEach
    void setUp() throws Exception {
        Database db = new Database("jdbc:sqlite::memory:");
        BookDao bookDao = new BookDao(db.connectionSource());
        MemberDao memberDao = new MemberDao(db.connectionSource());
        LoanDao loanDao = new LoanDao(db.connectionSource());

        Clock clock = Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneOffset.UTC);
        BookService bookService = new BookService(bookDao);
        loanService = new LoanService(loanDao, bookService, memberDao, clock);

        Book book1 = new Book("Book One", "Author One", "1111111111111", 5);
        Book book2 = new Book("Book Two", "Author Two", "2222222222222", 5);
        bookDao.create(book1);
        bookDao.create(book2);

        Member member = new Member("Ada Lovelace", "ada@example.com");
        memberDao.create(member);

        // Overdue loan due on 2025-12-22 (Current clock date is 2026-01-15 -> 24 days overdue * 1.0 = 24.0)
        overdueLoan = new Loan(member, book1, LocalDate.of(2025, 12, 15), LocalDate.of(2025, 12, 22));
        loanDao.create(overdueLoan);

        // Active loan due in the future
        activeLoan = new Loan(member, book2, LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 25));
        loanDao.create(activeLoan);
    }

    /**
     * Verifies that {@link LoanService#findAll()} correctly calculates and populates
     * the accrued overdue fees for open overdue loans while leaving active (non-overdue)
     * loans with a zero fee.
     *
     * <p>Test scenario:
     * <ul>
     *   <li>An overdue loan is evaluated, having a due date set 24 days prior to the current fixed clock date,
     *       expecting an accrued fee of 24.0 (24 days * FEE_PER_DAY).</li>
     *   <li>An active loan is evaluated, having a future due date, expecting an accrued fee of 0.0.</li>
     * </ul>
     * </p>
     */
    @Test
    void testAccruedFees() {
        List<Loan> loans = loanService.findAll();

        for (Loan loan : loans) {
            if (loan.getId() == overdueLoan.getId()) {
                assertEquals(24.0, loan.getOverdueFee(), 0.001);
            } else if (loan.getId() == activeLoan.getId()) {
                assertEquals(0.0, loan.getOverdueFee(), 0.001);
            }
        }
    }
}