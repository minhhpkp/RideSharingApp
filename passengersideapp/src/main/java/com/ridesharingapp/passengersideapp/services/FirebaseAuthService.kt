package com.ridesharingapp.passengersideapp.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.ridesharingapp.passengersideapp.ServiceResult
import com.ridesharingapp.passengersideapp.domain.AppUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseAuthService(
    val auth: FirebaseAuth
) : AuthenticationService {
    override suspend fun signUp(
        email: String,
        password: String
    ): ServiceResult<SignUpResult> = withContext(Dispatchers.IO) {
        try {
            val authAttempt = auth.createUserWithEmailAndPassword(email, password).await()
            if (authAttempt.user != null) ServiceResult.Value(
                SignUpResult.Success(authAttempt.user!!.uid)
            )
            else ServiceResult.Failure(Exception("Null user"))
        } catch (exception: Exception) {
            when (exception) {
                is FirebaseAuthWeakPasswordException -> ServiceResult.Value(SignUpResult.InvalidCredentials)
                is FirebaseAuthInvalidCredentialsException -> ServiceResult.Value(SignUpResult.InvalidCredentials)
                is FirebaseAuthUserCollisionException -> ServiceResult.Value(SignUpResult.AlreadySignedUp)
                else -> ServiceResult.Failure(exception)
            }
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): ServiceResult<LogInResult> = withContext(Dispatchers.IO) {
        try {
            val authAttempt = auth.signInWithEmailAndPassword(email, password).await()
            if (authAttempt.user != null) ServiceResult.Value(
                LogInResult.Success(
                    AppUser(
                        userId = authAttempt.user!!.uid
                    )
                )
            )
            else ServiceResult.Failure(Exception("Null user"))
        } catch (exception: Exception) {
            when (exception) {
                is FirebaseAuthInvalidUserException -> ServiceResult.Value(LogInResult.InvalidCredentials)
                is FirebaseAuthInvalidCredentialsException -> ServiceResult.Value(LogInResult.InvalidCredentials)
                else -> ServiceResult.Failure(exception)
            }
        }
    }

    override suspend fun logout(): ServiceResult<Unit> {
        auth.signOut()
        return ServiceResult.Value(Unit)
    }

    override suspend fun getSession(): ServiceResult<AppUser?> {
//        logout()
//        return ServiceResult.Value(null)
        val firebaseUser = auth.currentUser
        return if (firebaseUser == null) ServiceResult.Value(null)
        else ServiceResult.Value(
            AppUser(
                userId = firebaseUser.uid
            )
        )
    }
}