package org.sake_hack.feature.auth.data.repository

import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.sake_hack.domain.model.UserRole
import org.sake_hack.feature.auth.data.TokenManager
import org.sake_hack.feature.auth.domain.model.User
import org.sake_hack.feature.auth.domain.repository.AuthRepository

/**
 * AuthRepositoryの実装
 */
class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun loginWithGoogle(): Result<User> = withContext(Dispatchers.IO) {
        runCatching {
            // TODO: Firebase Google ログインの実装
            // 現在はプレースホルダー実装
            // 実際の実装時は、FirebaseAuthでGoogle認証を行い、
            // 取得したcredentialを使用してsignInWithCredentialを呼び出す

            val firebaseUser = firebaseAuth.currentUser ?: error("User is null")

            val idToken = firebaseUser.getIdToken(false)
            tokenManager.saveToken(idToken)

            // TODO: バックエンドAPIからロール取得
            User(
                uid = firebaseUser.uid,
                email = firebaseUser.email,
                displayName = firebaseUser.displayName,
                photoUrl = firebaseUser.photoUrl,
                role = UserRole.User
            )
        }
    }

    override suspend fun loginAsGuest(): Result<User> = withContext(Dispatchers.IO) {
        runCatching {
            val result = firebaseAuth.signInAnonymously()
            val firebaseUser = result.user ?: error("User is null")

            User(
                uid = firebaseUser.uid,
                email = null,
                displayName = "ゲスト",
                photoUrl = null,
                role = UserRole.Guest
            )
        }
    }

    override suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            firebaseAuth.signOut()
            tokenManager.clearToken()
        }
    }

    override suspend fun getCurrentUser(): Result<User?> = withContext(Dispatchers.IO) {
        runCatching {
            firebaseAuth.currentUser?.let { firebaseUser ->
                // TODO: バックエンドAPIからロール取得
                User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email,
                    displayName = firebaseUser.displayName,
                    photoUrl = firebaseUser.photoUrl,
                    role = UserRole.User
                )
            }
        }
    }

    override suspend fun getIdToken(): Result<String?> = withContext(Dispatchers.IO) {
        runCatching {
            tokenManager.getToken()
        }
    }

    override suspend fun isSessionValid(): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            firebaseAuth.currentUser != null && tokenManager.getToken() != null
        }
    }
}
