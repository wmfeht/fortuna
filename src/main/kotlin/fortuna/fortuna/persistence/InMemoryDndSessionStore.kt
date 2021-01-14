package fortuna.fortuna.persistence

import fortuna.fortuna.persistence.dto.DndPlayer
import fortuna.fortuna.persistence.dto.DndSession
import org.springframework.stereotype.Component
import java.time.ZonedDateTime

@Component
class InMemoryDndSessionStore : DndSessionStore {
    private val sessionMap: MutableMap<String, DndSession> = mutableMapOf()

    override fun newSession(session: DndSession): DndSession {
        if (sessionMap.containsKey(session.key())) {
            this.cleanupSession(session.server, session.channel)
        }
        sessionMap[session.key()] = session
        return session
    }

    override fun getSession(server: String, channel: String): DndSession? {
        return sessionMap["$server:$channel"]
    }

    override fun updateSession(dndSession: DndSession): DndSession {
        sessionMap[dndSession.key()] = dndSession.copy(updatedDt = ZonedDateTime.now())
        return sessionMap[dndSession.key()]!!
    }

    override fun cleanupSession(server: String, channel: String) {
        sessionMap.remove("$server:$channel")
    }

    override fun addPlayer(dndSession: DndSession, playerName: String): DndSession {
        val players = dndSession.players.toMutableList()
        players.add(DndPlayer(playerName, 0, 0))
        val session = dndSession.copy(players = players.toList(), next = playerName)
        return updateSession(session)
    }

    override fun updatePlayer(dndSession: DndSession, player: DndPlayer): DndSession {
        val players = dndSession.players.toMutableList()
        val index = players.indexOfFirst { it.playerName == player.playerName }
        players[index] = player
        val session = dndSession.copy(players = players.toList())
        return updateSession(session)
    }

    override fun removePlayer(dndSession: DndSession, playerName: String): DndSession {
        val players = dndSession.players.toMutableList()
        players.removeIf{ it.playerName == playerName }
        val session = dndSession.copy(players = players.toList())
        return updateSession(session)
    }
}