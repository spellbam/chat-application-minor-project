package com.example.chatapplication
import java.lang.Byte.decode
import java.lang.Integer.decode
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.crypto.SecretKey
import java.security.MessageDigest
import java.util.*

class DESDemo {
    @Throws(Exception::class)
    fun encrypt(message: String): String {
        val md = MessageDigest.getInstance("md5")
        val digestOfPassword = md.digest(
            "HG58YZ3CR9"
                .toByteArray(charset("utf-8"))
        )
        val keyBytes = Arrays.copyOf(digestOfPassword, 24)
        var j = 0
        var k = 16
        while (j < 8) {
            keyBytes[k++] = keyBytes[j++]
        }
        val key: SecretKey = SecretKeySpec(keyBytes, "DESede")
        val iv = IvParameterSpec(ByteArray(8))
        val cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
       // val plainTextBytes = message.toByteArray(charset("utf-8"))
        // final String encodedCipherText = new sun.misc.BASE64Encoder()
        // .encode(cipherText);
        return  Base64.getEncoder().encodeToString(cipher.doFinal(message?.toByteArray(charset("utf-8"))))
    }

    @Throws(Exception::class)
    fun decryptD(message: String): String ?{

       // val strtodecrypt: ByteArray = message.toByteArray(Charsets.UTF_8)




        val md = MessageDigest.getInstance("md5")
        val digestOfPassword = md.digest(
            "HG58YZ3CR9"
                .toByteArray(charset("utf-8"))
        )
        val keyBytes = Arrays.copyOf(digestOfPassword, 24)
        var j = 0
        var k = 16
        while (j < 8) {
            keyBytes[k++] = keyBytes[j++]
        }
        val key: SecretKey = SecretKeySpec(keyBytes, "DESede")
        val iv = IvParameterSpec(ByteArray(8))

        try {
            val decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding")
            decipher.init(Cipher.DECRYPT_MODE, key, iv)

//        final byte[] encData = new sun.misc.BASE64Decoder().decodeBuffer(message);

//        val decodedBytes: ByteArray = Base64.getDecoder().decode(encodedString)
//        val decodedString = String(decodedBytes)




            return String(decipher.doFinal(Base64.getDecoder().
            decode(message)))
        }
        catch (e: Exception) {
            println("Error while decrypting: $e")
        }
        return null

    }

    /*companion object {
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val text = "knowledgefactory.net"
            val codedtext = DESDemo().encrypt(text)
            val decodedtext = DESDemo().decrypt(codedtext)
            println(codedtext)
            println(decodedtext)
            // This correctly shows "knowledgefactory.net"
        }
    }
     */
}