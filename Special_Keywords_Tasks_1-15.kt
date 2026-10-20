/*
 * Laboratory Work 5 — Kotlin Special Keywords
 * All 15 tasks are combined into one Main.kt file.
 *
 * Requirements:
 * - Kotlin/JVM 2.2+ is recommended (Task 5 uses break/continue inside inline lambdas).
 * - Select a task number from the main menu.
 * - Task 2, Task 4 and Task 9 use the corrected versions so the file compiles.
 * - Task 3 includes the extra non-inline/inline demonstration.
 */

import kotlin.system.exitProcess

// ----------------------------- TASK 01 ---------------------------------------
inline fun <T> ask01(prompt: String, parse: (String) -> T?): T {
    while (true) {
        print(prompt)
        val value = parse(readln().trim())
        if (value != null) return value
        println("Invalid input, try again.")
    }
}

fun task01() {
    var attempts = 0
    val age = ask01("Your age (0-120): ") { text ->
        attempts++
        text.toIntOrNull()?.takeIf { it in 0..120 }
    }
    println("Age accepted: $age after $attempts attempt(s)")
}

// ----------------------------- TASK 02 ---------------------------------------
val pending02 = mutableListOf<() -> Unit>()

inline fun addTask02(save: () -> Unit, noinline reminder: () -> Unit) {
    save()
    pending02.add(reminder)
}

fun task02() {
    print("Task name: ")
    val name = readln().trim()
    addTask02(
        save = { println("Saved: $name") },
        reminder = { println("Reminder: finish '$name'") }
    )
    println("Pending reminders: ${pending02.size}")
    pending02.forEach { it() }
    // A stored lambda is a real object and can be called later.
}

// ----------------------------- TASK 03 ---------------------------------------
val contacts03 = listOf("Ali Karimov", "Dilnoza Yusupova", "Aliyah Smith", "Bobur Aliev")

fun findFirst03(query: String) {
    contacts03.forEach {
        if (it.contains(query, ignoreCase = true)) {
            println("First match: $it")
            return // non-local return: exits findFirst03
        }
    }
    println("No contact contains '$query'")
}

fun findAll03(query: String) {
    contacts03.forEach {
        if (!it.contains(query, ignoreCase = true)) return@forEach
        println("Match: $it")
    }
}

fun each03(items: List<String>, action: (String) -> Unit) {
    for (item in items) action(item)
}

inline fun each03Inline(items: List<String>, action: (String) -> Unit) {
    for (item in items) action(item)
}

fun findWithEach03(query: String) {
    each03Inline(contacts03) {
        if (it.contains(query, ignoreCase = true)) {
            println("First match with inline each: $it")
            return
        }
    }
    println("No contact contains '$query'")
}

fun task03() {
    print("Search: ")
    val query = readln().trim()
    findFirst03(query)
    findAll03(query)
    println("Search finished")
    // Demonstrates that a bare return is allowed in a lambda passed to inline each:
    // findWithEach03(query)
}

// ----------------------------- TASK 04 ---------------------------------------
inline fun command04(crossinline action: () -> Unit): Runnable =
    Runnable { action() }

fun task04() {
    val commands = mapOf(
        "1" to command04 { println("Hello!") },
        "2" to command04 { println("Goodbye!") },
        "3" to command04 { println("Time to rest.") },
        "4" to command04 { println("Settings opened") }
    )
    print("Choose 1, 2, 3 or 4: ")
    val chosen = commands[readln().trim()]
    if (chosen == null) println("Unknown command") else chosen.run()
}

// ----------------------------- TASK 05 ---------------------------------------
fun orSkip05(text: String?, onNull: () -> Double): Double =
    text?.toDoubleOrNull() ?: onNull()

