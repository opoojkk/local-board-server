package com.joykeepsflowin.biz

import kotlinx.serialization.Serializable

@Serializable
class ResponseShell<T> {
    var code: Int = 0
    var msg: String = "ok"
    var data: T? = null

    @JvmOverloads
    constructor(code: Int = 200, msg: String = "", data: T? = null) {
        this.code = code
        this.msg = msg
        this.data = data
    }

}