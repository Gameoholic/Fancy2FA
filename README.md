# Fancy2FA

This was a learning project of mine, which helped me learn how to work with MySQL, Kotlin, ktor and the Discord API without using an API wrapper.
Don't use this in production, as it wasn't tested for vulnerabilities and might have exploits.

# Features
Authenticate yourself via Discord, security questions or a password, all of which are hashed, salted and peppered, and stored in a MySQL database.

Fancy UI menus

Written 100% in Kotlin (yes, this is a feature)


# Ideas / Issues
Things that weren't implemented due to time constraints, but were still cool ideas I had planned:
+ Persistent authentication: Logging IP's in the database to allow users to only enter their credentials once every 30 days, or when their IP changes.
+ Automatically create a database for the user through code and set up all the tables.
+ Config file error handling.
+ Password confirmation.
+ Discord OAuth2 shouldn't be used for user authentication - better to use an actual Discord bot.
