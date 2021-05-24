package com.example.android.unscramble.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel contendo os dados do aplicativo e métodos para processar os dados
 */
class GameViewModel : ViewModel() {
    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord: LiveData<String>
        get() = _currentScrambledWord

// Para usar o Talkack
//    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
//        if (it == null) {
//            SpannableString("")
//        } else {
//            val scrambledWord = it.toString()
//            val spannable: Spannable = SpannableString(scrambledWord)
//            spannable.setSpan(
//                TtsSpan.VerbatimBuilder(scrambledWord).build(),
//                0,
//                scrambledWord.length,
//                Spannable.SPAN_INCLUSIVE_INCLUSIVE
//            )
//            spannable
//        }
//    }

    // Vai armazenar uma lista de palavras que estão sendo usadas no jogo para evitar repetições.
    private var wordsList: MutableList<String> = mutableListOf()

    // Vai armazenar a palavra que vai ser decifrada
    private lateinit var currentWord: String

    // Chamada do método para exibir uma palavra embaralhada ao iniciar o app
    init {
        getNextWord()
    }

    //Atualiza currentWord e currentScrambledWord com a próxima palavra.
    private fun getNextWord() {
        // Recebe uma palavra aleatoria da lista de palavras
        currentWord = allWordsList.random()
        // Converte a string currentWord em uma matriz de caracteres e embaralha as letras
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()
        // Condição para que a palavra embaralhada não seja igual a original
        while (tempWord.toString().equals(currentWord, false)) {
            tempWord.shuffle()
        }
        // Verifica se a palavra já foi usada.
        // Se a wordsList contiver currentWord, chama getNextWord().
        // Caso contrário, atualiza o valor da _currentScrambledWord com a palavra recém embaralhada,
        // Aumenta a contagem de palavras e adiciona a nova palavra à wordsList.
        if (wordsList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordsList.add(currentWord)
        }
    }

    // Reinicializa os dados do jogo
    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }

    // Aumenta a pontuação do jogo se a palavra estiver correta.
    private fun increaseScore() {
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    // Retorna verdadeiro se a palavra do jogador estiver correta.
    // Aumenta a pontuação de acordo.
    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    // Acessa a próxima palavra da lista e retorne true se a contagem de palavras
    // for menor que o MAX_NO_OF_WORDS.
    fun nextWord(): Boolean {
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }
}
