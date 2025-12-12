import java.io.File

data class Recipe(
    val name: String,
    val ingredients: List<String>,
    val instructions: String,
    val categories: List<String>
)

fun main() {
    val recipes = loadRecipes()
    println("Loaded ${recipes.size} recipes.")
    while (true) {
        println("\n==== Recipe Organizer ====")
        println("1. List all recipes")
        println("2. Search recipes")
        println("3. View a recipe")
        println("4. Add a recipe")
        println("5. Delete a recipe")
        println("6. Save & Exit")
        print("Choose option: ")
        when (readln().trim()) {
            "1" -> listRecipes(recipes)
            "2" -> searchMenu(recipes)
            "3" -> viewRecipe(recipes)
            "4" -> addRecipe(recipes)
            "5" -> deleteRecipe(recipes)
            "6" -> {
                saveRecipes(recipes)
                println("Saved. Goodbye!")
                return
            }
            else -> println("Invalid option.")
        }
    }
}

//csv splitter
fun splitCsvLine(line: String): List<String> {
    val result = mutableListOf<String>() //will be the 4 strings that make the recipe
    val current = StringBuilder()
    var inQuotes = false
    var i = 0
    while (i < line.length) {
        val c = line[i]
        when {
            c == '"' -> { //checks for quotes in instructions, flags when within quotations
                inQuotes = !inQuotes
            }
            c == ',' && !inQuotes -> { //ignores commas in quotes
                result.add(current.toString()) //finishes one of the strings
                current.clear() //able to start a new one
            }
            else -> current.append(c)
        }
        i++
    }
    result.add(current.toString())
    return result
}

//csv loading
fun loadRecipes(): MutableList<Recipe> {
    val file = File("src/recipes.csv")
    val recipes = mutableListOf<Recipe>()
    if (!file.exists()) return recipes
    file.forEachLine { line ->
        if (line.isBlank()) return@forEachLine
        val cols = splitCsvLine(line)
        val name = cols[0].trim()
        val ingredients = cols[1].split("|").map { it.trim() }
        val instructions = cols[2].trim()
        val categories = cols[3].split("|").map { it.trim() }
        recipes.add(Recipe(name, ingredients, instructions, categories))
    }
    return recipes
}

//csv saving
fun saveRecipes(recipes: List<Recipe>) {
    val file = File("src/recipes.csv")
    file.printWriter().use { out ->
        for (r in recipes) {
            val ingredients = r.ingredients.joinToString("|")
            val categories = r.categories.joinToString("|")
            out.println("${r.name},$ingredients,\"${r.instructions}\",$categories")
        }
    }
}

fun listRecipes(recipes: List<Recipe>) {
    println("\n=== All Recipes ===")
    if (recipes.isEmpty()) {
        println("No recipes.")
        return
    }
    recipes.forEachIndexed { i, r ->
        println("${i + 1}. ${r.name}")
    }
}

fun searchMenu(recipes: List<Recipe>) {
    print("Enter search term: ")
    val query = readln().trim().lowercase() //search term and results become lowercase so search is case-insensitive
    val results = recipes.filter { r ->
        r.name.lowercase().contains(query) ||
                r.instructions.lowercase().contains(query) ||
                r.categories.any { it.lowercase().contains(query) } ||
                r.ingredients.any { it.lowercase().contains(query) }
    }
    if (results.isEmpty()) {
        println("No matching recipes.")
        return
    }
    println("\n=== Search Results ===")
    results.forEach { println("- ${it.name}") }
}

fun viewRecipe(recipes: List<Recipe>) {
    print("Enter recipe name: ")
    val name = readln().trim().lowercase()
    val found = recipes.find { it.name.lowercase() == name }
    if (found == null) {
        println("Recipe not found.")
        return
    }
    println("\n=== ${found.name} ===")
    println("Ingredients:")
    found.ingredients.forEach { println(" - $it") }
    println("\nInstructions:")
    println(found.instructions)
    println("\nCategories:")
    found.categories.forEach { println(" - $it") }
}

fun addRecipe(recipes: MutableList<Recipe>) {
    print("Name: ")
    val name = readln().trim()
    println("Enter ingredients one per line. Blank line to finish:")
    val ingredients = mutableListOf<String>()
    while (true) {
        val line = readln().trim()
        if (line.isEmpty()) break
        ingredients.add(line)
    }
    println("Enter instructions (commas allowed):")
    val instructions = readln().trim()
    println("Enter categories one per line. Blank line to finish:")
    val categories = mutableListOf<String>()
    while (true) {
        val line = readln().trim()
        if (line.isEmpty()) break
        categories.add(line)
    }
    recipes.add(Recipe(name, ingredients, instructions, categories))
    println("Recipe added.")
}

fun deleteRecipe(recipes: MutableList<Recipe>) {
    print("Enter recipe name to delete: ")
    val name = readln().trim().lowercase()
    val removed = recipes.removeIf { it.name.lowercase() == name }
    if (removed) {
        println("Recipe deleted.")
    } else {
        println("Recipe not found.")
    }
}
