package com.moban.util

/**
 * Created by H. Len Vo on 9/23/18.
 */
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


class Decrypter  {
    companion object {
        private val SALT = "6HDiXzuSelt8G+zg0p4Bc9gXPWlXDZmqFp1LQcKe".toByteArray(charset("UTF8"))
        private const val ITERATION_COUNT = 1000
        private const val KEY_LENGTH = 160
        private const val PBKDF2_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1"


        fun getTransactionId(password: String): ByteArray {
            val keySpec = PBEKeySpec(password.toCharArray(), SALT, ITERATION_COUNT, KEY_LENGTH)
            val keyFactory = SecretKeyFactory.getInstance(PBKDF2_DERIVATION_ALGORITHM)
            val secretKey = keyFactory.generateSecret(keySpec)
            return secretKey.encoded
        }
    }
}
