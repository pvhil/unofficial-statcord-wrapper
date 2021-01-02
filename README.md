# unofficial-statcord-wrapper
My own java Wrapper to automatically post stats to Statcord with java

## How to use it.
Compile or download the jar file and add it as an external library/dependency in your java project. You need to use JDA!  
After you started your bot, use this line of code to start the code:
```java
Statcord.start(id,key,jda,autopost)
```
What are these parameters?  
| Parameter        | Type           | Meaning |
| ------------- |:-------------:|:-------------:| 
| id      | String | The ID of your Bot |
| key      | String      |  Key from Statcord |
| jda | JDA      | The JDA instance (mostly named jda)|
| autopost | boolean      | allow autoposting stats every minute |

To count commands and usage of them you have to use
```java
Statcord.commandPost(command,author)
```
in every *command section* of your bot

'command' means the command (like !help) (String)  
'author' is the ID from the Bot User / Message author (String)

## Important!
This code is still in alpha and not finished. Everything *should* work but metrics like ram, cpu, custom things and bandwidth are not working.  
This code is a third-party application and not affiliated with Statcord.com.