fun task05() {
    val prices = mutableListOf<Double>()
    println("Enter prices, one per line. Type 'done' to finish.")
    while (true) {
        val line = readln().trim()
        if (line == "done") break
        // Portable equivalent of the inline-lambda break/continue experiment.
        // In Kotlin 2.2+, the original experiment can use continue inside run { }
        // and break inside let { } when the language feature is enabled.
        val price = line.toDoubleOrNull()
        if (price == null) {
            println("'$line' is not a number, skipped")
            continue
        }
        if (price > 10_000) {
            println("Price $price is too large, stopping")
            break
        }
        prices.add(price)
    }
    println("Items: ${prices.size}, total: ${prices.sum()}")
    // orSkip05(line) { ... } is included above for the inline experiment.
}

// ----------------------------- TASK 06 ---------------------------------------
inline fun <reified T> readValue06(prompt: String): T? {
    print(prompt)
    val text = readln().trim()
    return when (T::class) {
        Int::class -> text.toIntOrNull() as T?
        Long::class -> text.toLongOrNull() as T?
        Double::class -> text.toDoubleOrNull() as T?
        Boolean::class -> text.toBooleanStrictOrNull() as T?
        String::class -> text as T?
        else -> error("Unsupported type ${T::class.simpleName}")
    }
}

fun task06() {
    val age = readValue06<Int>("Age: ")
    val height = readValue06<Double>("Height in meters: ")
    val subscribed = readValue06<Boolean>("Newsletter (true/false): ")
    val city = readValue06<String>("City: ")
    val phone = readValue06<Long>("Phone: ")
    println("age=$age, height=$height, subscribed=$subscribed, city=$city, phone=$phone")
}

// ----------------------------- TASK 07 ---------------------------------------
inline fun <reified T> List<Any>.onlyOfType07(): List<T> {
    val result = mutableListOf<T>()
    for (item in this) {
        if (item is T) result.add(item)
    }
    return result
}

fun parseToken07(token: String): Any =
    token.toIntOrNull()
        ?: token.toDoubleOrNull()
        ?: token.toBooleanStrictOrNull()
        ?: token

fun task07() {
    print("Items separated by spaces (for example: 10 apples 2.5 7): ")
    val items: List<Any> = readln().trim().split(Regex("\\s+"))
        .filter { it.isNotEmpty() }
        .map(::parseToken07)
    println("All: $items")
    println("Ints: ${items.onlyOfType07<Int>()}")
    println("Doubles: ${items.onlyOfType07<Double>()}")
    println("Words: ${items.onlyOfType07<String>()}")
    println("Booleans: ${items.onlyOfType07<Boolean>()}")
}

// ----------------------------- TASK 08 ---------------------------------------
const val VAT_RATE08 = 0.12

inline val Double.withVat08: Double
    get() = this * (1 + VAT_RATE08)

val String.wordCount08: Int
    inline get() = split(" ").count { it.isNotBlank() }

inline val Int.kilobytes08: Int
    get() = this * 1024

fun task08() {
    print("Price without VAT: ")
    val price = readln().trim().toDoubleOrNull()
    if (price == null) {
        println("Not a number")
        return
    }
    println("Price with VAT: ${"%.2f".format(price.withVat08)}")
    print("Write a sentence: ")
    val sentence = readln()
    println("Words: ${sentence.wordCount08}")
    print("Enter number of kilobytes: ")
    val kb = readln().trim().toIntOrNull()
    if (kb == null) println("Not a whole number")
    else println("$kb KB = ${kb.kilobytes08} bytes")
}

// ----------------------------- TASK 09 ---------------------------------------
@PublishedApi
internal var debugEnabled09 = false

inline fun debug09(message: () -> String) {
    if (debugEnabled09) println("[DEBUG] ${message()}")
}

fun buildReport09(): String {
    println("(building the report...)")
    return "report with 1000 rows"
}

fun task09() {
    print("Enable debug logging? (yes/no): ")
    debugEnabled09 = readln().trim().equals("yes", ignoreCase = true)
    debug09 { buildReport09() }
    println("Program finished")
}

