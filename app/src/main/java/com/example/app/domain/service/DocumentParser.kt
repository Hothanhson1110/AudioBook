cat > app/src/main/java/com/example/app/domain/service/DocumentParser.kt << 'EOF'
package com.example.app.domain.service

import kotlinx.coroutines.flow.Flow

/**
 * Trích xuất văn bản từ một tài liệu (PDF/Word/...).
 * Trả về Flow<String> theo từng đoạn để hỗ trợ xử lý streaming
 * (dùng cho progress bar hoặc TTS theo chunk).
 */
interface DocumentParser {
    val supportedExtensions: Set<String>
    fun parse(uriString: String): Flow<String>
}
EOF

# Verify
ls -la app/src/main/java/com/example/app/domain/service/
cat app/src/main/java/com/example/app/domain/service/DocumentParser.kt