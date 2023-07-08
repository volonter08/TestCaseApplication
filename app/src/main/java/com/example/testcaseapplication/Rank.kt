package com.example.testcaseapplication

enum class Rank {
    KING, QUEEN, ACE, JACK, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN;

    override fun toString(): String =
        when (this) {
            TWO -> "2"
            THREE->"3"
            FOUR -> "4"
            FIVE->"5"
            SIX -> "6"
            SEVEN->"7"
            EIGHT -> "8"
            NINE->"9"
            TEN -> "10"
            else-> super.toString().toLowerCase()
        }
}