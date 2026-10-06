/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.db.Database;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit test verifying transactional rollback behavior during loan checkout failures.
 */
class TransactionBugTest {

    /**
     * The DAO handling book persistence operations.
     */
    private BookDao bookDao;

    /**
     * The service handling member operations.
     */
    private MemberService memberService;

    /**
     * Sets up an in-memory database and required DAO and service instances before each test execution.
     *
     * @throws Exception If an error occurs during database setup or initialization.
     */
    @BeforeEach
    void setUp() throws Exception {
        Database db = new Database("jdbc:sqlite::memory:");
        bookDao = new BookDao(db.connectionSource());
        MemberDao memberDao = new MemberDao(db.connectionSource());
        LoanDao loanDao = new LoanDao(db.connectionSource());
        memberService = new MemberService(memberDao, bookDao, loanDao);
    }

    /**
     * Verifies that a failed checkout transaction does not leave partial state modifications
     * in the book's available copy count.
     *
     * @throws Exception If an unexpected error occurs during test execution.
     */
    @Test
    void checkoutLeavesNoPartialStateOnFailure() throws Exception {
        Book book = new Book("Clean Code", "Robert C. Martin", "9780132350884", 2);
        bookDao.create(book);

        assertThrows(Exception.class, () -> memberService.checkout(9999, book.getId()));

        Book reloaded = bookDao.findById(book.getId());
        assertEquals(reloaded.getTotalCopies(), reloaded.getAvailableCopies(),
                "availableCopies was decremented even though the loan was never created");
    }
}