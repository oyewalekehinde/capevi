package com.capevi.data.model

import java.time.LocalDateTime

data class CaseModel(
    var id: String? = null,
    val title: String,
    val description: String,
    val evidenceType: String,
    val evidenceList: List<String>,
    val loggedAt: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val officerName: String,
    val officerId: String,
    val status: String? = null,
)
