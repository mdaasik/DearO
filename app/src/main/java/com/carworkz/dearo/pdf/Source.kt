package com.carworkz.dearo.pdf

/**
* Tags used to track from which card listing screens the user has navigated to the current screen.
 *
 * Used to display PDFs, can be used for other purposes
* */
enum class Source {
    INVOICED, PARTIAL_INVOICED, COMPLETED, PROFORMA, OTC, DEFAULT, PAID, CANCELLED, IN_PROGRESS, CLOSED
}