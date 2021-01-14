package fortuna.fortuna.persistence

import fortuna.fortuna.persistence.dto.DndPlayer
import fortuna.fortuna.persistence.dto.DndSession

interface DndSessionStore {
    fun newSession(session: DndSession): DndSession
    fun getSession(server: String, channel: String): DndSession?
    fun updateSession(dndSession: DndSession): DndSession
    fun cleanupSession(server: String, channel: String)
    fun addPlayer(dndSession: DndSession, playername: String): DndSession
    fun updatePlayer(dndSession: DndSession, player: DndPlayer): DndSession
    fun removePlayer(dndSession: DndSession, playerName: String): DndSession
}
