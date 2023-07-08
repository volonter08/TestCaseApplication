package com.example.testcaseapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class CardsBitmapHandling {
    companion object {
        fun getCardBitmap(context: Context, card:Card,cardWidth:Int,cardHeight:Int):Bitmap{
            return BitmapFactory.decodeStream(context.assets.open("cards/Atlas_deck_${card.rank.toString()}_of_${card.suit.toString()}.svg.png")).run {
                Bitmap.createScaledBitmap(this,cardWidth,cardHeight,true)
            }
        }
    }
}