package com.example.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList
import kotlin.system.measureTimeMillis
import kotlin.text.Charsets.UTF_8
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView : RecyclerView
    private lateinit var messageBox : EditText
    private lateinit var sendButton : Button
    private lateinit var messageAdapter : MessageAdapter
    private lateinit var messageList : ArrayList<Message>
    private lateinit var mDbRef : DatabaseReference

    var receiverRoom : String? =null
    var senderRoom : String? =null



    //for encryption part
    private var secretKey: SecretKeySpec? = null
    private lateinit var key: ByteArray

    @OptIn(ExperimentalTime::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //for intilising the object of desdemo
        var obj=DESDemo()


        //for encryption initialization
        val secretKey = "secretkey"

       val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")

        val senderUid=FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        supportActionBar?.title = name

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        //logic for adding data to recycler view
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for(postSnapshot in snapshot.children){

                        val message=postSnapshot.getValue(Message :: class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })



        //Adding the message to database
        sendButton.setOnClickListener{

           var message : String? =null
             message = messageBox.text.toString()

            //Encryption using DES
            message = obj.encrypt(message)
            val messageObject = Message(message,senderUid)

            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {

                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)

                }
            messageBox.setText("")
        }

    }

    // set Key
    fun setKey(myKey: String) {
        var sha: MessageDigest? = null
        try {
            key = myKey.toByteArray(charset("UTF-8"))
            sha = MessageDigest.getInstance("SHA-1")
            key = sha.digest(key)
            key = Arrays.copyOf(key, 16)
            secretKey = SecretKeySpec(key, "AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    // method to encrypt the secret text using key
    fun encrypt(strToEncrypt: String, secret: String): String? {
        try {
             setKey(secret)
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            return Base64.getEncoder().encodeToString(cipher.doFinal
                (strToEncrypt.toByteArray(charset("UTF-8"))))
        } catch (e: Exception) {

            println("Error while encrypting: $e")
        }
        return null
    }

    // method to decrypt the secret text using key
    fun decrypt(strToDecrypt: String?, secret: String): String? {
        try {
            setKey(secret)
            val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            return String(cipher.doFinal(Base64.getDecoder().
            decode(strToDecrypt)))
        } catch (e: Exception) {
            println("Error while decrypting: $e")
        }
        return null
    }

}