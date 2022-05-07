package com.example.chatapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList

class MessageAdapter(val context : Context,val messageList : ArrayList<Message>)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1;
    val ITEM_SENT = 2;

    //for encryption part
    private var secretKey: SecretKeySpec? = null
    private lateinit var key: ByteArray

    var obj = DESDemo()




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType == 1){
            //inflate receive
            val view:View = LayoutInflater.from(context).inflate(R.layout.receive,parent,false)
            return ReceiveViewHolder(view)
        }
        else{
            //inflate sent
            val view:View = LayoutInflater.from(context).inflate(R.layout.send,parent,false)
            return SentViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageList[position]

        //for encryption initialization
        val secretKey = "secretkey"
        val charset = Charsets.UTF_8
        if(holder.javaClass == SentViewHolder::class.java){
            //do the stuff for sent view holder

            val viewHolder =holder as SentViewHolder

            holder.sentMessage.text = obj.decryptD(currentMessage.message!!)

        }
        else{
            //do stuff for receive view holder
            val viewHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = obj.decryptD(currentMessage.message!!)
        }

    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]

        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT
        }
        else{
            return ITEM_RECEIVE
        }


    }

    override fun getItemCount(): Int {

        return messageList.size
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
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