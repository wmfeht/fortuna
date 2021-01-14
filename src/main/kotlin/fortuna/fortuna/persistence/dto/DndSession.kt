package fortuna.fortuna.persistence.dto

import java.time.ZonedDateTime

data class DndSession(
    val server: String,
    val channel: String,
    val createdDt: ZonedDateTime,
    val updatedDt: ZonedDateTime,
    val players: List<DndPlayer>,
    val next: String
) {
    fun key() = "$server:$channel"

    fun nextPlayer(): DndPlayer {
        return players[playerIndex(next)]
    }

    fun playerIndex(playerName: String): Int {
        return players.indexOf(getPlayer(playerName))
    }

    fun getPlayer(playerName: String): DndPlayer? {
        return players.firstOrNull{ it.playerName == playerName }
    }

    fun getPlayerAfter(playerName: String): DndPlayer {
        val i = playerIndex(playerName)
        if (players.size == i + 1) {
            return players[0]
        } else {
            return players[i + 1]
        }
    }
}

data class DndPlayer(
    val playerName: String,
    val playerScore: Int = 0,
    val toGive: Int = 0,
    val given: Int = 0
)