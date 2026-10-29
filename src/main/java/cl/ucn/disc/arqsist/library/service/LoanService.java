/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;

import java.sql.SQLException;
import java.time.Clock;
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
     * The book service.
     */
    private final BookService bookService;

    /**
     * The DAO used for member persistence operations.
     */
    private final MemberDao memberDao;

    /**
     * The clock for date-time operations.
     */
    private final Clock clock;

    /**
     * The constructor.
     * @param loanDao      The DAO used to handle loans.
     * @param memberDao    The DAO used to handle members.
     * @param bookService  The book service.
     * @param clock        The clock.
     */
    public LoanService(LoanDao loanDao, BookService bookService, MemberDao memberDao, Clock clock) {
        this.loanDao = loanDao;
        this.bookService = bookService;
        this.memberDao = memberDao;
        this.clock = clock;
    }

    /**
     * Retrieves all loans from the database.
     *
     * @return A list containing all loans.
     */
    public List<Loan> findAll() {
        return withAccruedFees(LoanDao.findAll());
    }

    /**
     * Performs a book checkout for a specific member and persists the loan.
     *
     * @param memberId The member ID.
     * @param bookId   The book ID.
     * @return The newly created loan.
     */
    public Loan checkout(int memberId, int bookId) throws SQLException {
        Member member = memberDao.findById(memberId);
        if (member == null) {
            throw new NotFoundException("Member not found with ID: " + memberId);
        }

        Book book = bookService.findById(bookId);
        if (book == null) {
            throw new NotFoundException("Book not found with ID: " + bookId);
        }

        LocalDate today = LocalDate.now(clock);
        LocalDate dueDate = LoanPolicy.dueDate(today);

        Loan loan = new Loan(member, book, today, dueDate);

        // Runs loan update and return copy in one transaction
        return loanDao.transaction(() -> {
            bookService.borrow(bookId);
            loanDao.create(loan);
            return loan;
        });
    }

    /**
     * Processes the return of a borrowed book, marks the loan as returned,
     * calculates any overdue fees, and increment the available copies of the book.
     * @param loanId The loan ID.
     * @return The updated loan.
     */
    public Loan returnLoan(int loanId) throws SQLException {
        Loan loan = loanDao.findById(loanId);
        if (loan == null) {
            throw new NotFoundException("Loan not found with ID: " + loanId);
        }
        if (loan.isReturned()){
            throw new IllegalStateException("Loan with ID " + loanId + " is already returned");
        }

        LocalDate today = LocalDate.now();
        LocalDate due = loan.getDueDate();

        loan.setReturned(true);
        loan.setReturnDate(LocalDate.now());

        // Set the fee if the book is returned after de due date.
        if (today.isAfter(due)) {
            loan.setOverdueFee(accruedFee(loan, today));
        }

        return loanDao.transaction(() -> {
            loanDao.update(loan);
            bookService.returnCopy(loan.getBook().getId());
            return loan;
        });
    }

    /**
     * Find all overdue loans.
     *
     * @return A list of all overdue loans.
     */
    public List<Loan> overdueLoans() {
        LocalDate today = LocalDate.now();

        // Filter the loans to find those that are overdue.
        return loanDao.findAll().stream()
                .filter(loan -> !loan.isReturned()) // Only consider loans that have not been returned.
                .filter(loan -> loan.getDueDate().isBefore(today)) // Only consider loans that are overdue.
                .toList();
    }

    /**
     * Sets the fee on every open overdue loan without persisting the fee.
     *
     * @param loans The list of loans.
     * @return      The list of loans with accrued fees updated.
     */
    public List<Loan> withAccruedFees(List<Loan> loans) {
        LocalDate today = LocalDate.now(clock);
        for (Loan loan : loans) {
            if (!loan.isReturned() && loan.getDueDate().isBefore(today)) {
                loan.setOverdueFee(accruedFee(loan, today));
            }
        }
        return loans;
    }

    /**
     * Calculates the accrued fee for a specific loan based on days overdue.
     *
     * @param loan  The loan
     * @param today Current date.
     * @return      The calculated fee amount.
     */
    private double accruedFee(Loan loan, LocalDate today) {
        long daysBetween = ChronoUnit.DAYS.between(loan.getDueDate(), today);
        return daysBetween * LoanPolicy.FEE_PER_DAY;
    }
}
