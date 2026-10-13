package com.example.app.domain.service

import kotlinx.coroutines.flow.Flow

interface DocumentParser {
    val supportedExtensions: Set<String>
    fun parse(uriString: String): Flow<String>
}