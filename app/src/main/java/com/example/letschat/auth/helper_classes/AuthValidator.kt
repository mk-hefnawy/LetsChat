package com.example.letschat.auth.helper_classes

import android.util.Patterns
import com.example.letschat.user.User
import java.util.regex.Matcher
import java.util.regex.Pattern

open class AuthValidator() {

    fun isEmailValid(email: String): Pair<Boolean, String>{
        // Empty check
        if (email.isEmpty()) return Pair(false, "Please enter your email")

        // Validation check
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return Pair(false, "Please enter a valid email")

        return Pair(true, "")
    }

    fun isPasswordValid(password: String): Pair<Boolean, String>{
        // Empty check
        if (password.isEmpty()) return Pair(false, "Please enter your password")
        // Validation check
        if (!isValidPassword(password)) return Pair(false,
            "Password must have at least one digit, one upper-case letter, one lower-case letter, and one special character")
        return Pair(true, "")
    }

    private fun isValidPassword(password: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher

        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"

        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)

        return matcher.matches()

    }
    fun isUserNameValid(userName: String): Pair<Boolean, String>{
        // Empty check
        if (userName.isEmpty()) return Pair(false, "Please enter your user name")

        // Validation check
            // check if it is already used :(

            // check if it is the userName of the current user // wil need RemoteValidator class

        return Pair(true, "")
    }


}