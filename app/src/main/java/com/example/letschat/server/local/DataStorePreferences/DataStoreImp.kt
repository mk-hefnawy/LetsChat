package com.example.letschat.server.local.DataStorePreferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreImp @Inject constructor(val context: Context): IDataStorePreferencesAPI {

    override suspend fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
    context.dataStore.data.catch { exception ->
        if (exception is IOException){
            emit(emptyPreferences())
        }else{
            throw exception
        }
    }.map { preferences ->
        val result = preferences[key]?: defaultValue
        result
    }


    override suspend fun <T> putPreference(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    override suspend fun <T> removePreference(key: Preferences.Key<T>) {
        context.dataStore.edit {
            it.remove(key)
        }
    }

    override suspend fun clearAllPreference() {
        context.dataStore.edit {
            it.clear()
        }
    }
}