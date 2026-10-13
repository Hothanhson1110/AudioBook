package com.example.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.app.data.local.entity.AudioBookEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AudioBookDao_Impl implements AudioBookDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AudioBookEntity> __insertionAdapterOfAudioBookEntity;

  private final EntityDeletionOrUpdateAdapter<AudioBookEntity> __deletionAdapterOfAudioBookEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateAudio;

  private final SharedSQLiteStatement __preparedStmtOfUpdatePlayback;

  public AudioBookDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAudioBookEntity = new EntityInsertionAdapter<AudioBookEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `audio_books` (`id`,`title`,`sourceUri`,`audioPath`,`durationMs`,`status`,`createdAt`,`voiceProfileId`,`voiceName`,`pitch`,`speechRate`,`lastPositionMs`,`lastPlayedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AudioBookEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getSourceUri());
        if (entity.getAudioPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getAudioPath());
        }
        statement.bindLong(5, entity.getDurationMs());
        statement.bindString(6, entity.getStatus());
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindString(8, entity.getVoiceProfileId());
        statement.bindString(9, entity.getVoiceName());
        statement.bindDouble(10, entity.getPitch());
        statement.bindDouble(11, entity.getSpeechRate());
        statement.bindLong(12, entity.getLastPositionMs());
        statement.bindLong(13, entity.getLastPlayedAt());
      }
    };
    this.__deletionAdapterOfAudioBookEntity = new EntityDeletionOrUpdateAdapter<AudioBookEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `audio_books` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AudioBookEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateAudio = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE audio_books \n"
                + "        SET audioPath = ?, status = ?, durationMs = ? \n"
                + "        WHERE id = ?\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfUpdatePlayback = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE audio_books \n"
                + "        SET lastPositionMs = ?, lastPlayedAt = ? \n"
                + "        WHERE id = ?\n"
                + "    ";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final AudioBookEntity entity, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAudioBookEntity.insertAndReturnId(entity);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final AudioBookEntity entity, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfAudioBookEntity.handle(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAudio(final long id, final String path, final String status,
      final long duration, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateAudio.acquire();
        int _argIndex = 1;
        if (path == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, path);
        }
        _argIndex = 2;
        _stmt.bindString(_argIndex, status);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, duration);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateAudio.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePlayback(final long id, final long positionMs, final long now,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdatePlayback.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, positionMs);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, now);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdatePlayback.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AudioBookEntity>> observeAll() {
    final String _sql = "SELECT * FROM audio_books ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"audio_books"}, new Callable<List<AudioBookEntity>>() {
      @Override
      @NonNull
      public List<AudioBookEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfSourceUri = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceUri");
          final int _cursorIndexOfAudioPath = CursorUtil.getColumnIndexOrThrow(_cursor, "audioPath");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfVoiceProfileId = CursorUtil.getColumnIndexOrThrow(_cursor, "voiceProfileId");
          final int _cursorIndexOfVoiceName = CursorUtil.getColumnIndexOrThrow(_cursor, "voiceName");
          final int _cursorIndexOfPitch = CursorUtil.getColumnIndexOrThrow(_cursor, "pitch");
          final int _cursorIndexOfSpeechRate = CursorUtil.getColumnIndexOrThrow(_cursor, "speechRate");
          final int _cursorIndexOfLastPositionMs = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPositionMs");
          final int _cursorIndexOfLastPlayedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPlayedAt");
          final List<AudioBookEntity> _result = new ArrayList<AudioBookEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AudioBookEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpSourceUri;
            _tmpSourceUri = _cursor.getString(_cursorIndexOfSourceUri);
            final String _tmpAudioPath;
            if (_cursor.isNull(_cursorIndexOfAudioPath)) {
              _tmpAudioPath = null;
            } else {
              _tmpAudioPath = _cursor.getString(_cursorIndexOfAudioPath);
            }
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpVoiceProfileId;
            _tmpVoiceProfileId = _cursor.getString(_cursorIndexOfVoiceProfileId);
            final String _tmpVoiceName;
            _tmpVoiceName = _cursor.getString(_cursorIndexOfVoiceName);
            final float _tmpPitch;
            _tmpPitch = _cursor.getFloat(_cursorIndexOfPitch);
            final float _tmpSpeechRate;
            _tmpSpeechRate = _cursor.getFloat(_cursorIndexOfSpeechRate);
            final long _tmpLastPositionMs;
            _tmpLastPositionMs = _cursor.getLong(_cursorIndexOfLastPositionMs);
            final long _tmpLastPlayedAt;
            _tmpLastPlayedAt = _cursor.getLong(_cursorIndexOfLastPlayedAt);
            _item = new AudioBookEntity(_tmpId,_tmpTitle,_tmpSourceUri,_tmpAudioPath,_tmpDurationMs,_tmpStatus,_tmpCreatedAt,_tmpVoiceProfileId,_tmpVoiceName,_tmpPitch,_tmpSpeechRate,_tmpLastPositionMs,_tmpLastPlayedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getById(final long id, final Continuation<? super AudioBookEntity> $completion) {
    final String _sql = "SELECT * FROM audio_books WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AudioBookEntity>() {
      @Override
      @Nullable
      public AudioBookEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfSourceUri = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceUri");
          final int _cursorIndexOfAudioPath = CursorUtil.getColumnIndexOrThrow(_cursor, "audioPath");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfVoiceProfileId = CursorUtil.getColumnIndexOrThrow(_cursor, "voiceProfileId");
          final int _cursorIndexOfVoiceName = CursorUtil.getColumnIndexOrThrow(_cursor, "voiceName");
          final int _cursorIndexOfPitch = CursorUtil.getColumnIndexOrThrow(_cursor, "pitch");
          final int _cursorIndexOfSpeechRate = CursorUtil.getColumnIndexOrThrow(_cursor, "speechRate");
          final int _cursorIndexOfLastPositionMs = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPositionMs");
          final int _cursorIndexOfLastPlayedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPlayedAt");
          final AudioBookEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpSourceUri;
            _tmpSourceUri = _cursor.getString(_cursorIndexOfSourceUri);
            final String _tmpAudioPath;
            if (_cursor.isNull(_cursorIndexOfAudioPath)) {
              _tmpAudioPath = null;
            } else {
              _tmpAudioPath = _cursor.getString(_cursorIndexOfAudioPath);
            }
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpVoiceProfileId;
            _tmpVoiceProfileId = _cursor.getString(_cursorIndexOfVoiceProfileId);
            final String _tmpVoiceName;
            _tmpVoiceName = _cursor.getString(_cursorIndexOfVoiceName);
            final float _tmpPitch;
            _tmpPitch = _cursor.getFloat(_cursorIndexOfPitch);
            final float _tmpSpeechRate;
            _tmpSpeechRate = _cursor.getFloat(_cursorIndexOfSpeechRate);
            final long _tmpLastPositionMs;
            _tmpLastPositionMs = _cursor.getLong(_cursorIndexOfLastPositionMs);
            final long _tmpLastPlayedAt;
            _tmpLastPlayedAt = _cursor.getLong(_cursorIndexOfLastPlayedAt);
            _result = new AudioBookEntity(_tmpId,_tmpTitle,_tmpSourceUri,_tmpAudioPath,_tmpDurationMs,_tmpStatus,_tmpCreatedAt,_tmpVoiceProfileId,_tmpVoiceName,_tmpPitch,_tmpSpeechRate,_tmpLastPositionMs,_tmpLastPlayedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<AudioBookEntity> observeById(final long id) {
    final String _sql = "SELECT * FROM audio_books WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"audio_books"}, new Callable<AudioBookEntity>() {
      @Override
      @Nullable
      public AudioBookEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfSourceUri = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceUri");
          final int _cursorIndexOfAudioPath = CursorUtil.getColumnIndexOrThrow(_cursor, "audioPath");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfVoiceProfileId = CursorUtil.getColumnIndexOrThrow(_cursor, "voiceProfileId");
          final int _cursorIndexOfVoiceName = CursorUtil.getColumnIndexOrThrow(_cursor, "voiceName");
          final int _cursorIndexOfPitch = CursorUtil.getColumnIndexOrThrow(_cursor, "pitch");
          final int _cursorIndexOfSpeechRate = CursorUtil.getColumnIndexOrThrow(_cursor, "speechRate");
          final int _cursorIndexOfLastPositionMs = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPositionMs");
          final int _cursorIndexOfLastPlayedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPlayedAt");
          final AudioBookEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpSourceUri;
            _tmpSourceUri = _cursor.getString(_cursorIndexOfSourceUri);
            final String _tmpAudioPath;
            if (_cursor.isNull(_cursorIndexOfAudioPath)) {
              _tmpAudioPath = null;
            } else {
              _tmpAudioPath = _cursor.getString(_cursorIndexOfAudioPath);
            }
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpVoiceProfileId;
            _tmpVoiceProfileId = _cursor.getString(_cursorIndexOfVoiceProfileId);
            final String _tmpVoiceName;
            _tmpVoiceName = _cursor.getString(_cursorIndexOfVoiceName);
            final float _tmpPitch;
            _tmpPitch = _cursor.getFloat(_cursorIndexOfPitch);
            final float _tmpSpeechRate;
            _tmpSpeechRate = _cursor.getFloat(_cursorIndexOfSpeechRate);
            final long _tmpLastPositionMs;
            _tmpLastPositionMs = _cursor.getLong(_cursorIndexOfLastPositionMs);
            final long _tmpLastPlayedAt;
            _tmpLastPlayedAt = _cursor.getLong(_cursorIndexOfLastPlayedAt);
            _result = new AudioBookEntity(_tmpId,_tmpTitle,_tmpSourceUri,_tmpAudioPath,_tmpDurationMs,_tmpStatus,_tmpCreatedAt,_tmpVoiceProfileId,_tmpVoiceName,_tmpPitch,_tmpSpeechRate,_tmpLastPositionMs,_tmpLastPlayedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
