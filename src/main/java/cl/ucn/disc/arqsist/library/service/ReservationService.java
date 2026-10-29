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
import java.time.Clock;
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
     * The DAO for member persistence operation.
     */
    private final MemberDao memberDao;

    /**
     * The DAO for loan persistence operation.
     */
    private final LoanDao loanDao;

    /**
     * The clock for date-time operations.
     */
    private final Clock clock;

    /**
     * The book service.
     */
    private final BookService bookService;

    /**
     * The constructor.
     *
     * @param reservationDao    The DAO to handle reservations.
     * @param bookService       The book service.
     * @param memberDao         The DAO to handle members.
     * @param loanDao           The DAO to handle loans.
     * @param clock             The clock.
     */
    public ReservationService(ReservationDao reservationDao, MemberDao memberDao, LoanDao loanDao, Clock clock, BookService bookService) {
        this.reservationDao = reservationDao;
        this.memberDao = memberDao;
        this.loanDao = loanDao;
        this.clock = clock;
        this.bookService = bookService;
    }

    /**
     * Create a new reservation for a book on behalf of a specific member.
     *
     * @param bookId    The book ID.
     * @param memberId  The member ID.
     * @return The newly created reservation.
     */
    public Reservation reserve(int bookId, int memberId){
        Book book = bookService.findById(bookId);
        if (book == null) {
            throw new NotFoundException("Book not found with ID: " + bookId);
        }
        Member member = memberDao.findById(memberId);
        if (member == null) {
            throw new NotFoundException("Member not found with ID: " + memberId);
        }
        Reservation reservation = new Reservation(member, book, LocalDate.now(clock));
        reservationDao.create(reservation);
        return reservation;
    }

    /**
     * Retrieves all reservations registered in the system.
     *
     * @return A list containing all the reservations.
     */
    public List<Reservation> findAll() {
        return reservationDao.findAll();
    }

    /**
     * Fulfills an active reservation by converting it into a loan
     *
     * @param reservationId The reservation ID.
     * @return  The newly created loan.
     */
    public Loan fulfill(int reservationId) throws SQLException {
        Reservation reservation = reservationDao.findById(reservationId);
        if (reservation == null) {
            throw new NotFoundException("Reservation not found with ID: " + reservationId);
        }
        if (reservation.isFulfilled()){
            throw new IllegalStateException("Reservation with ID " + reservationId + " is already fulfilled");
        }
        reservation.setFulfilled(true);

        LocalDate today = LocalDate.now(clock);
        LocalDate dueDate = LoanPolicy.dueDate(today);
        Loan loan = new Loan(reservation.getMember(), reservation.getBook(), today, dueDate);
        return reservationDao.transaction(() ->{
            reservationDao.update(reservation);
            bookService.borrow(reservation.getBook().getId());
            loanDao.create(loan);
            return loan;
        });
    }
}
