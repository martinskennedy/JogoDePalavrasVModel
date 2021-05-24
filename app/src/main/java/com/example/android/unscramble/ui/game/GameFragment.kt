/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.example.android.unscramble.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragmento onde contém a lógica do jogo.
 */
class GameFragment : Fragment() {

    // Instância o binding para acesso às visualizações no layout game_fragment.xml
    private lateinit var binding: GameFragmentBinding

    // Cria um ViewModel na primeira vez que o fragmento é criado.
    // Se o fragmento for recriado, ele receberá a mesma instância de GameViewModel criada pelo
    // primeiro fragmento
    private val viewModel: GameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o arquivo XML de layout e retorna uma instância do binding
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Define o viewModel para vinculação de dados - isso permite o acesso ao layout vinculado
        // para todos os dados no VieWModel
        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS

        // Especifica a visualização do fragmento como o proprietário do ciclo de vida do binding.
        // É usado para o binding observar as atualizações do LiveData
        binding.lifecycleOwner = viewLifecycleOwner

        // Configure os cliques para os botões Enviar e Ignorar.
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }
    }

    /* Verifica a palavra do usuário e atualiza a pontuação.
     * Exibe a próxima palavra codificada.
     * Após a última palavra, é mostrado ao usuário um Diálogo com a pontuação final.
     */
    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()

        if (viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if (!viewModel.nextWord()) {
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }
    }

    // Pula a palavra atual sem alterar a pontuação.
    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextField(false)
        } else {
            showFinalScoreDialog()
        }
    }

    // Cria e mostra um AlertDialog com a pontuação final.
    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.you_scored, viewModel.score.value))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                exitGame()
            }
            .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                restartGame()
            }
            .show()
    }

    // Reinicializa os dados no ViewModel e atualiza as visualizações com os novos dados, para
    // reiniciar o jogo.
    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
    }

    // Sai do jogo.
    private fun exitGame() {
        activity?.finish()
    }

    // Define e redefine o status de erro do campo de texto.
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }
}
