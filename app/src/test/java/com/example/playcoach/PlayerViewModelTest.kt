package com.example.playcoach

import com.example.playcoach.data.entities.PlayerEntity
import com.example.playcoach.data.repositories.PlayerRepository
import com.example.playcoach.viewmodels.PlayerViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PlayerViewModelTest {

    private lateinit var viewModel: PlayerViewModel
    private lateinit var repository: PlayerRepository

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        viewModel = PlayerViewModel(repository)
    }

    @Test
    fun `añadir jugador con dorsal duplicado devuelve false y no inserta`() = runTest {
        val existing = PlayerEntity(1, 10, "Juan", "Pérez", "JP", "Jugador", "Infantil A")
        coEvery { repository.getPlayersByTeam("Infantil A") } returns flowOf(listOf(existing))

        val result = viewModel.addPlayerIfPossible(
            team = "Infantil A", firstName = "Pedro", lastName = "Gómez", nickname = "PG", number = 10, position = "Jugador"
        )

        assertFalse(result)
        coVerify(exactly = 0) { repository.insertPlayer(any()) }
    }

    @Test
    fun `añadir jugador con dorsal único devuelve true e inserta`() = runTest {
        coEvery { repository.getPlayersByTeam("Infantil A") } returns flowOf(emptyList())

        val result = viewModel.addPlayerIfPossible(
            team = "Infantil A", firstName = "Pedro", lastName = "Gómez", nickname = "PG", number = 21, position = "Jugador"
        )

        assertTrue(result)
        coVerify(exactly = 1) { repository.insertPlayer(any()) }
    }

    @Test
    fun `eliminar jugador llama al repositorio`() = runTest {
        val player = PlayerEntity(1, 15, "Luis", "Martínez", "LM", "Portero", "Infantil B")

        viewModel.deletePlayer(player)

        coVerify(exactly = 1) { repository.deletePlayer(player) }
    }
}
