/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.model.Book;
import cl.ucn.disc.arqsist.library.model.Loan;
import cl.ucn.disc.arqsist.library.model.Member;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Service class that manages operations and business logic for member.
 */
public final class MemberService {

    /**
     * The DAO for member persistence operation.
     */
    private final MemberDao memberDao;

    /**
     * The DAO for book persistence operation.
     */
    private final BookDao bookDao;

    /**
     * The DAO for loan persistence operation.
     */
    private final LoanDao loanDao;

    /**
     * The constructor.
     *
     * @param memberDao The DAO to handle members.
     * @param bookDao   The DAO to handle books.
     * @param loanDao   The DAO to handle loans.
     */
    public MemberService(MemberDao memberDao, BookDao bookDao, LoanDao loanDao) {
        this.memberDao = memberDao;
        this.bookDao = bookDao;
        this.loanDao = loanDao;
    }

    /**
     * Register a member.
     *
     * @param member The member.
     * @return The register member.
     * @throws SQLException If an error occurs during database operations.
     */
    public Member register(Member member) throws SQLException {
        memberDao.create(member);
        return member;
    }

    /**
     * Retrieves all members registered in the system.
     *
     * @return A list containing all the members.
     * @throws SQLException If an error occurs during database operations.
     */
    public List<Member> findAll() throws SQLException {
        return memberDao.findAll();
    }

    /**
     * Performs a book checkout for a specific member, decrements the available book copies,
     * calculates the due date, and persists the loan.
     * @param memberId  The member ID.
     * @param bookId    The book ID.
     * @return The newly created loan.
     * @throws SQLException If an error occurs during database operations.
     */
    public Loan checkout(int memberId, int bookId) throws SQLException {
        Member member = memberDao.findById(memberId);
        Book book = bookDao.findById(bookId);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookDao.update(book);

        //Today date:
        LocalDate today = LocalDate.now();

        //Due date:
        LocalDate dueDate = LoanPolicy.computeDueDate(today);

        Loan loan = new Loan(member, book, today, dueDate);
        loanDao.create(loan);
        return loan;
    }
}
