//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.io.File

data class Recipe(
    val name: String,
    val ingredients: List<String>,
    val instructions: String,
    val categories: List<String>
)

const val CSV_PATH = "src/recipes.csv"

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

//////////////////////////////////////////////////////
// CSV LOAD / SAVE
//////////////////////////////////////////////////////

fun loadRecipes(): MutableList<Recipe> {
    val file = File(CSV_PATH)

    if (!file.exists()) {
        println("No recipes file found. Creating new one.")
        file.createNewFile()
        return mutableListOf()
    }

    val recipes = mutableListOf<Recipe>()

    file.forEachLine { line ->
        if (line.isBlank()) return@forEachLine

        // Find comma boundaries safely
        val firstComma = line.indexOf(',')
        val secondComma = line.indexOf(',', firstComma + 1)
        val lastComma = line.lastIndexOf(',')

        if (firstComma == -1 || secondComma == -1 || lastComma == -1 || lastComma == secondComma) {
            println("Skipping malformed line: $line")
            return@forEachLine
        }

        val name = line.substring(0, firstComma).trim()
        val ingredientsRaw = line.substring(firstComma + 1, secondComma).trim()
        val instructions = line.substring(secondComma + 1, lastComma).trim()
        val categoriesRaw = line.substring(lastComma + 1).trim()

        val ingredients = if (ingredientsRaw.isBlank()) emptyList()
        else ingredientsRaw.split("|").map { it.trim() }

        val categories = if (categoriesRaw.isBlank()) emptyList()
        else categoriesRaw.split("|").map { it.trim() }

        recipes.add(Recipe(name, ingredients, instructions, categories))
    }

    return recipes
}

fun saveRecipes(recipes: List<Recipe>) {
    val file = File(CSV_PATH)
    file.printWriter().use { out ->
        for (r in recipes) {
            val ingredients = r.ingredients.joinToString("|")
            val categories = r.categories.joinToString("|")
            out.println("${r.name},$ingredients,${r.instructions},$categories")
        }
    }
}

//////////////////////////////////////////////////////
// MENU ACTIONS
//////////////////////////////////////////////////////

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
    val query = readln().trim().lowercase()

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
