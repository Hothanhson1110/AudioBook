package com.example.app.core.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Wrapper chuẩn cho các tác vụ bất đồng bộ (network, DB, file I/O).
 *
 * Dùng khi cần biểu diễn cả 3 trạng thái Loading / Success / Error
 * trong CÙNG một stream — thường áp dụng cho Repository trả về Flow
 * cho UI (kết hợp với `onStart` + `catch`).
 *
 * Với các UseCase chỉ quan tâm kết quả cuối (one-shot), ưu tiên dùng
 * `kotlin.Result<T>` để giữ code gọn.
 */
sealed interface Resource<out T> {

    data object Loading : Resource<Nothing>

    data class Success<T>(val data: T) : Resource<T>

    data class Error(
        val throwable: Throwable,
        val message: String? = throwable.message,
    ) : Resource<Nothing>
}

/* -------------------------------------------------------------------------- */
/*  Flow helpers                                                              */
/* -------------------------------------------------------------------------- */

/**
 * Bọc một Flow<T> thành Flow<Resource<T>>:
 *  - emit [Resource.Loading] ngay khi bắt đầu collect
 *  - emit [Resource.Success] cho mỗi item từ upstream
 *  - emit [Resource.Error] nếu upstream throw, và **kết thúc flow**
 *
 * @param emitLoading đặt `false` nếu caller không muốn emit Loading ban đầu
 *                    (vd: refresh ngầm sau khi đã có data).
 */
fun <T> Flow<T>.asResource(emitLoading: Boolean = true): Flow<Resource<T>> =
    this
        .map<T, Resource<T>> { Resource.Success(it) }
        .onStart { if (emitLoading) emit(Resource.Loading) }
        .catch { t -> emit(Resource.Error(t)) }

/**
 * Biến đổi một Flow<Resource<T>> thành Flow<T> — ném lại exception
 * khi gặp [Resource.Error]. Hữu ích khi tầng trên muốn xử lý
 * bằng `try/catch` thay vì pattern matching.
 */
fun <T> Flow<Resource<T>>.unwrap(): Flow<T> = this.map { resource ->
    when (resource) {
        is Resource.Loading -> error("Loading state is not a value")
        is Resource.Success -> resource.data
        is Resource.Error -> throw resource.throwable
    }
}

/* -------------------------------------------------------------------------- */
/*  Convenience                                                               */
/* -------------------------------------------------------------------------- */

inline fun <T, R> Resource<T>.map(transform: (T) -> R): Resource<R> = when (this) {
    is Resource.Loading -> Resource.Loading
    is Resource.Success -> Resource.Success(transform(data))
    is Resource.Error -> this
}

inline fun <T> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> {
    if (this is Resource.Success) action(data)
    return this
}

inline fun <T> Resource<T>.onError(action: (Resource.Error) -> Unit): Resource<T> {
    if (this is Resource.Error) action(this)
    return this
}

inline fun <T> Resource<T>.onLoading(action: () -> Unit): Resource<T> {
    if (this is Resource.Loading) action()
    return this
}

val Resource<*>.isLoading: Boolean get() = this is Resource.Loading
val Resource<*>.isSuccess: Boolean get() = this is Resource.Success
val Resource<*>.isError: Boolean get() = this is Resource.Error

fun <T> Resource<T>.getOrNull(): T? = (this as? Resource.Success)?.data

fun <T> Resource<T>.getOrDefault(default: T): T =
    (this as? Resource.Success)?.data ?: default

fun <T> Resource<T>.exceptionOrNull(): Throwable? =
    (this as? Resource.Error)?.throwable