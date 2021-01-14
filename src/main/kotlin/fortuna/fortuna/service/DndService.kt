package fortuna.fortuna.service

import com.bernardomg.tabletop.dice.interpreter.DiceRoller
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser
import fortuna.fortuna.persistence.DndSessionStore
import fortuna.fortuna.persistence.dto.DndPlayer
import fortuna.fortuna.persistence.dto.DndSession
import fortuna.fortuna.util.respond
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.apache.commons.text.similarity.LevenshteinDistance
import org.springframework.stereotype.Component
import java.time.ZonedDateTime

@Component
class DndService(
    val dndSessionStore: DndSessionStore
) {
    val parser = DefaultDiceParser()
    val roller = DiceRoller()

    fun startSession(server: String, channel: String, creator: String, event: GuildMessageReceivedEvent) {
        if (dndSessionStore.getSession(server, channel) != null) {
            event.respond("The game has already started!")
            return
        }
        dndSessionStore.newSession(DndSession(
            server = server,
            updatedDt = ZonedDateTime.now(),
            createdDt = ZonedDateTime.now(),
            next = creator,
            players = arrayListOf(DndPlayer(creator, 0, 0)),
            channel = channel
        ))
        event.respond("Game started!")
    }

    fun restartSession(server: String, channel: String, creator: String, event: GuildMessageReceivedEvent) {
        val session = dndSessionStore.getSession(server, channel)
        if (session != null) dndSessionStore.cleanupSession(server, channel)
        startSession(server, channel, creator, event)
    }

    fun stopSession(server: String, channel: String, event: GuildMessageReceivedEvent) {
        val session = dndSessionStore.getSession(server, channel)
        if (session != null) dndSessionStore.cleanupSession(server, channel)
        event.respond("Game stopped!")
    }

    fun roll(server: String, channel: String, playerName: String, event: GuildMessageReceivedEvent) {
        var session = dndSessionStore.getSession(server, channel)
        if (session == null) {
            event.respond("Start a game first!")
            return
        }
        val player = session.getPlayer(playerName)
        if (player == null) {
            event.respond("Join the game first!")
            return
        }
        if (session.nextPlayer() != player) {
            event.respond("It's ${session.nextPlayer().playerName}'s turn!")
            return
        }
        val dSix = parser.parse("1d6", roller).totalRoll
        val dTwenty = parser.parse("1d20", roller).totalRoll

        val toGive = if (dSix % 2 == 0) dTwenty else 0
        val toDrink = if (dSix % 2 != 0) dTwenty else 0

        session = dndSessionStore.updatePlayer(session,
            player.copy(playerScore = player.playerScore + toDrink, toGive = player.toGive + toGive))
        session = dndSessionStore.updateSession(session.copy(next = session.getPlayerAfter(playerName).playerName))

        val action = if (toGive > 0) "Give $dTwenty!" else "Drink $dTwenty!"

        event.respond("""
            ${player.playerName} rolled!
            ${dTwenty}, ${dSix}
            $action,
            It's ${session.nextPlayer().playerName}'s turn!
        """.trimIndent())
    }

    fun give(server: String, channel: String, playerName: String, recipient: String, amount: Int, event: GuildMessageReceivedEvent) {
        var session = dndSessionStore.getSession(server, channel)
        if (session == null) {
            event.respond("Start a game first!")
            return
        }
        val player = session.getPlayer(playerName)
        if (player == null) {
            event.respond("Join the game first!")
            return
        }
        if (amount > player.toGive) {
            event.respond("You don't have that many drinks to give!")
            return
        }
        session = dndSessionStore.updatePlayer(session, player.copy(toGive = player.toGive - amount, given = player.given + amount))
        val rankedPlayers = session.players.sortedByDescending { LevenshteinDistance.getDefaultInstance().apply(it.playerName, playerName) }
        val giveTo = rankedPlayers.first()
        session = dndSessionStore.updatePlayer(session, giveTo.copy(playerScore = giveTo.playerScore + amount))
        event.respond("${giveTo.playerName}, drink $amount!")
    }

    fun join(server: String, channel: String, playerName: String, event: GuildMessageReceivedEvent) {
        var session = dndSessionStore.getSession(server, channel)
        if (session == null) {
            event.respond("Start a game first!")
            return
        }
        dndSessionStore.addPlayer(session, playerName)
        event.respond("$playerName joined!")
    }

    fun score(server: String, channel: String, event: GuildMessageReceivedEvent) {
        val session = dndSessionStore.getSession(server, channel)
        if (session == null) {
            event.respond("Game hasn't started yet!")
            return
        }
        val players = session.players
        val lines = players.map { "${it.playerName} | Drinks taken: ${it.playerScore} | Drinks to give: ${it.toGive} | Drinks given: ${it.given}" }
        event.respond(lines.joinToString("\n"))
    }
}