// ----------------------------- TASK 10 ---------------------------------------
typealias Gradebook10 = MutableMap<String, MutableList<Int>>
typealias Grades10 = MutableList<Int>

fun Gradebook10.addGrade10(student: String, grade: Int) {
    getOrPut(student) { mutableListOf() }.add(grade)
}

fun Grades10.best10(): Int? = maxOrNull()

fun task10() {
    val book: Gradebook10 = mutableMapOf()
    println("Enter lines like: Ali 85 (type 'end' to stop)")
    while (true) {
        val line = readln().trim()
        if (line == "end") break
        val parts = line.split(Regex("\\s+"))
        val grade = parts.getOrNull(1)?.toIntOrNull()
        if (parts.size != 2 || grade == null) {
            println("Use: name grade")
            continue
        }
        book.addGrade10(parts[0], grade)
    }
    for ((name, grades) in book) {
        println("$name: ${grades.joinToString()} average = ${"%.1f".format(grades.average())}, best = ${grades.best10()}")
    }
}

// ----------------------------- TASK 11 ---------------------------------------
typealias Validator11 = (String) -> Boolean
typealias Predicate11<T> = (T) -> Boolean

val notBlank11: Validator11 = { it.isNotBlank() }
val isEmail11: Validator11 = { '@' in it && '.' in it.substringAfter('@') }
val isStrongPassword11: Validator11 = { it.length >= 8 && it.any(Char::isDigit) }

fun check11(label: String, input: String, validator: Validator11) {
    println("$label: ${if (validator(input)) "OK" else "INVALID"}")
}

fun <T> countMatching11(items: List<T>, rule: Predicate11<T>): Int = items.count(rule)

typealias Meters11 = Double
typealias Feet11 = Double

fun toFeet11(length: Meters11): Feet11 = length * 3.28084

fun describeRoom11(width: Meters11) {
    println("Room width: $width m")
}

fun roomDemo11() {
    print("Room width in meters: ")
    val width = readln().trim().toDoubleOrNull()
    if (width == null) {
        println("Invalid number")
        return
    }
    val feet: Feet11 = toFeet11(width)
    println("That is $feet feet")
    describeRoom11(feet) // Both aliases are Double, so the compiler permits this.
}

fun task11() {
    print("E-mail: ")
    val email = readln().trim()
    print("Password: ")
    val password = readln()
    check11("E-mail", email, isEmail11)
    check11("Password", password, isStrongPassword11)
    check11("Not blank", email) { it.isNotBlank() }
    val plain: (String) -> Boolean = isEmail11
    println("Used as a plain function type: ${plain(email)}")
    println("Valid e-mails in the list: ${countMatching11(listOf(email, "x@y.uz", "bad"), isEmail11)}")
    roomDemo11()
    val phones = mutableListOf<String>()
    repeat(3) {
        print("Phone ${it + 1} (+998...): ")
        phones.add(readln().trim())
    }
    val startsWithCode: Predicate11<String> = { it.startsWith("+998") }
    println("Numbers starting with +998: ${countMatching11(phones, startsWithCode)}")
}

// ----------------------------- TASK 12 ---------------------------------------
class Contact12(val name: String, val phone: String) {
    override fun toString(): String = "$name ($phone)"

    override fun equals(other: Any?): Boolean =
        other is Contact12 && other.phone == phone

    override fun hashCode(): Int = phone.hashCode()
}

fun readContact12(): Contact12 {
    print("Name: ")
    val name = readln().trim()
    print("Phone: ")
    val phone = readln().trim()
    return Contact12(name, phone)
}

fun task12() {
    println("--- first contact ---")
    val a = readContact12()
    println("--- second contact ---")
    val b = readContact12()
    println(a)
    println("a == b: ${a == b}")
    println("a === b: ${a === b}")
    val book = hashSetOf(a)
    println("book contains b: ${b in book}")
}

