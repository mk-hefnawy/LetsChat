package com.example.letschat.server.local.DataStorePreferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStorePreferencesConstants {
    val BOOLEAN_KEY = booleanPreferencesKey("BOOLEAN_KEY")
    val INT_KEY = intPreferencesKey("INT_KEY")
    val STRING_KEY = stringPreferencesKey("STRING_KEY")
    val LONG_KEY = longPreferencesKey("LONG_KEY")

    val THEME_MODE_NIGHT = booleanPreferencesKey("THEME_MODE_NIGHT")
}