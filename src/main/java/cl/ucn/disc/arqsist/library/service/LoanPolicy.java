/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.service;

import java.time.LocalDate;

/**
 * Holds the loan policy: the loan period and the free rate.
 */
public final class LoanPolicy {

    /**
     * The loan period in days.
     */
    public static final int DUE_DATE = 21;

    /**
     * The fee for each day after de due date.
     */
    public static final double FEE_PER_DAY = 1.0;

    /**
     * Private constructor for loan policy.
     */
    private  LoanPolicy() {

    }

    /**
     * Compute the due date for loan.
     *
     * @param loanDate The day of the loan.
     * @return The due date.
     */
    public static LocalDate computeDueDate(LocalDate loanDate) {
        return loanDate.plusDays(DUE_DATE);
    };
}
