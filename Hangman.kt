import kotlin.system.exitProcess

const val lives = 8
var scores = mutableMapOf<String, Int>()

class Word(value: String) {
    var unmaskedValue = value
    var maskedValue = "_".repeat(value.length)
    var guessedChars = mutableListOf<String>()
    fun unmaskChars() {
        val indices = mutableListOf<Int>()
        guessedChars.forEach {
            var index: Int = unmaskedValue.indexOf(it)
            while (index >= 0) {
                indices.add(index)
                index = unmaskedValue.indexOf(it, index + 1)
            }
        }
        indices.forEach { index ->
            maskedValue = maskedValue.replaceRange(index, index + 1, unmaskedValue[index].toString())
        }
    }

    fun checkChar(string: String) = string in unmaskedValue
    fun checkAlreadyGuessed(string: String) = string !in guessedChars
    fun checkWin() = unmaskedValue == maskedValue
}

class Game(playerName: String, word: Word) {
    private val name = playerName
    private var livesLeft = lives
    private var gameWord = word
    private fun valChar(character: String) = Regex("^[A-Z]$").matches(character)
    fun start() {
        println("Game is starting...")
        while (livesLeft != 0 && !gameWord.checkWin()) {
            println("Lives Remaining: $livesLeft")
            println(gameWord.maskedValue)
            val guess = guessChar()
            if (valChar(guess)) {
                if (gameWord.checkChar(guess) && gameWord.checkAlreadyGuessed(guess)) {
                    gameWord.guessedChars.add(guess)
                    gameWord.unmaskChars()
                } else if (gameWord.checkChar(guess) && !gameWord.checkAlreadyGuessed(guess))
                    println("You already guessed that character")
                else livesLeft -= 1
            } else println("Please enter a valid character")
        }
        if (gameWord.checkWin()) {
            println("You won. Score: $livesLeft")
            scores[name] = livesLeft
        } else println("You lost. The word was ${gameWord.unmaskedValue}.")
        menu()
    }

    private fun guessChar(): String {
        println("Guess a character:")
        return readLine().toString().toUpperCase()
    }
}

fun getName(): String {
    println("Enter Name:")
    val name = readLine().toString()
    println("Hello $name, Let’s play Hangman!")
    return name
}

fun getWord(): String {
    println("Enter Word:")
    return readLine().toString().toUpperCase()
}

fun menu() {
    println(if (scores.isEmpty()) "Welcome to Hangman! Would you like to play? (Y/N)" else "Play again? (Y/N/H)")
    when (readLine().toString().toUpperCase()) {
        "Y" -> {
            val playerName = getName()
            val word = Word(getWord())
            Game(playerName, word).start()
        }
        "N" -> {
            println("Goodbye!")
            exitProcess(0)
        }
        "H" -> showScores()
        else -> {
            println("Invalid choice")
            menu()
        }
    }
}

fun showScores() {
    val sorted = scores.toList().sortedByDescending { (_, value) -> value }.toMap()
    sorted.forEach {
        println("Name: ${it.key} | Score: ${it.value}")
    }
    menu()
}

fun main() = menu()
