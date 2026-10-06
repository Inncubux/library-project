/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.dao.ReservationDao;
import cl.ucn.disc.arqsist.library.db.Database;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.model.Reservation;
import cl.ucn.disc.arqsist.library.service.MemberService;
import cl.ucn.disc.arqsist.library.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test verifying that checkout and fulfill loan operations calculate due dates consistently.
 */
class DueDateDuplicationTest {

    /**
     * The service handling member operations.
     */
    private MemberService memberService;

    /**
     * The service handling reservation operations.
     */
    private ReservationService reservationService;

    /**
     * The test book entity.
     */
    private Book book;

    /**
     * The test member entity.
     */
    private Member member;

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
        ReservationDao reservationDao = new ReservationDao(db.connectionSource());

        memberService = new MemberService(memberDao, bookDao, loanDao);
        reservationService = new ReservationService(reservationDao, bookDao, memberDao, loanDao);

        book = new Book("Design Patterns", "Gamma et al.", "9780201633610", 1);
        bookDao.create(book);
        member = new Member("Grace Hopper", "grace@example.com");
        memberDao.create(member);
    }

    /**
     * Verifies that borrowing a book via direct checkout and fulfilling a reservation
     * yield the exact same due date policy.
     *
     * @throws Exception If any service operation fails during test execution.
     */
    @Test
    void checkoutAndFulfillUseTheSameLoanPeriod() throws Exception {
        Loan fromCheckout = memberService.checkout(member.getId(), book.getId());

        Reservation reservation = reservationService.reserve(book.getId(), member.getId());
        Loan fromFulfill = reservationService.fulfill(reservation.getId());

        assertEquals(fromCheckout.getDueDate(), fromFulfill.getDueDate(),
                "the same kind of loan should have the same due date");
    }
}