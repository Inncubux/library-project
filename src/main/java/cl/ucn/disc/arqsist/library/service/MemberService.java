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
     * The constructor.
     *
     * @param memberDao The DAO to handle members.
     */
    public MemberService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    /**
     * Register a member.
     *
     * @param member The member.
     * @return The register member.
     */
    public Member register(Member member) {
        memberDao.create(member);
        return member;
    }

    /**
     * Retrieves all members registered in the system.
     *
     * @return A list containing all the members.
     */
    public List<Member> findAll() {
        return memberDao.findAll();
    }
}
