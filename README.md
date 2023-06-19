# Fancy2FA

This was a learning project of mine, which helped me learn how to work with MySQL, Kotlin, NMS, ktor and the Discord API without using an API wrapper.
Don't use this in production, as it wasn't tested for vulnerabilities and might have exploits.

https://github.com/Gameoholic/Fancy2FA/assets/30177004/1ccd3e9b-e7dc-4e9d-b0c2-97a4a9733c0d


# Features
+ Authenticate yourself via Discord, security questions or a password, all of which are hashed, salted and peppered, and stored in a MySQL database.

+ Fancy UI menus

![image](https://github.com/Gameoholic/Fancy2FA/assets/30177004/74dd7cb9-acb3-4ff8-99af-03d5b3b74ea8)
![image](https://github.com/Gameoholic/Fancy2FA/assets/30177004/707cdfcb-2917-4266-9ba0-1e6082b1f358)
![image](https://github.com/Gameoholic/Fancy2FA/assets/30177004/388be0f7-7cc0-44a1-a5e3-b4bee3c31e8d)

+ Written 100% in Kotlin (yes, this is a feature)


# Ideas / Issues
Things that weren't implemented due to time constraints, but were still cool ideas I had planned:
+ Persistent authentication: Logging IP's in the database to allow users to only enter their credentials once every 30 days, or when their IP changes.
+ Automatically create a database for the user through code and set up all the tables.
+ Config file error handling.
+ Password confirmation.
+ Discord OAuth2 shouldn't be used for user authentication - better to use an actual Discord bot.
