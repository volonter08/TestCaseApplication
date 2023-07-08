package com.example.testcaseapplication

data class Card(
    val suit: Suit,
    val rank: Rank,
    private var location: Pair<Float, Float> = Pair(0f, 0f),
    private var isFrontSide:Boolean = false
) {
    fun move(newLocation:Pair<Float,Float>){
        location = newLocation
    }
    fun flip(){
        isFrontSide = !isFrontSide
    }
}