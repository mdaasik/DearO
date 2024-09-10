package com.carworkz.dearo.cardslisting

import com.carworkz.dearo.domain.entities.AMC
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.pdf.Source

interface AMCInteractionProvider
{
    fun initiateJobCard(registrationNumber: String, mobile: String)
    fun startAmcPreview(amc: AMC)
    fun startAMCInvoicePreview(amc: AMC)
}