package fortuna.fortuna

import fortuna.fortuna.eventhandler.GuildMessageReceivedHandler
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MessageListener(
    val guildMessageReceivedHandler: GuildMessageReceivedHandler
) : EventListener {
    val log = LoggerFactory.getLogger(MessageListener::class.java)

    override fun onEvent(event: GenericEvent) {
        when {
            event is ReadyEvent -> log.info("Api is ready!")
            event is GuildMessageReceivedEvent -> guildMessageReceivedHandler.onEvent(event)
        }
    }
}