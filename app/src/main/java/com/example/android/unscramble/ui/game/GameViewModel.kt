package com.example.android.unscramble.ui.game


import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel



class GameViewModel : ViewModel () {
    private val _score:MutableLiveData<Int> = MutableLiveData(0);
    val score: LiveData<Int>
        get() = _score
    private val _currentWordCount:MutableLiveData<Int> = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount // make it a read only but can change within the calss

    private  var wordsList:MutableList<String> = mutableListOf()
    private lateinit var currentWord:String
    private val _currentScrambledWord:MutableLiveData<String> =  MutableLiveData<String>()
    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
        if (it == null) {
            SpannableString("")
        } else {
            val scrambledWord = it.toString()
            val spannable: Spannable = SpannableString(scrambledWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambledWord).build(),
                0,
                scrambledWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }


    /**
     * Updates the current word and the currentScrambled word with the next word
     */
    private fun getNextWord() {
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()
        while (String(tempWord).equals(currentWord,false))    {
            tempWord.shuffle()
        }

        if(wordsList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentWordCount.value = _currentWordCount.value?.inc()
            _currentScrambledWord.value = String(tempWord)
            wordsList.add(currentWord)
        }
    }

    /**
     * Return the current word if the maxNumberOf word is not reached
     * And updates the word with nextWord
     */
    fun nextWord() :Boolean {
       return if (currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }

    fun isUserWordCorrect(playerWord:String):Boolean {
       return if (playerWord.equals(currentWord,true)){
            increaseScore()
            true
        } else false

    }

    fun increaseScore(){
        _score.value = _score.value?.plus(SCORE_INCREASE)
    }

    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }
    init {
        Log.d("GameFragment", "GameViewModelCreated")
        getNextWord()
    }

}