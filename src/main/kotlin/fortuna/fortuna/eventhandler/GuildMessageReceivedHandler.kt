package fortuna.fortuna.eventhandler

import com.bernardomg.tabletop.dice.interpreter.DiceRoller
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser
import fortuna.fortuna.service.DndService
import fortuna.fortuna.util.respond
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GuildMessageReceivedHandler(
    val dndService: DndService
) {
    val log = LoggerFactory.getLogger(GuildMessageReceivedHandler::class.java)
    val parser = DefaultDiceParser()
    val roller = DiceRoller()

    fun onEvent(event: GuildMessageReceivedEvent) {
        //log.debug("Received message from ${event.author.id}")
        //log.debug("Message contents: ${event.message.contentStripped}")
        val server = event.guild.id
        val channel = event.channel.id
        val player = event.author.name
        val messageContent = event.message.contentStripped

        if (messageContent.startsWith("?start")) {
            dndService.startSession(server, channel, player, event)
        }

        if (messageContent.startsWith(("?end"))) {
            dndService.stopSession(server, channel, event)
        }

        if (messageContent.startsWith("?restart")) {
            dndService.restartSession(server, channel, player, event)
        }

        if (messageContent.startsWith("?roll")) {
            dndService.roll(server, channel, player, event)
        }

        if (messageContent.startsWith("?join")) {
            dndService.join(server, channel, player, event)
        }

        if (messageContent.startsWith("?score")) {
            dndService.score(server, channel, event)
        }

        if (messageContent.startsWith("?give")) {
            val splitContent = messageContent.split(" ")
            if (splitContent.size != 3) {
                event.respond("I can't do that Dave.")
                return
            }
            val giveTo = splitContent.subList(1, splitContent.lastIndex - 1).joinToString( )
            val giveAmount = splitContent.last().toInt()
            dndService.give(server, channel, player, giveTo, giveAmount, event)
        }
    }
}