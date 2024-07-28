package com.botirovka.mymessenger.chats

import com.botirovka.mymessenger.users.User

data class Chat (val chatId: String,
                 val chatName: String,
                 val firstUserId: String,
                 val secondUserId: String)