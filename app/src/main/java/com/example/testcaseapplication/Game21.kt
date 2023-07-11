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
    var count = 0
    var countOpponent = kotlin.random.Random.nextInt(12,22)
    fun takeCard(onFinishGame: (Boolean) -> Unit) {
        if (cardsOnTable.isNotEmpty())
            myCards.push(
                cardsOnTable.run {
                    removeAt(Random.nextInt(size - 1))
                }
            )
        count += when(myCards.peek().rank){
            Rank.JACK -> 2
            Rank.QUEEN-> 3
            Rank.KING -> 4
            Rank.ACE-> 11
            else -> myCards.peek().rank.toString().toInt()
        }
        if(count==21){
            onFinishGame(true)
        }
    }
    fun finishGame( onFinishGame: (Boolean) -> Unit){
         when{
            count>21->{
                when{
                    count>countOpponent-> onFinishGame(false)
                    else-> onFinishGame(true)
                }
            }
            else -> {
                when{
                    count>countOpponent-> onFinishGame(true)
                    else-> onFinishGame(false)
                }
            }
        }
    }
}