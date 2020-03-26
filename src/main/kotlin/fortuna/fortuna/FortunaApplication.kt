package fortuna.fortuna

import net.dv8tion.jda.api.JDA
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FortunaApplication(jda: JDA)

fun main(args: Array<String>) {
	runApplication<FortunaApplication>(*args)
}
