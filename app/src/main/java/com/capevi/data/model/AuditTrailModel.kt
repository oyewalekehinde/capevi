package com.capevi.data.model

data class AuditTrail(
    val userId: String?,
    val action: String,
    val description: String,
    val entityName: String,
    val entityId: String,
    val oldValue: String? = null,
    val newValue: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val ipAddress: String? = null,
    val deviceInfo: String? = null,
)
