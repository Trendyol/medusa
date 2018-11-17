<img src="https://raw.githubusercontent.com/Trendyol/medusa/master/art/medusaicon.png"/>

Fragment manager for your Single Activity - Multi Fragment app. 

Medusa is smart enough to handle fragment attachment/detachment or hiding/showing fragment.  No more memory handling. No more fragment stack logic.

## Why? ##

Single activity and Multi Fragment structure is new trend and reasonable to choose. Most of the popular apps like Instagram, Youtube, Spotify etc. are using Single Activity with bottom bar and controlling 
multiple fragment in that single activity. 

In our [Trendyol.com](https://play.google.com/store/apps/details?id=trendyol.com) android application, we are also using Single Activity- Multi Fragment UI Structure. Everything seems fine when you decide using Single Activity- Multi Fragment, except fragment stack and controlling them. 

It was always hard to manage fragment manager in our application. We are using bottom bar and user have his history in every bottom bar tab. For instance, In Home tab, use can goes 4-5s fragment deep in home tab. And then user switches to the another tab and goes 3-4 fragment in that tab too. 

So what happens when user switch back to home fragment? Which fragment should be visible to user? What happens when user starts going back? Which fragments will be visible to user in which order while user is pressing back button? What should happen? Yes this is confusing. 

This is why we create medusa. It is not confusing anymore. Medusa keeps track of fragment history of every tab and you only call start, goBack to navigate your fragments. 

## Wiki ## 
Check out our [wiki](https://github.com/Trendyol/medusa/wiki)!
* [Basic usage](https://github.com/Trendyol/medusa/wiki/Basic-Usage)
* Coming soon..

## Icon Credits
All credits goes to [Tatiana](https://dribbble.com/DarumaCreative)
