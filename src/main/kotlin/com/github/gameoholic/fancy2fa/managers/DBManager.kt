package com.github.gameoholic.fancy2fa.managers

import com.github.gameoholic.fancy2fa.datatypes.*
import org.bukkit.entity.Player
import java.lang.IllegalArgumentException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.*

object DBManager {

        private var username = ConfigManager.SQLUsername
        private var password = ConfigManager.SQLPassword
        private var port = ConfigManager.SQLPort
        private var name = ConfigManager.SQLName
        private var url = "jdbc:mysql://localhost:$port/$name"


        /**
         * Retrieves the password data for a player from the database.
         *
         * @return PlayerAuthDetails?: The player's hashed password, salt if found in the database, null otherwise.
         */
        fun getPlayerPasswordData(playerUUID: UUID): InternalDBResult<PlayerPasswordData?> {
                var playerAuthDetails: PlayerPasswordData?
                try {
                        val connection: Connection = DriverManager.getConnection(url, username, password)
                        val statement = connection.createStatement()
                        val query = """
                            SELECT * FROM users WHERE uuid = "$playerUUID"
                        """.trimIndent()

                        val resultSet: ResultSet = statement.executeQuery(query)

                        if (resultSet.next()) {
                                val password: String? = resultSet.getString("password")
                                val salt: String? = resultSet.getString("salt")
                                if (password == null || salt == null)
                                        playerAuthDetails = null
                                else
                                        playerAuthDetails = PlayerPasswordData(playerUUID, password, salt)
                        }
                        else
                                playerAuthDetails = null

                        resultSet.close()
                        statement.close()
                        connection.close()
                } catch (e: Exception) {
                        e.printStackTrace()
                        return InternalDBResult.Error("Internal exception")
                }
                return InternalDBResult.Success(playerAuthDetails)
        }

        /**
         * @return MutableList<String>: The player's security questions in the form of mutableListOf<String>
         */
        fun getPlayerSecurityQuestions(playerUUID: UUID): InternalDBResult<MutableList<SecurityQuestion>> {
                var questions = mutableListOf<SecurityQuestion>()
                try {
                        val connection: Connection = DriverManager.getConnection(url, username, password)
                        val statement = connection.createStatement()
                        val query = """
                            SELECT * FROM security_questions WHERE player_uuid = "$playerUUID"
                        """.trimIndent()

                        val resultSet: ResultSet = statement.executeQuery(query)

                        while (resultSet.next()) {
                                val question: String = resultSet.getString("question")
                                val hash: String = resultSet.getString("hash")
                                val salt: String = resultSet.getString("salt")
                                questions.add(SecurityQuestion(question, hash, salt))
                        }

                        resultSet.close()
                        statement.close()
                        connection.close()
                } catch (e: Exception) {
                        e.printStackTrace()
                        return InternalDBResult.Error("Internal exception")
                }
                return InternalDBResult.Success(questions)
        }

        /**
         * @return Boolean: Whether the player has a specific security question
         */
        fun doesSecurityQuestionExist(question: String, playerUUID: UUID): InternalDBResult<Boolean> {
                try {
                        val connection: Connection = DriverManager.getConnection(url, username, password)

                        val searchQuery = """
                            SELECT * FROM security_questions 
                            WHERE player_uuid = ? AND question = ?
                        """.trimIndent()
                        val statement = connection.prepareStatement(searchQuery)

                        statement.setString(1, playerUUID.toString())
                        statement.setString(2, question)

                        val resultSet: ResultSet = statement.executeQuery()

                        if (resultSet.next()) {
                                statement.close()
                                connection.close()
                                return InternalDBResult.Success(true)
                        }

                        statement.close()
                        connection.close()
                } catch (e: Exception) {
                        e.printStackTrace()
                        return InternalDBResult.Error("Internal exception")
                }
                return InternalDBResult.Success(false)
        }

