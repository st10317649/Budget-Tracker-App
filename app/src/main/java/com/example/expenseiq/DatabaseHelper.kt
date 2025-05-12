package com.example.expenseiq

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ExpenseIQDatabase.db"
        private const val DATABASE_VERSION = 2

        // Users
        const val TABLE_USERS = "Users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"

        // Categories
        const val TABLE_CATEGORIES = "Categories"
        const val COLUMN_CATEGORY_ID = "id"
        const val COLUMN_CATEGORY_USER_ID = "user_id"
        const val COLUMN_CATEGORY_NAME = "name"
        const val COLUMN_CATEGORY_TYPE = "type"

        // Expenses
        const val TABLE_EXPENSES = "Expenses"
        const val COLUMN_EXPENSE_ID = "id"
        const val COLUMN_EXPENSE_USER_ID = "user_id"
        const val COLUMN_EXPENSE_NAME = "expense_name"
        const val COLUMN_EXPENSE_IMAGE_URI = "image_uri"
        const val COLUMN_EXPENSE_AMOUNT = "amount"
        const val COLUMN_EXPENSE_CATEGORY_ID = "category_id"
        const val COLUMN_EXPENSE_DATE = "date"
        const val COLUMN_EXPENSE_DESCRIPTION = "description"

        // Budgets
        const val TABLE_BUDGETS = "Budgets"
        const val COLUMN_BUDGET_ID = "id"
        const val COLUMN_BUDGET_USER_ID = "user_id"
        const val COLUMN_BUDGET_AMOUNT = "amount"
        const val COLUMN_BUDGET_CATEGORY_ID = "category_id"
        const val COLUMN_BUDGET_DATE = "date"
        const val COLUMN_BUDGET_DESCRIPTION = "description"

        // Incomes
        const val TABLE_INCOMES = "Incomes"
        const val COLUMN_INCOME_ID = "id"
        const val COLUMN_INCOME_USER_ID = "user_id"
        const val COLUMN_INCOME_AMOUNT = "amount"
        const val COLUMN_INCOME_NAME = "income_name"
        const val COLUMN_INCOME_CATEGORY_ID = "category_id"
        const val COLUMN_INCOME_DATE = "date"
        const val COLUMN_INCOME_DESCRIPTION = "description"

        // Goals
        const val TABLE_GOALS = "Goals"
        const val COLUMN_GOAL_ID = "id"
        const val COLUMN_GOAL_USER_ID = "user_id"
        const val COLUMN_MIN_GOAL = "min_goal"
        const val COLUMN_MAX_GOAL = "max_goal"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL
            );
        """.trimIndent()

        val createCategoriesTable = """
            CREATE TABLE $TABLE_CATEGORIES (
                $COLUMN_CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CATEGORY_USER_ID INTEGER,
                $COLUMN_CATEGORY_NAME TEXT NOT NULL,
                $COLUMN_CATEGORY_TYPE TEXT NOT NULL,
                FOREIGN KEY($COLUMN_CATEGORY_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            );
        """.trimIndent()

        val createExpensesTable = """
            CREATE TABLE $TABLE_EXPENSES (
                $COLUMN_EXPENSE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_EXPENSE_USER_ID INTEGER,
                $COLUMN_EXPENSE_NAME STRING NOT NULL,
                $COLUMN_EXPENSE_AMOUNT REAL NOT NULL,
                $COLUMN_EXPENSE_IMAGE_URI TEXT,
                $COLUMN_EXPENSE_CATEGORY_ID INTEGER NOT NULL,
                $COLUMN_EXPENSE_DATE TEXT NOT NULL,
                $COLUMN_EXPENSE_DESCRIPTION TEXT,
                FOREIGN KEY($COLUMN_EXPENSE_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID),
                FOREIGN KEY($COLUMN_EXPENSE_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($COLUMN_CATEGORY_ID)
            );
        """.trimIndent()

        val createBudgetsTable = """
            CREATE TABLE $TABLE_BUDGETS (
                $COLUMN_BUDGET_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_BUDGET_USER_ID INTEGER,
                $COLUMN_BUDGET_AMOUNT REAL NOT NULL,
                $COLUMN_BUDGET_CATEGORY_ID INTEGER NOT NULL,
                $COLUMN_BUDGET_DATE TEXT NOT NULL,
                $COLUMN_BUDGET_DESCRIPTION TEXT,
                FOREIGN KEY($COLUMN_BUDGET_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID),
                FOREIGN KEY($COLUMN_BUDGET_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($COLUMN_CATEGORY_ID)
            );
        """.trimIndent()

        val createIncomesTable = """
            CREATE TABLE $TABLE_INCOMES (
                $COLUMN_INCOME_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_INCOME_USER_ID INTEGER,
                $COLUMN_INCOME_AMOUNT REAL NOT NULL,
                $COLUMN_INCOME_NAME TEXT NOT NULL,
                $COLUMN_INCOME_CATEGORY_ID INTEGER NOT NULL,
                $COLUMN_INCOME_DATE TEXT NOT NULL,
                $COLUMN_INCOME_DESCRIPTION TEXT,
                FOREIGN KEY($COLUMN_INCOME_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID),
                FOREIGN KEY($COLUMN_INCOME_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($COLUMN_CATEGORY_ID)
            );
        """.trimIndent()

        val createGoalsTable = """
            CREATE TABLE $TABLE_GOALS (
                $COLUMN_GOAL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_GOAL_USER_ID INTEGER,
                $COLUMN_MIN_GOAL REAL,
                $COLUMN_MAX_GOAL REAL,
                FOREIGN KEY($COLUMN_GOAL_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            );
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createCategoriesTable)
        db.execSQL(createExpensesTable)
        db.execSQL(createBudgetsTable)
        db.execSQL(createIncomesTable)
        db.execSQL(createGoalsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_INCOMES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BUDGETS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GOALS")
        onCreate(db)
    }
    fun insertUser(username: String, password: String): Long {
        val db = readableDatabase

        // Check for existing username
        val usernameCursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID),
            "$COLUMN_USERNAME = ?",
            arrayOf(username),
            null, null, null
        )
        if (usernameCursor.moveToFirst()) {
            usernameCursor.close()
            return -2L // Username exists
        }
        usernameCursor.close()


        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
        }

        return try {
            writableDatabase.insertOrThrow(TABLE_USERS, null, values)
        } catch (e: SQLException) {
            -1L // Unknown DB error
        }
    }
    fun readUserByUsername(username: String, password: String): Boolean {
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.query(
            TABLE_USERS,
            null,
            selection,
            arrayOf(username, password),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun checkUserExists(username: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_USERNAME = ?",
            arrayOf(username),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getUserId(username: String): Int? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID),
            "$COLUMN_USERNAME = ?",
            arrayOf(username),
            null,
            null,
            null
        )
        val id =
            if (cursor.moveToFirst()) cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)) else null
        cursor.close()
        return id
    }

    fun getUsernameById(userId: Int): String {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_USERNAME FROM $TABLE_USERS WHERE $COLUMN_USER_ID = ?",
            arrayOf(userId.toString())
        )
        val username = if (cursor.moveToFirst()) cursor.getString(0) else "User"
        cursor.close()
        return username
    }


    fun insertCategory(userId: Int?, name: String, type: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            if (userId != null) put(COLUMN_CATEGORY_USER_ID, userId)
            put(COLUMN_CATEGORY_NAME, name)
            put(COLUMN_CATEGORY_TYPE, type)
        }
        return db.insert(TABLE_CATEGORIES, null, values)
    }

    fun getCategoryCountForUser(userId: Int): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_CATEGORIES WHERE $COLUMN_CATEGORY_USER_ID = ?",
            arrayOf(userId.toString())
        )
        val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return count
    }

    fun getCategoryCountForUserByType(userId: Int, type: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_CATEGORIES WHERE $COLUMN_CATEGORY_USER_ID = ? AND $COLUMN_CATEGORY_TYPE = ?",
            arrayOf(userId.toString(), type)
        )
        val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return count
    }

    fun getCategoryIdForName(name: String, userId: Int): Int? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_CATEGORIES,
            arrayOf(COLUMN_CATEGORY_ID),
            "($COLUMN_CATEGORY_USER_ID IS NULL OR $COLUMN_CATEGORY_USER_ID = ?) AND $COLUMN_CATEGORY_NAME = ?",
            arrayOf(userId.toString(), name),
            null,
            null,
            null
        )
        val id = if (cursor.moveToFirst()) cursor.getInt(
            cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)
        ) else null
        cursor.close()
        return id
    }

    fun insertExpense(
        userId: Int,
        amount: Double,
        categoryId: Int,
        date: String, // Already in yyyy-MM-dd format
        description: String,
        expenseName: String,
        imageUri: String?
    ): Long {
        val db = writableDatabase

        val formattedDate = date // Already correct from UI

        val values = ContentValues().apply {
            put(COLUMN_EXPENSE_USER_ID, userId)
            put(COLUMN_EXPENSE_AMOUNT, amount)
            put(COLUMN_EXPENSE_CATEGORY_ID, categoryId)
            put(COLUMN_EXPENSE_DATE, formattedDate)
            put(COLUMN_EXPENSE_DESCRIPTION, description)
            put(COLUMN_EXPENSE_NAME, expenseName)
            put(COLUMN_EXPENSE_IMAGE_URI, imageUri)
        }
        Log.d("InsertExpense", "Inserting expense with date: $formattedDate")
        return db.insert(TABLE_EXPENSES, null, values)
    }

    fun getExpensesForUserInRange(
        userId: Int,
        startDate: String,
        endDate: String
    ): List<ExpenseItem> {
        val db = readableDatabase

        // Log the start and end dates
        Log.d("ExpenseQuery", "Start Date: $startDate, End Date: $endDate")

        val query = """
        SELECT e.amount, c.name AS category, e.description, e.id AS expense_id, 
               e.expense_name, e.date, e.image_uri
        FROM $TABLE_EXPENSES e
        JOIN $TABLE_CATEGORIES c ON e.$COLUMN_EXPENSE_CATEGORY_ID = c.$COLUMN_CATEGORY_ID
        WHERE e.$COLUMN_EXPENSE_USER_ID = ? AND e.$COLUMN_EXPENSE_DATE BETWEEN ? AND ?
        ORDER BY e.$COLUMN_EXPENSE_DATE ASC
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(userId.toString(), startDate, endDate))

        val expenses = mutableListOf<ExpenseItem>()
        if (cursor.moveToFirst()) {
            do {
                try {
                    val amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"))
                    val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                    val expenseId = cursor.getInt(cursor.getColumnIndexOrThrow("expense_id"))
                    val expenseName = cursor.getString(cursor.getColumnIndexOrThrow("expense_name"))
                    val dateString = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                    val imageUri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"))

                    // Log the raw date from the database
                    Log.d("ExpenseDate", "Stored date string: $dateString")

                    // Try parsing the date
                    val date: Date? = try {
                        val parsedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)
                        if (parsedDate != null && parsedDate.year > 100) {
                            parsedDate
                        } else {
                            Log.w("InvalidDate", "Skipping expense with invalid date: $dateString")
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("DateParseError", "Failed to parse date: $dateString", e)
                        null
                    }

                    if (date != null) {
                        val expenseItem = ExpenseItem(
                            amount = amount,
                            category = category,
                            description = description,
                            expenseName = expenseName,
                            date = date,
                            imageUri = imageUri
                        )
                        expenses.add(expenseItem)
                    } else {
                        Log.w("ExpenseSkip", "Skipping expense with invalid date: $dateString")
                    }
                } catch (e: Exception) {
                    Log.e("ExpenseParseError", "Error parsing expense row", e)
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        return expenses
    }


    fun getExpensesForUser(userId: Int): List<ExpenseItem> {
        val expenseList = mutableListOf<ExpenseItem>()
        val db = readableDatabase

        val query = """
        SELECT e.amount, c.name AS category_name, e.description, e.expense_name, e.date, e.image_uri
        FROM $TABLE_EXPENSES e
        JOIN $TABLE_CATEGORIES c ON e.$COLUMN_EXPENSE_CATEGORY_ID = c.$COLUMN_CATEGORY_ID
        WHERE e.$COLUMN_EXPENSE_USER_ID = ?
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"))
                val category = cursor.getString(cursor.getColumnIndexOrThrow("category_name"))
                val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                val expenseName = cursor.getString(cursor.getColumnIndexOrThrow("expense_name"))
                val dateString = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)
                val imageUri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"))


                val expenseItem = date?.let { ExpenseItem(amount, category, description, expenseName, it,imageUri) }
                if (expenseItem != null) {
                    expenseList.add(expenseItem)
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return expenseList
    }

    fun insertIncome(
        userId: Int,
        amount: Double,
        categoryId: Int,
        date: String,
        description: String,
        incomeName: String
    ): Long {
        val db = writableDatabase

        // Store only the date part of the string (no timestamps)
        val formattedDate = date.substringBefore(" ")  // Or just use dateTime if it's already in the correct format

        val values = ContentValues().apply {
            put(COLUMN_INCOME_USER_ID, userId) // Correct field for user_id
            put(COLUMN_INCOME_AMOUNT, amount)
            put(COLUMN_INCOME_CATEGORY_ID, categoryId)
            put(COLUMN_INCOME_DATE, formattedDate) // Only store date (no time)
            put(COLUMN_INCOME_DESCRIPTION, description)
            put(COLUMN_INCOME_NAME, incomeName)
        }
        return db.insert(TABLE_INCOMES, null, values)
    }


    fun getCategoryNamesForUser(userId: Int): List<String> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_CATEGORY_NAME FROM $TABLE_CATEGORIES WHERE $COLUMN_CATEGORY_USER_ID = ?",
            arrayOf(userId.toString())
        )
        val categories = mutableListOf<String>()
        while (cursor.moveToNext()) {
            categories.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME)))
        }
        cursor.close()
        return categories
    }

    fun saveSpendingGoals(userId: Int, minGoal: Double, maxGoal: Double): Long {
        val db = writableDatabase

        // Try update first
        val values = ContentValues().apply {
            put(COLUMN_MIN_GOAL, minGoal)
            put(COLUMN_MAX_GOAL, maxGoal)
        }
        val rowsUpdated = db.update(
            TABLE_GOALS,
            values,
            "$COLUMN_GOAL_USER_ID = ?",
            arrayOf(userId.toString())
        )

        if (rowsUpdated == 0) {
            // Insert if not existing
            values.put(COLUMN_GOAL_USER_ID, userId)
            return db.insert(TABLE_GOALS, null, values)
        }
        return rowsUpdated.toLong()
    }


    private fun toStartOfDay(date: String): String {
        return "$date 00:00:00"
    }

    private fun toEndOfDay(date: String): String {
        return "$date 23:59:59"
    }

    fun logAllExpenses() {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT id, date FROM $TABLE_EXPENSES", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val date = cursor.getString(1)
            Log.d("DB_DEBUG", "Expense ID=$id | Date=$date")
        }
        cursor.close()
    }
    fun logAllIncomes() {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT id, date FROM $TABLE_INCOMES", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val date = cursor.getString(1)
            Log.d("DB_DEBUG", "Income ID=$id | Date=$date")
        }
        cursor.close()
    }
    fun getIncomesForUser(userId: Int): List<IncomeItem> {
        val incomeList = mutableListOf<IncomeItem>()
        val db = readableDatabase

        // Query to join Incomes and Categories table to fetch income data for the user
        val query = """
            SELECT i.$COLUMN_INCOME_AMOUNT, 
                   c.$COLUMN_CATEGORY_NAME AS category, 
                   i.$COLUMN_INCOME_DESCRIPTION, 
                   i.$COLUMN_INCOME_NAME
            FROM $TABLE_INCOMES i
            JOIN $TABLE_CATEGORIES c ON i.$COLUMN_INCOME_CATEGORY_ID = c.$COLUMN_CATEGORY_ID
            WHERE i.$COLUMN_INCOME_USER_ID = ?
        """.trimIndent()

        // Debugging query output
        Log.d("DB_DEBUG", "Query: $query")

        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        // Extract data from the cursor
        if (cursor.moveToFirst()) {
            do {
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_INCOME_AMOUNT))
                val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INCOME_DESCRIPTION))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INCOME_NAME))

                // Add the income item to the list
                incomeList.add(IncomeItem(amount, category, description, name))

                Log.d("DB_DEBUG", "Income: Amount=$amount | Category=$category | Description=$description | Name=$name")
            } while (cursor.moveToNext())
        }

        cursor.close()
        return incomeList
    }
    fun getTotalSpentByCategory(userId: Int, startDate: String, endDate: String): List<CategoryTotal> {
        val categoryTotals = mutableListOf<CategoryTotal>()
        val db = readableDatabase

        Log.d("SQL_DEBUG", "Querying totals for userId=$userId, startDate=$startDate, endDate=$endDate")

        val query = """
        SELECT c.$COLUMN_CATEGORY_NAME AS category_name, 
               SUM(e.$COLUMN_EXPENSE_AMOUNT) AS total_spent
        FROM $TABLE_EXPENSES e
        JOIN $TABLE_CATEGORIES c ON e.$COLUMN_EXPENSE_CATEGORY_ID = c.$COLUMN_CATEGORY_ID
        WHERE e.$COLUMN_EXPENSE_USER_ID = ? AND e.$COLUMN_EXPENSE_DATE BETWEEN ? AND ?
        GROUP BY c.$COLUMN_CATEGORY_NAME
    """.trimIndent()

        Log.d("SQL_DEBUG", "Running query:\n$query")

        val cursor = db.rawQuery(query, arrayOf(userId.toString(), startDate, endDate))

        Log.d("SQL_DEBUG", "Found ${cursor.count} rows")

        if (cursor.moveToFirst()) {
            do {
                val category = cursor.getString(cursor.getColumnIndexOrThrow("category_name"))
                val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total_spent"))

                Log.d("SQL_DEBUG", "Row: category=$category, total=$total")

                categoryTotals.add(CategoryTotal(category, total))
            } while (cursor.moveToNext())
        } else {
            Log.w("SQL_DEBUG", "No expense data found for user $userId in range $startDate to $endDate")
        }

        cursor.close()
        return categoryTotals
    }

    fun getTotalIncome(userId: Int): Double {
        val db = this.readableDatabase
        val query = "SELECT SUM($COLUMN_INCOME_AMOUNT) FROM $TABLE_INCOMES WHERE $COLUMN_INCOME_USER_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))
        val totalIncome = if (cursor.moveToFirst()) cursor.getDouble(0) else 0.0
        cursor.close() // Close the cursor after use
        return totalIncome
    }

    fun getTotalExpense(userId: Int): Double {
        val db = this.readableDatabase
        val query = "SELECT SUM($COLUMN_EXPENSE_AMOUNT) FROM $TABLE_EXPENSES WHERE $COLUMN_EXPENSE_USER_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))
        val totalExpense = if (cursor.moveToFirst()) cursor.getDouble(0) else 0.0
        cursor.close() // Close the cursor after use
        return totalExpense
    }

}

