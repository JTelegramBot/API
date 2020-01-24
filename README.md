# Java Telegram Bot API

This is a Telegram Bot API for Java. At the time of writing it supports all the features of the HTTP API and follows the same specification. This is a work in progress and currently has very minimal error handling and testing. Any methods or functionality outside of `pro.zackpollard.telegrambot.api.internal` package should remain as they are currently and should work as expected, anything inside the `internal` package is not guaranteed to work as expected and may yield unexpected results if used in a way that is not intended.

## Documentation

JavaDocs https://zackpollard.github.io/JavaTelegramBot-API  

## Support

Telegram Channel for this API available at https://telegram.me/javatelegrambotapi   
Please join this channel for information about updates to this bot.   
You can also contact me at https://telegram.me/zackpollard   
If you need help in Persian(فارسی) you can contact https://telegram.me/aaomidi

## Source Code
Source is stored on GitLab and mirrored to Github   
[![Repo on GitHub](https://img.shields.io/badge/repo-GitHub-3D76C2.svg)](https://github.com/zackpollard/JavaTelegramBot-API)   
[![Repo on GitLab](https://img.shields.io/badge/repo-GitLab-6C488A.svg)](https://git.zackpollard.pro/personal-projects/open-source/telegram-bots/JavaTelegramBot-API)   

## Example Usage
You can find fully working examples of bots in the following repository   
https://github.com/zackpollard/JavaTelegramBot-API-Examples   
   
The following is a simple overview of how the API works as a kind of quick start guide:
```java

//Main Class
public class MyBot {

    public MyBot() {
    
        TelegramBot telegramBot = TelegramBot.login("APIKey");
       
        //The API key was invalid, an error will have also been printed into the console.
        if(telegramBot == null) System.exit(-1);
        
        telegramBot.getEventsManager().register(new MyListener());
        
        //This will tell the API to start polling the servers for updates
        //If you specify true as the argument you will receive any previous messages before the bot started.
        //If you specify false the API will discard any messages from before the bot was started.
        telegramBot.startUpdates(false);
        
        //Thread would die, do something to keep it alive.
    }
}

//Listener class
public class MyListener implements Listener {

    public void onTextMessageReceived(TextMessageReceivedEvent event) {

        event.getChat().sendMessage(SendableTextMessage.builder().message("You sent me a text based message!").replyTo(event.getMessage()).build(), telegramBot);
    }
}
```
## Deployment

The API is available on Github Package Registry.
### Maven
Maven is used for dependency management and deployment.
```xml
<dependencies>
    <dependency>
        <groupId>pro.zackpollard.telegrambot.api</groupId>
        <artifactId>jtelegram-botapi</artifactId>
        <version>1.7.1</version>
    </dependency>
</dependencies>
```

The following is required temporarily as github package registry can't be used without authentication, please follow the instructions on the following issue:
https://github.com/zackpollard/JavaTelegramBot-API/issues/93#issuecomment-577789861

## Licensing

This project is licensed under the GPLv3 licence.
