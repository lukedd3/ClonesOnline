# ClonesOnline
ClonesOnline is an online multiplayer Android board game. It was developed oryginally as an university project, but eventually after many improvements I released it on Google Play.

[![Foo](https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png)](https://play.google.com/store/apps/details?id=com.luke.Clones)

Description

Clones Online is an multiplayer board game with simple rules. Every player has balls of his individual color. Aim of the game is to infect with your colour as much opponent balls as possible. You can do that by cloning or jumping your balls. Player with bigger number of his colour balls wins. Player can choose server and room to join or create his own room. There is also an option to play together on one device. There are game statistics and achievements to unlock.

Screenshots

![enter image description here](https://lh3.googleusercontent.com/KYcRlsFOKwL2u_hPD19P4eLIH9tJc_jcpmR9u_T2qiCjagZc72YArhTwIFk5_cxVLPM=h900)

![enter image description here](https://lh3.googleusercontent.com/zHO4YsntpAGUgMYCFwNfmUK0S7bFmzLcoe-2wQkhqXQCjmFWaStC6crIID6XFSJ5pNM=h900)

![a](https://lh3.googleusercontent.com/w1g_G5Zk_8HORQYxBCiGZ29Yj8ykBBpSkzSYg6xVk88I4UEIIesQLxwuoEZ3tqSNrtQ=h900)

![a](https://lh3.googleusercontent.com/llHkrJ7FAmJOjzg5q1MYMXekCGxQhPo8pCV9PtdatRmhQ6TprAZR6NBz4OE65keai7I=h900)![a](https://lh3.googleusercontent.com/qRUD63Hw_eIlCgcCFNzXkc2Yn9Mk6U__p4q1ykyHiKGfZE3SbR9R_P00OaDWQxEAzg=h900)

## Architecture

Clones Online was developed using Eclipse ADT. Used Android SDK version was 4.0.3. Main library used to develop this game was **Libgdx**. However there were more libraries included. 
> **List of included libraries (most important ones without logging libs, uttils libs etc.):**
> 
> - **Libgdx** <- Main library. Java game development framework.
> - **KryoNet** <- Network communication and serialization Java library. Often used together with Libgdx.
> - **Google Play Services** <- Google API used to authenticate user connecting to server using Google account (on Android device)

Clones Online consists of many projects. They are representing important parts of whole project like **client core**, **game server** etc. Game can be dived into:
> **ClonesOnline important partial projects:**
> 
> - **Clones** <- Project of core client application
> - **Clones-andorid** <- Project used to Build and Launch client Android version
> - **Clones-desktop** <- Project used to Build and Launch client desktop (PC) version (useful for testing)
> - **ClonesServer** <- Project of game server which handles online mode
> - **ClonesBot** <- Project of AI Bot which could be run on desktop (PC) to simulate player on server

> **ClonesOnline not important partial projects:**
> 
> - **Clones-html** <- Project used to Build and Launch Web GL version - not used, has errors.
> - **Facebook SDK** <- Used as a library for other projects
> - **google-play-services_lib** <- Used as a library for other projects
