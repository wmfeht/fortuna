package fortuna.fortuna

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JDAConfiguration(
    val messageListener: MessageListener
) {
    @Bean
    fun buildJdaConfiguration(@Value("\${app.token}") token: String): JDA {
        return JDABuilder
            .create(token, listOf(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES))
            .disableCache(listOf(
                CacheFlag.VOICE_STATE,
                CacheFlag.CLIENT_STATUS,
                CacheFlag.EMOTE,
                CacheFlag.MEMBER_OVERRIDES,
                CacheFlag.ACTIVITY))
            .setMemberCachePolicy(MemberCachePolicy.NONE)
            .addEventListeners(messageListener)
            .build()
    }
}