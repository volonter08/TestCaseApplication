package com.example.testcaseapplication.model

enum class Suit {
    Diamonds, Hearts,Clubs,Spades;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}