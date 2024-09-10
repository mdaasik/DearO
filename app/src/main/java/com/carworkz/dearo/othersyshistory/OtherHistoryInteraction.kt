package com.carworkz.dearo.othersyshistory

import com.carworkz.dearo.domain.entities.History

interface OtherHistoryInteraction
{
    fun showDetails(details: History)
}