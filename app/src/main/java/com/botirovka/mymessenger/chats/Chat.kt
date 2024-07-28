package com.botirovka.mymessenger.chats

import com.botirovka.mymessenger.users.User

data class Chat (val firstUserId: String,
                 val secondUserId: String){
   val chatId: String
   init {
       chatId = if(firstUserId>secondUserId) firstUserId+secondUserId else secondUserId+firstUserId
   }
}