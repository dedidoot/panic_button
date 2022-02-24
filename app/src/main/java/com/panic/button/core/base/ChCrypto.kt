package com.panic.button.core.base

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object ChCrypto {
    fun aesEncrypt(v: String, secretKey: String) = encrypt(v, secretKey)
    fun aesDecrypt(v: String, secretKey: String) = decrypt(v, secretKey)

    private fun cipher(opmode: Int, secretKey: String): Cipher {
        if (secretKey.length != 32) throw RuntimeException("SecretKey length is not 32 chars")
        val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val sk = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES")
        val iv = IvParameterSpec(secretKey.substring(0, 16).toByteArray(Charsets.UTF_8))
        c.init(opmode, sk, iv)
        return c
    }

    private fun encrypt(str: String, secretKey: String): String {
        val encrypted =
            cipher(Cipher.ENCRYPT_MODE, secretKey).doFinal(str.toByteArray(Charsets.UTF_8))
        val byteStr = android.util.Base64.encode(encrypted, android.util.Base64.NO_WRAP)
        return String(byteStr)
    }

    private fun decrypt(str: String, secretKey: String): String {
        val byteStr = android.util.Base64.decode(str, android.util.Base64.NO_WRAP)
        return String(cipher(Cipher.DECRYPT_MODE, secretKey).doFinal(byteStr))
    }
}