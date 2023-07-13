package com.example.testcaseapplication.model

enum class Suit :java.io.Serializable{
    Diamonds, Hearts,Clubs,Spades;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}