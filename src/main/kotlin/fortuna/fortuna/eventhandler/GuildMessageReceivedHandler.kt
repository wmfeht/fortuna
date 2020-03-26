package fortuna.fortuna.eventhandler

import com.bernardomg.tabletop.dice.interpreter.DiceRoller
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GuildMessageReceivedHandler {
    val log = LoggerFactory.getLogger(GuildMessageReceivedHandler::class.java)
    val parser = DefaultDiceParser()
    val roller = DiceRoller()

    fun onEvent(event: GuildMessageReceivedEvent) {
        log.debug("Received message from ${event.author.id}")
        log.debug("Message contents: ${event.message.contentStripped}")
        if (event.message.contentStripped.startsWith("!roll ")) {
            val cmd = event.message.contentStripped.removePrefix("!roll ")
            val rolls = parser.parse(cmd, roller)
            val response = "${rolls.rollResults.map { it.allRolls }} ${rolls.totalRoll}"
            event.channel.sendMessage(response).queue()
        }
    }
}