        /**
         * @return Has no return value
         */
        fun addSecurityQuestion(question: String, answer: String, playerUUID: UUID): InternalDBResult<Nothing?> {
                if (answer == "")
                        return InternalDBResult.Error("The answer for the security question mustn't be empty.")
                var salt = CredentialsManager.genSalt(ConfigManager.hashLogRounds)
                var hash = CredentialsManager.hashString(answer, salt, ConfigManager.generalPepper)
                try {
                        val connection: Connection = DriverManager.getConnection(url, username, password)

                        val query = """
                            INSERT INTO security_questions (question, hash, salt, player_uuid)
                            VALUES (?, ?, ?, ?);
                        """.trimIndent()
                        val statement = connection.prepareStatement(query)

                        statement.setString(1, question)
                        statement.setString(2, hash)
                        statement.setString(3, salt)
                        statement.setString(4, playerUUID.toString())

                        statement.executeUpdate()

                        statement.close()
                        connection.close()
                } catch (e: Exception) {
                        e.printStackTrace()
                        return InternalDBResult.Error("Couldn't create security question, an internal error occurred.")
                }
                return InternalDBResult.Success(null)
        }

        /**
         * @return Has no return value
         */
        fun updateSecurityQuestion(question: String, newAnswer: String, playerUUID: UUID): InternalDBResult<Nothing?> {
                if (newAnswer == "")
                        return InternalDBResult.Error("The answer for the security question mustn't be empty.")
                var salt = CredentialsManager.genSalt(ConfigManager.hashLogRounds)
                var hash = CredentialsManager.hashString(newAnswer, salt, ConfigManager.generalPepper)
                try {
                        val connection: Connection = DriverManager.getConnection(url, username, password)
                        val query = """
                            UPDATE security_questions
                            SET hash = ?, salt = ?
                            WHERE question = ? AND player_uuid = ?;
                        """.trimIndent()
                        val statement = connection.prepareStatement(query)

                        statement.setString(1, hash)
                        statement.setString(2, salt)
                        statement.setString(3, question)
                        statement.setString(4, playerUUID.toString())

                        statement.executeUpdate()

                        statement.close()
                        connection.close()
                } catch (e: Exception) {
                        e.printStackTrace()
                        return InternalDBResult.Error("Couldn't create security question, an internal error occurred.")
                }
                return InternalDBResult.Success(null)
        }

        /**
         * @return Has no return value
         */
        fun removeSecurityQuestion(question: String, playerUUID: UUID): InternalDBResult<Nothing?> {
                try {
                        val connection: Connection = DriverManager.getConnection(url, username, password)
                        val query = """
                            DELETE FROM security_questions
                            WHERE question = ? AND player_uuid = ?;
                        """.trimIndent()
                        val statement = connection.prepareStatement(query)

                        statement.setString(1, question)
                        statement.setString(2, playerUUID.toString())

                        statement.executeUpdate()

                        statement.close()
                        connection.close()
                } catch (e: Exception) {
                        e.printStackTrace()
                        return InternalDBResult.Error("Internal exception")
                }
                return InternalDBResult.Success(null)
        }

        /**
         * @return Has no return value
         */
        fun setPassword(password: String, playerUUID: UUID): InternalDBResult<Nothing?> {
                var salt = CredentialsManager.genSalt(ConfigManager.hashLogRounds)
                var hash = CredentialsManager.hashString(password, salt, ConfigManager.generalPepper)
                try {
                        val connection: Connection = DriverManager.getConnection(url, username, DBManager.password)

                        val query = """
                         UPDATE users
                            SET password = ?, salt = ?
                            WHERE uuid = ?;
                        """.trimIndent()
                        val statement = connection.prepareStatement(query)

                        statement.setString(1, hash)
                        statement.setString(2, salt)
                        statement.setString(3, playerUUID.toString())

                        statement.executeUpdate()

                        statement.close()
                        connection.close()
                } catch (e: Exception) {
                        e.printStackTrace()
                        return InternalDBResult.Error("Couldn't create password, an internal error occurred.")
                }
                return InternalDBResult.Success(null)
        }

