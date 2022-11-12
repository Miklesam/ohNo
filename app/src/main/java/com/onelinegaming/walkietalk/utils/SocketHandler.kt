package com.onelinegaming.walkietalk.utils

import java.net.Socket

object SocketHandler {
    private var socket: Socket? = null

    @Synchronized
    fun getSocket(): Socket? {
        return socket
    }

    @Synchronized
    fun setSocket(socket: Socket) {
        SocketHandler.socket = socket
    }
}
