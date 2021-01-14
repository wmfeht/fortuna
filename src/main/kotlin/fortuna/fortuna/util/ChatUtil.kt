package fortuna.fortuna.util

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

fun GuildMessageReceivedEvent.respond(text: String) {
    this.channel.sendMessage(text).queue()
}