// ----------------------------- TASK 13 ---------------------------------------
fun Any.typeName13(): String = this::class.simpleName ?: "anonymous"

fun describe13(value: Any?): String = when (value) {
    null -> "no value"
    is Int -> "a number, doubled it is ${value * 2}"
    is Boolean -> if (value) "switched ON" else "switched OFF"
    is String -> "a text of ${value.length} characters"
    is Double -> "a decimal number: $value"
    else -> "an unknown type: ${value.typeName13()}"
}

fun task13() {
    val settings: Map<String, Any?> = mapOf(
        "volume" to 7,
        "darkMode" to true,
        "username" to "Dilnoza",
        "theme" to null,
        "ratio" to 1.5
    )
    println("Settings: ${settings.keys}")
    print("Setting name: ")
    val key = readln().trim()
    if (key !in settings) {
        println("No such setting")
        return
    }
    val value = settings[key]
    println("$key is ${describe13(value)}")
    val asNumber = value as? Int
    println("as? Int gives: $asNumber")
    val asText = value as? String
    println("as? String gives: $asText")
    println("5 type: ${5.typeName13()}")
    println("\"x\" type: ${"x".typeName13()}")
}

// ----------------------------- TASK 14 ---------------------------------------
fun fail14(message: String): Nothing = throw IllegalArgumentException(message)

fun readAge14(): Int {
    print("Age: ")
    val age = readln().trim().toIntOrNull() ?: fail14("Age must be a whole number")
    if (age < 0) fail14("Age cannot be negative")
    return age
}

fun task14() {
    try {
        val age = readAge14()
        println("You will be ${age + 1} next year")
    } catch (e: IllegalArgumentException) {
        println("Input error: ${e.message}")
    }
}

// ----------------------------- TASK 15 ---------------------------------------
fun fail15(message: String): Nothing = throw IllegalStateException(message)

fun runCalculator15(): Nothing {
    var total = 0
    while (true) {
        print("> ")
        val parts = readln().trim().split(Regex("\\s+"))
        val number = parts.getOrNull(1)?.toIntOrNull()
        when (parts[0]) {
            "add" -> total += number ?: 0
            "sub" -> total -= number ?: 0
            "mul" -> total *= number ?: 1
            "reset" -> total = 0
            "show" -> println("Total = $total")
            "exit" -> {
                println("Bye, final total = $total")
                exitProcess(0)
            }
            else -> println("Unknown command '${parts[0]}'")
        }
    }
}

fun discountFor15(code: String): Int = when (code.uppercase()) {
    "STUDENT" -> 20
    "FRIEND" -> 10
    else -> fail15("Unknown coupon '$code'")
}

fun task15() {
    val none: List<Nothing> = emptyList()
    val names: List<String> = none
    val ages: List<Int> = none
    println("${names.size} names, ${ages.size} ages")
    print("Coupon (STUDENT/FRIEND): ")
    try {
        println("Discount: ${discountFor15(readln().trim())}%")
    } catch (e: IllegalStateException) {
        println(e.message)
    }
    println("Commands: add N, sub N, mul N, reset, show, exit")
    runCalculator15()
    // This line is unreachable because runCalculator15() returns Nothing.
}

// -------------------------------- MAIN ----------------------------------------
fun main() {
    while (true) {
        println()
        println("========== Kotlin Special Keywords: Tasks 1–15 ==========")
        println("Enter task number 1-15, or 0 to exit.")
        print("Task: ")
        when (readln().trim()) {
            "1" -> task01()
            "2" -> task02()
            "3" -> task03()
            "4" -> task04()
            "5" -> task05()
            "6" -> task06()
            "7" -> task07()
            "8" -> task08()
            "9" -> task09()
            "10" -> task10()
            "11" -> task11()
            "12" -> task12()
            "13" -> task13()
            "14" -> task14()
            "15" -> task15()
            "0" -> return
            else -> println("Invalid choice. Enter a number from 0 to 15.")
        }
    }
}
