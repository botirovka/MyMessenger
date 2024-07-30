package com.botirovka.mymessenger.message

data class Message(val id: String,
                   val ownerId: String,
                   val text: String,
                   val date: String)
