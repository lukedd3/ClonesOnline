# ClonesOnline
ClonesOnline is an online multiplayer Android board game. It was developed oryginally as an university project, but eventually after many improvements I released it on Google Play. It grew up and for now it contains 14268 lines of code (without comments and libraries).

[![Clones Online Google Play link button](https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png)](https://play.google.com/store/apps/details?id=com.luke.Clones)

## Game Description

Clones Online is an multiplayer board game with simple rules. Every player has balls of his individual color. Aim of the game is to infect with your colour as much opponent balls as possible. You can do that by cloning or jumping your balls. Player with bigger number of his colour balls wins. Player can choose server and room to join or create his own room. There is also an option to play together on one device. There are game statistics and achievements to unlock.

![Clones Online screenshot 1](https://lh3.googleusercontent.com/KYcRlsFOKwL2u_hPD19P4eLIH9tJc_jcpmR9u_T2qiCjagZc72YArhTwIFk5_cxVLPM=h900)

![Clones Online screenshot 2](https://lh3.googleusercontent.com/zHO4YsntpAGUgMYCFwNfmUK0S7bFmzLcoe-2wQkhqXQCjmFWaStC6crIID6XFSJ5pNM=h900)

![Clones Online screenshot 3](https://lh3.googleusercontent.com/w1g_G5Zk_8HORQYxBCiGZ29Yj8ykBBpSkzSYg6xVk88I4UEIIesQLxwuoEZ3tqSNrtQ=h900)

![Clones Online screenshot 4](https://lh3.googleusercontent.com/llHkrJ7FAmJOjzg5q1MYMXekCGxQhPo8pCV9PtdatRmhQ6TprAZR6NBz4OE65keai7I=h900)![Clones Online screenshot 5](https://lh3.googleusercontent.com/qRUD63Hw_eIlCgcCFNzXkc2Yn9Mk6U__p4q1ykyHiKGfZE3SbR9R_P00OaDWQxEAzg=h900)

## License

This software is distributed under the MIT License (MIT).

More info could be found in the **LICENSE** file.


## Architecture

Clones Online was developed using Eclipse ADT. Used Android SDK version was 4.0.3. Main library used to develop this game was **Libgdx**. However there were more libraries included. 
> **List of included libraries (most important ones without logging libs, utils libs etc.):**
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


## Project import

Clones Online was made using Eclipse ADT. Everything is compiled and build using Eclipse tools and plugins thus it is impossible to import project to another IDE without doing some modifications. Below I will show how to import project step by step using Eclipse ADT.

**1. Prepare Eclipse ADT environment**
You should firstly install Eclipse ADT bundle (which contains Android SDK) or manually configure Eclipse together with Android SDK (which is worse option). Then you should configure your Android SDK by downloading all important libraries connected with Android 4.0.3. Also make sure that you have installed JDK and your Eclipse is properly configured to use it. Installation and configuration of these tools are topic for another instruction so if you have any troubles just search for it on the Internet.

**2. Clone project from github repo to your hard drive**
If you are a github user you should know how to do it. If you don't remember how to do it or you are new to github (and maybe also git) just google it.

**3. File->import in Eclipse ADT**
After launching Eclipse ADT you have to click File -> import and import window will pop up.

![](https://i.imgsafe.org/f85987c270.png)

**4.  Pick General->Existing Projects into Workspace option**
Pick General->Existing Projects into Workspace option and click next.

![](https://i.imgsafe.org/f85ee000bb.jpg)

**5.  Browse for project root directory and select all projects**
Click browse and select directory with the project from your hard drive. Then select all projects (you can use Select All button). After that just click Finish.

![](https://i.imgsafe.org/f8b4c224d6.png)

**5.  Clean Projects**
Often after import Eclipse shows many errors. Very often they aren't real errors and we can get rid of them using Clean command. Click Project->Clean, then select Clean all projects and click OK. You can make it several times. Sometimes also Project->Build All helps.

![](https://i.imgsafe.org/f8c0036f0b.png)

![](https://i.imgsafe.org/f8d259fa45.png)

**6. Resolve Android project errors**
Very often even after cleaning Clones-android project has errors. In fact it is usually Eclipse fault and there are no real errors. However we have to cope with that. Firstly we have to click Project->Build All to get rid off Android Manifest.xml error. 
Secondly open GetTokenTask.java and delete imports with errors. Then we need to click keys combination Ctrl + Shift + O to automatically organize imports. Then Ctrl + S to save file. Then we have to do the same for MainActivity.java. Select com.google.android.gms.ads.AdRequest, and com.google.android.gms.ads.AdSize when asked by automatic import. Then clean all projects as it was shown before. If MainActivity still has some errors remove imports with errors one more time, then automatically organize imports, save and clean all projects and eventually Project->Build All.

![](https://i.imgsafe.org/f97957ecbd.png)

![](https://s32.postimg.org/x595iswqt/Przechwytywanie16.png)

![](https://s31.postimg.org/9aa21107f/Przechwytywanie18.png)

**7. Enjoy code**
Everything should work now.

## Configuration

By default everything is configured in the way that application should be able to be run (with not all functions working) without any changes in config files. However sometimes it may be necessary to make changes in config files before first launch of server and client. What is more you have to modify config files in order to make all functions working. There are also config variables that aren't essential, but you might want to change them.

> **List of files with configuration variables:**
> 
> - **Clones**
>  - network -> NetworkConfig.java <-network configuration
>   - game -> GithubConnector.java, Config.java <- github connection configuration (bug report system), game preferences
> - **Clones-andorid**
>  - MainActivity.java <- ads show configuration
> - **Clones Server**
>  - Config.java <- Server port settings, Database connection configuration

**Files with configuration variables shown in Project Explorer:**

![](https://s32.postimg.org/wt24t0yhx/Przechwytywanie20.png)

![](https://s31.postimg.org/dxv86voyz/Przechwytywanie22.png)

## Running project in Eclipse ADT

## Code statistics

Some interesting facts about project code
