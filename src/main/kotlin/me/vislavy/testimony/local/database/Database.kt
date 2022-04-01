package me.vislavy.testimony.local.database

import me.vislavy.testimony.local.data.Account
import me.vislavy.testimony.plugin
import java.sql.Connection
import java.sql.DriverManager

object Database {

    private const val TableName = "accounts"
    private val Url = "jdbc:sqlite:plugins/${plugin.dataFolder.name}/$TableName.db"

    private var connection: Connection? = null

    private val logger = plugin.logger

    fun openConnection() {
        try {
            logger.info("§6Connecting database...")
            connection = DriverManager.getConnection(Url)
        } catch (e: Exception) {
            logger.severe("${plugin.name} $e")
        }

        createNewTableIfNotExists()
    }

    fun closeConnection() {
        try {
            logger.info("§6Disconnecting database...")
            connection?.close()
        } catch (e: Exception) {
            logger.severe("${plugin.name} $e")
        }
    }

    fun getAccount(nickname: String): Account? {
        val sqlRequest = """
            SELECT nickname, ip, lastSeen, encryptedPassword, seed 
            FROM $TableName WHERE nickname = ?
        """.trimIndent()
        try {
            val prepareStatement = connection!!.prepareStatement(sqlRequest)
            prepareStatement.setString(1, nickname)
            val result = prepareStatement.executeQuery()
            while (result.next()) {
                return Account(
                    nickname = result.getString("nickname"),
                    ip = result.getString("ip"),
                    lastSeen = result.getLong("lastSeen"),
                    encryptedPassword = result.getString("encryptedPassword"),
                    seed = result.getString("seed")
                )
            }
        } catch (e: Exception) {
            logger.severe("${plugin.name} $e")
        }

        return null
    }

    fun addAccount(value: Account) {
        val sqlRequest = """
            INSERT INTO $TableName (nickname, ip, lastSeen, encryptedPassword, seed) 
            VALUES(?, ?, ?, ?, ?);
        """.trimIndent()
        try {
            val prepareStatement = connection!!.prepareStatement(sqlRequest)
            with (prepareStatement) {
                setString(1, value.nickname)
                setString(2, value.ip)
                setLong(3, value.lastSeen)
                setString(4, value.encryptedPassword)
                setString(5, value.seed)
                executeUpdate()
            }
        } catch (e: Exception) {
            logger.severe("${plugin.name} $e")
        }
    }

    fun updateAccount(nickname: String, value: Account) {
        val sqlRequest = """
            UPDATE $TableName SET 
                nickname = ?,
                ip = ?,
                lastSeen = ?,
                encryptedPassword = ?,
                seed = ?
            WHERE nickname = ?
        """.trimIndent()
        try {
            val prepareStatement = connection!!.prepareStatement(sqlRequest)
            with (prepareStatement) {
                setString(1, value.nickname)
                setString(2, value.ip)
                setLong(3, value.lastSeen)
                setString(4, value.encryptedPassword)
                setString(5, value.seed)
                setString(6, nickname)
                executeUpdate()
            }
        } catch (e: Exception) {
            logger.severe("${plugin.name} $e")
        }
    }

    fun deleteAccount(nickname: String) {
        val sqlRequest = """
            DELETE FROM $TableName WHERE nickname = ?
        """.trimIndent()
        try {
            val prepareStatement = connection!!.prepareStatement(sqlRequest)
            prepareStatement.setString(1, nickname)
            prepareStatement.executeUpdate()
        } catch (e: Exception) {
            logger.severe("${plugin.name} $e")
        }
    }

    private fun createNewTableIfNotExists() {
        val sqlRequest = """
            CREATE TABLE IF NOT EXISTS $TableName (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                nickname TEXT NOT NULL,
                ip TEXT NOT NULL,
                lastSeen INT NOT NULL,
                encryptedPassword TEXT NOT NULL,
                seed TEXT NOT NULL
            );
        """.trimIndent()
        try {
            val statement = connection!!.createStatement()
            statement.execute(sqlRequest)
        } catch (e: Exception) {
            plugin.logger.severe("${plugin.name} $e")
        }
    }
}