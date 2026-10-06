/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.dao.ReservationDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.model.Reservation;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Service class that manages operations and business logic for reservation.
 */
public final class ReservationService {

    /**
     * The DAO for reservation persistence operation.
     */
    private final ReservationDao reservationDao;

    /**
     * The DAO for book persistence operation.
     */
    private final BookDao bookDao;

    /**
     * The DAO for member persistence operation.
     */
    private final MemberDao memberDao;

    /**
     * The DAO for loan persistence operation.
     */
    private final LoanDao loanDao;

    /**
     * The constructor.
     *
     * @param reservationDao    The DAO to handle reservations.
     * @param bookDao           The DAO to handle books.
     * @param memberDao         The DAO to handle members.
     * @param loanDao           The DAO to handle loans.
     */
    public ReservationService(ReservationDao reservationDao, BookDao bookDao, MemberDao memberDao, LoanDao loanDao) {
        this.reservationDao = reservationDao;
        this.bookDao = bookDao;
        this.memberDao = memberDao;
        this.loanDao = loanDao;
    }

    /**
     * Create a new reservation for a book on behalf of a specific member.
     *
     * @param bookId    The book ID.
     * @param memberId  The member ID.
     * @return The newly created reservation.
     * @throws SQLException If an error occurs during database operations.
     */
    public Reservation reserve(int bookId, int memberId) throws SQLException {
        Book book = bookDao.findById(bookId);
        Member member = memberDao.findById(memberId);
        Reservation reservation = new Reservation(member, book, LocalDate.now());
        reservationDao.create(reservation);
        return reservation;
    }

    /**
     * Retrieves all reservations registered in the system.
     *
     * @return A list containing all the reservations.
     * @throws SQLException If an error occurs during database operations.
     */
    public List<Reservation> findAll() throws SQLException {
        return reservationDao.findAll();
    }

    /**
     * Fulfills an active reservation by converting it into a loan
     *
     * @param reservationId The reservation ID.
     * @return  The newly created loan.
     * @throws SQLException If an error occurs during database operations.
     */
    public Loan fulfill(int reservationId) throws SQLException {
        Reservation reservation = reservationDao.findById(reservationId);
        if (reservation == null || reservation.isFulfilled()) {
            throw new IllegalStateException("Reservation not available");
        }

        reservation.setFulfilled(true);
        reservationDao.update(reservation);

        LocalDate dueDate = LoanPolicy.computeDueDate(LocalDate.now());
        Loan loan = new Loan(reservation.getMember(), reservation.getBook(), LocalDate.now(), dueDate);
        loanDao.create(loan);
        return loan;
    }
}
