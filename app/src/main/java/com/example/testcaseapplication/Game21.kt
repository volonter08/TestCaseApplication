package com.example.testcaseapplication

import java.util.*
import kotlin.random.Random

class Game21() {
    val myCards = Stack<Card>()
    val cardsOnTable: MutableList<Card> = mutableListOf<Card>().apply {
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                add(Card(suit, rank))
            }
        }
    }
    val count = 0
    fun takeCard() {
        if (cardsOnTable.isNotEmpty())
            myCards.push(
                cardsOnTable.run {
                    removeAt(Random.nextInt(size - 1))
                }
            )
    }
}