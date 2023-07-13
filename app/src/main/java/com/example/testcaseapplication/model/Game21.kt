package com.example.testcaseapplication.model

import com.example.testcaseapplication.Card
import java.util.*
import kotlin.random.Random

class Game21():java.io.Serializable{
    val myCards = Stack<Card>()
    val cardsOnTable: MutableList<Card> = mutableListOf<Card>().apply {
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                add(Card(suit, rank))
            }
        }
    }
    var count = 0
    var countOpponent = kotlin.random.Random.nextInt(12, 26)
    fun takeCard() {
        if (cardsOnTable.isNotEmpty())
            myCards.push(
                cardsOnTable.run {
                    removeAt(Random.nextInt(size - 1))
                }
            )
        count += when (myCards.peek().rank) {
            Rank.JACK -> 2
            Rank.QUEEN -> 3
            Rank.KING -> 4
            Rank.ACE -> 11
            else -> myCards.peek().rank.toString().toInt()
        }
    }

    fun finishGame(onFinishGame: (Boolean) -> Unit) {
        when {
            count > 21 -> {
                when {
                    countOpponent > 21 -> {
                        when {
                            count > countOpponent -> onFinishGame(false)
                            else -> onFinishGame(true)
                        }
                    }
                    else -> onFinishGame(false)
                }
            }
            else -> {
                when {
                    countOpponent > 21 -> onFinishGame(true)
                    else -> when {
                        count >= countOpponent -> onFinishGame(true)
                        else -> onFinishGame(false)
                    }
                }
            }
        }
    }
}