# Cowin Vaccine Availability
## _Get notified for custom alerts_

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://github.com/5-k/CowinAvailability)

Cowin Availability is an open source service which can be used to be notified for custom alerts setup by user for vaccine availability.

Register to alerts: https://docs.google.com/forms/d/e/1FAIpQLSfdQ6xSxK-mmnrg_Tz72Acc2jIo1D3I833k4NuAOV-ZOE4x3Q/viewform  or signup on telegram https://t.me/Cowin_Availability_Slot_bot

It's a web project based on Java Spring boot, and hosted on Azure.

- Custom unlimited alerts
- Choice of notification
- ✨Magic ✨

## Features

- Register to get alerts via google forms
- Register via chatbot on Telegram
- Create multiple custom alerts,
- Notified over email, whatsapp, telegram and SMS
- Reusable components

Let's use this application and get vaccinated soon 

> Hope is important because it can make the present moment less difficult to bear
> If we believe that tomorrow will be better, we can bear a hardship today.

## Tech

Following tech was used.

- [Java11] - Core Java
- [Spring 2.x] - Spring web and core
- [Maven] - Build Manager.
- [SQL Server] - SQL Database

And of course is open source with a [https://github.com/5-k/CowinAvailability]  on GitHub.


## Integrations

Following integrations was used.

- [Whatsapp Business API] - Support using Facebook Business Manager with Service Provider twilio
- [Telegram Open API] - Helps in creating chatbot service, with client id and provides a web hook to continiously monitor the chats
- [SMS] - Paid SMS Service with .twilio Service account
- [Gmail Email] - Open Source javax.net and Google Email implementation

## Installation

Application uses [Java 11](https://www.oracle.com/in/java/technologies/javase-jdk11-downloads.html) v11+ to run.

Install the dependencies and devDependencies and start the server.

```sh
cd cowinAvailibility
mvn clean install
mvn spring-boot:run
keytool -genkey -alias cowin-avlb -storetype JKS -keyalg RSA -keysize 2048 -validity 365 -keystore cowin-avlb.jks
keytool -importkeystore -srckeystore cowin-avlb.jks -destkeystore cowin-avlb.jks -deststoretype pkcs12
```


## Plugins

This applicable is currently integrated with the following plugins.
Instructions on how to use them in your own application are linked below.

| Plugin | README |
| ------ | ------ |
| Telegram | [https://core.telegram.org/][TelegramAPI] |
| Telegram Apps | [https://telegram.org/apps][TelegramApps] |
| Twilio Console | [https://www.twilio.com/console][PlGh] |
| Twilio Docs | [https://www.twilio.com/docs][PlGd] |
| Facebook Business Account | [https://business.facebook.com/][PlOd] |

## Development

Want to contribute? Great!

You can contibute by forking the repository create pull request on user branches
Also you can contact me at my linkedin profile.
https://www.linkedin.com/in/prateek-mishra-61aa4658/ 


## License

MIT

**Free Software, Hell Yeah!**

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [TelegramAPI]: <https://core.telegram.org/api/requests>
   [TelegramApps]: <https://telegram.org/apps>
   [PlGh]: <https://www.twilio.com/console>
   [PlGd]: <https://www.twilio.com/docs>
   [PlOd]: <https://business.facebook.com/>
