package com.example.noticias

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class SaveSharedPreference {
    companion object {
        private const val PREF_NOMBRE = "username"
        private const val PREF_ALIAS = "alias"
        private const val PREF_CORREO = "correo"
        private const val PREF_IDUSUARIO = "idUsuario"

        fun getSharedPreferences(ctx: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(ctx)
        }

        fun getIdUsuario(ctx: Context): Int {
            return getSharedPreferences(ctx).getInt(PREF_IDUSUARIO, 0)
        }

        fun setIdUsuario(ctx: Context, idus: Int) {
            val editor = getSharedPreferences(ctx).edit()
            editor.putInt(PREF_IDUSUARIO, idus)
            editor.apply()
        }

        fun setUserName(ctx: Context, userName: String) {
            val editor = getSharedPreferences(ctx).edit()
            editor.putString(PREF_NOMBRE, userName)
            editor.apply()
        }

        fun getUserName(ctx: Context): String {
            return getSharedPreferences(ctx).getString(PREF_NOMBRE, "") ?: ""
        }

        fun setAlias(ctx: Context, alias: String) {
            val editor = getSharedPreferences(ctx).edit()
            editor.putString(PREF_ALIAS, alias)
            editor.apply()
        }

        fun getAlias(ctx: Context): String {
            return getSharedPreferences(ctx).getString(PREF_ALIAS, "") ?: ""
        }

        fun setCorreo(ctx: Context, correo: String) {
            val editor = getSharedPreferences(ctx).edit()
            editor.putString(PREF_CORREO, correo)
            editor.apply()
        }

        fun getCorreo(ctx: Context): String {
            return getSharedPreferences(ctx).getString(PREF_CORREO, "") ?: ""
        }

        fun clearAll(ctx: Context) {
            val editor = getSharedPreferences(ctx).edit()
            editor.clear()
            editor.apply()
        }
    }
}
