package com.example.letschat.auth.helper_classes

interface Factory<T> {
    fun create(): T
}