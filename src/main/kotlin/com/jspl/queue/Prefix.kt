package com.jspl.queue

enum class Prefix(val prefix: String) {
    WAITING("W"),PROCESS("P"),LOCK("L");
    companion object{
        fun stringWithPrefix(type: Prefix, id: String): String{
            return type.prefix + id
        }
    }
}