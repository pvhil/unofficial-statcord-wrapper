# unofficial-statcord-wrapper
My own java Wrapper to automatically post stats to Statcord with java

## How to use it.

Last version: [jitpack.io](https://jitpack.io/#pvhil/unofficial-statcord-wrapper)

**Maven**
```xml
<dependency>
  <groupId>com.github.pvhil</groupId>
  <artifactId>unofficial-statcord-wrapper</artifactId>
  <version>VERSION</version>
</dependency>

<repositories>
  <repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
  </repository>
</repositories>
```

```java
Statcord.start(id, key, jda, autopost, timerInMin);
```
What are these parameters?  
| Parameter        | Type           | Meaning |
| ------------- |:-------------:|:-------------:| 
| id      | String | The ID of your Bot |
| key      | String      |  Key from Statcord |
| jda | JDA      | The JDA instance (mostly named jda)|
| autopost | boolean      | allow autoposting stats every hour |
| timerInMin | int      | Interval between automatic updates |

To count commands and usage of them you have to use
```java
Statcord.commandPost(command, author);
```
in every *command section* of your bot

'command' means the command (like !help) (String)  
'author' is the ID from the Bot User / Message author (String)

For custom graphs use this:
```java
Statcord.customPost(id, content);
```
id can only be 1 or 2  
'content' is a String.
```java
Statcord.updateStats();
```
This boy manually updates stats.

# Example

```java
// Starting the Bot
public static void main(String[] args) throws LoginException {

        jda = JDABuilder.createDefault(BOTTOKEN)
                .addEventListeners(new DiscordMessage())
                .build();
                
ClassWithThread classWithThread = new ClassWithThread();
classWithThread.start();
}

// A Command
public void onMessageReceived(MessageReceivedEvent event) {
  java.lang.String[] args = event.getMessage().getContentRaw().split("\\s+");
  if (args[0].equalsIgnoreCase("help")) {
    event.getChannel.sendMessage("message").queue();
    Statcord.commandPost("help", event.getAuthor().getId());
    }
  }
```
**Since the code is stopping the thread, you need to run it in another thread!**
```java
public class ClassWithThread extends Thread {
  @Override
  public void run() {
    try {
      Statcord.start(
          BotStart.jda.getSelfUser().getId(), 
          Config.getStatcrord(), 
          BotStart.jda, 
          true, 
          5);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
```

## Important!
This code is still in alpha and not finished. Everything *should* work but bandwidth is not working because i can not find a way to get the usage.  
Because this thing is still in developement i will not take any responsibility by using this code. Right now, I will accept any help but please look at the development branch first! :)  

This code is a third-party application and not affiliated with [Statcord.com](https://Statcord.com)

## Help please!
I do not know much about sharding. I would love you if you could tell me how i can fetch metrics from shards :) Thanks
