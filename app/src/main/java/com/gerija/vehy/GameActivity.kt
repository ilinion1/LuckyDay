package com.gerija.vehy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gerija.vehy.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    lateinit var binding: ActivityGameBinding
    private val imageList = arrayListOf(
        R.drawable.chaynik, R.drawable.fen, R.drawable.noutbuk, R.drawable.powerbank,
        R.drawable.pristavka, R.drawable.proig1, R.drawable.proig2, R.drawable.proig3,
        R.drawable.proig4, R.drawable.pulesos, R.drawable.samokat, R.drawable.telefon,
        R.drawable.utug, R.drawable.proig5, R.drawable.proig6, R.drawable.proig7,
        R.drawable.proig8,
    )
    private val winList = listOf(
        R.drawable.chaynik, R.drawable.fen, R.drawable.noutbuk,
        R.drawable.powerbank, R.drawable.pristavka, R.drawable.samokat, R.drawable.telefon,
        R.drawable.utug, R.drawable.pulesos
    )

    var count = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        randomImage()
    }



    private fun randomImage() {
        binding.bPlay.setOnClickListener {
            val imageSize = imageList.size - 1
            val random = (0..imageSize).random()
            binding.imMain.setImageResource(imageList[random])
            countWin(imageList[random])
        }
    }

    private fun countWin(image: Int) {
        winList.forEach {
            if (image == it) {
                count++
                binding.tvCount.text = count.toString()
            }
        }
    }
}