        /**
         * @return Boolean: Whether the player has a password set
         */
        fun hasPassword(playerUUID: UUID): InternalDBResult<Boolean> {
                var hasPassword = false
                try {
                        val connection: Connection = DriverManager.getConnection(url, username, DBManager.password)

                        val query = """
                         SELECT * FROM users
                         WHERE uuid = ? AND password IS NOT NULL
                        """.trimIndent()
                        val statement = connection.prepareStatement(query)

                        statement.setString(1, playerUUID.toString())

                        statement.executeQuery()

                        if (statement.resultSet.next())
                                hasPassword = true

                        statement.close()
                        connection.close()
                } catch (e: Exception) {
                        e.printStackTrace()
                        return InternalDBResult.Error("Internal exception")
                }
                return InternalDBResult.Success(hasPassword)
        }

        /**
         * @return Has no return value
         */
        fun removePassword(playerUUID: UUID): InternalDBResult<Nothing?> {
                try {
                        val connection: Connection = DriverManager.getConnection(url, username, password)
                        val query = """
                            UPDATE users
                            SET password = NULL, salt = NULL
                            WHERE uuid = ?;
                        """.trimIndent()
                        val statement = connection.prepareStatement(query)

                        statement.setString(1, playerUUID.toString())

                        statement.executeUpdate()

                        statement.close()
                        connection.close()
                } catch (e: Exception) {
                        e.printStackTrace()
                        return InternalDBResult.Error("Internal exception")
                }
                return InternalDBResult.Success(null)
        }

        /**
         * @return Boolean: Whether player is authenticated via Discord oauth2
         */
        fun isDiscordAuthed(playerUUID: UUID): InternalDBResult<Boolean> {
                var isAuthed = false
                try {
                        val connection: Connection = DriverManager.getConnection(url, username, DBManager.password)

                        val query = """
                         SELECT * FROM users
                         WHERE uuid = ? AND discord_hash IS NOT NULL
                        """.trimIndent()
                        val statement = connection.prepareStatement(query)

                        statement.setString(1, playerUUID.toString())

                        statement.executeQuery()

                        if (statement.resultSet.next())
                                isAuthed = true

                        statement.close()
                        connection.close()
                } catch (e: Exception) {
                        e.printStackTrace()
                        return InternalDBResult.Error("Internal exception")
                }
                return InternalDBResult.Success(isAuthed)
        }

        /**
         * @return Has no return value
         */
        fun setDiscordAuth(playerUUID: UUID, userID: String, username: String): InternalDBResult<Nothing?> {
                var salt = CredentialsManager.genSalt(ConfigManager.hashLogRounds)
                var hash = CredentialsManager.hashString(userID, salt, ConfigManager.discordPepper)
                try {
                        val connection: Connection = DriverManager.getConnection(url, DBManager.username, DBManager.password)

                        val query = """
                         UPDATE users
                            SET discord_hash = ?, discord_salt = ?, discord_username = ?
                            WHERE uuid = ?;
                        """.trimIndent()
                        val statement = connection.prepareStatement(query)

                        statement.setString(1, hash)
                        statement.setString(2, salt)
                        statement.setString(3, username)
                        statement.setString(4, playerUUID.toString())

                        statement.executeUpdate()

                        statement.close()
                        connection.close()
                } catch (e: Exception) {
                        e.printStackTrace()
                        return InternalDBResult.Error("Internal exception")
                }
                return InternalDBResult.Success(null)
        }

        /**
         * @return Has no return value
         */
        fun removeDiscordAuth(playerUUID: UUID): InternalDBResult<Nothing?> {
                try {
                        val connection: Connection = DriverManager.getConnection(url, username, password)
                        val query = """
                            UPDATE users
                            SET discord_hash = NULL, discord_salt = NULL, discord_username = NULL
                            WHERE uuid = ?;
                        """.trimIndent()
                        val statement = connection.prepareStatement(query)

                        statement.setString(1, playerUUID.toString())

                        statement.executeUpdate()

                        statement.close()
                        connection.close()
                } catch (e: Exception) {
                        e.printStackTrace()
                        return InternalDBResult.Error("Internal exception")
                }
                return InternalDBResult.Success(null)
        }

        /**
         * Retrieves the discord authentication details for a player from the database.
         *
         * @return DiscordAuthDetails?: The player's hashed Discord user ID, salt and Discord username if found in the database, null otherwise.
         */
        fun getPlayerDiscordAuthData(playerUUID: UUID): InternalDBResult<DiscordAuthData?> {
                var discordAuthData: DiscordAuthData? = null
                try {
                        val connection: Connection = DriverManager.getConnection(url, username, password)
                        val statement = connection.createStatement()
                        val query = """
                            SELECT * FROM users WHERE uuid = "$playerUUID" AND discord_hash IS NOT NULL
                        """.trimIndent()

                        val resultSet: ResultSet = statement.executeQuery(query)

                        if (resultSet.next()) {
                                val userIDHash: String = resultSet.getString("discord_hash")
                                val userIDSalt: String = resultSet.getString("discord_salt")
                                val username: String = resultSet.getString("discord_username")
                                discordAuthData = DiscordAuthData(userIDHash, userIDSalt, username)
                        }
                        else
                                discordAuthData = null

                        resultSet.close()
                        statement.close()
                        connection.close()
                } catch (e: Exception) {
                        e.printStackTrace()
                        return InternalDBResult.Error("Internal exception")
                }
                return InternalDBResult.Success(discordAuthData)
        }


        /**
         * Retrieves whether the user has sufficient 2FA set up as per the server's requirements.
         *
         * @return Boolean: True if user has sufficient 2FA set up, false otherwise or if user isn't found.
         */
        fun hasSufficient2FA(playerUUID: UUID): InternalDBResult<Boolean> {
                var hasSufficient2FA = false
                try {
                        val connection: Connection = DriverManager.getConnection(url, username, password)
                        val query = """
                                SELECT * FROM users 
                                LEFT JOIN security_questions ON users.uuid = security_questions.player_uuid
                                WHERE (users.uuid = ? AND 
                                        (users.password IS NOT NULL 
                                        OR users.discord_salt IS NOT NULL
                                        OR (SELECT COUNT(security_questions.question) >= 3 
                                                FROM security_questions WHERE security_questions.player_uuid = users.uuid)
                                        ))
    
                            
                        """.trimIndent()

                        val statement = connection.prepareStatement(query)

                        statement.setString(1, playerUUID.toString())

                        val resultSet: ResultSet = statement.executeQuery()

                        if (resultSet.next()) {
                                hasSufficient2FA = true
                        }

                        statement.close()
                        connection.close()
                } catch (e: Exception) {
                        e.printStackTrace()
                        return InternalDBResult.Error("Internal exception")
                }
                return InternalDBResult.Success(hasSufficient2FA)
        }

        /**
         * Database operation method wrapper.
         * Runs a database operation and handles any errors that occur.
         * Call with player if you want to display an error menu to them upon an error.
         *
         * @return If an error in the database operation execution occurs, returns null.
         * Otherwise, returns DBResult<T> where result is set to the result of the successful operation.
         */
        fun <T> runDBOperation(result: InternalDBResult<T>, player: Player? = null): DBResult<T>? {
                if (result is InternalDBResult.Error && player != null) {
                        MenuManager.displayErrorMenu(player, result.errorMessage)
                        return null
                }
                else if (result is InternalDBResult.Error) {
                        return null
                }
                //todo: print error to console

                if (result is InternalDBResult.Success)
                        return DBResult(result.result)
                throw IllegalArgumentException()
        }




}