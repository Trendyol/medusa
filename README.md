<img src="https://raw.githubusercontent.com/Trendyol/medusa/master/art/medusaicon.png"/>

Fragment manager for your Single Activity - Multi Fragment app. 

[![](https://img.shields.io/nexus/r/com.trendyol/medusa?server=https%3A%2F%2Foss.sonatype.org%2F)](https://oss.sonatype.org/#nexus-search;gav~com.trendyol~medusa~~~~kw,versionexpand)
[![](https://img.shields.io/github/actions/workflow/status/Trendyol/medusa/pull-request.yml?branch=master)](https://github.com/Trendyol/medusa/actions/workflows/pull-request.yml?query=branch%3Amaster)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Medusa is smart enough to handle fragment attachment/detachment or hiding/showing fragment.  No more memory handling. No more fragment stack logic.

## Why? ##

Single activity and Multi Fragment structure is a new trend and reasonable to choose. Most of the popular apps like Instagram, Youtube, Spotify etc. are using Single Activity with bottom bar and controlling 
multiple fragments in that single activity. 

In our [Trendyol.com](https://play.google.com/store/apps/details?id=trendyol.com) android application, we are also using Single Activity- Multi Fragment UI Structure. Everything seems fine when you decide using Single Activity- Multi Fragment, except fragment stack and controlling them. 

It was always hard to manage fragment manager in our application. We are using bottom bar and user have his history in every bottom bar tab. For instance, In Home tab, user can goes 4-5 fragments deep in home tab. And then user switches to the another tab and goes 3-4 fragment in that tab too. 

So what happens when user switch back to home fragment? Which fragment should be visible to user? What happens when user starts going back? Which fragments will be visible to user in which order while user is pressing back button? What should happen? Yes this is confusing. 

This is why we create Medusa. It is not confusing anymore. Medusa keeps track of fragment history of every tab and you only call start, goBack to navigate your fragments. 

## Wiki ## 
Check out our [wiki](https://github.com/Trendyol/medusa/wiki)!
* [Basic usage](https://github.com/Trendyol/medusa/wiki/Basic-Usage)
* [Fragment lifecycle with Medusa](https://github.com/Trendyol/medusa/wiki/Fragment-Lifecycle)
* [Listeners](https://github.com/Trendyol/medusa/wiki/Listeners)
* [Grouping fragments](https://github.com/Trendyol/medusa/wiki/Grouping-Fragments)

## Setup
```gradle
dependencies {
    implementation 'com.trendyol:medusa:<latest-version>'
}
```

## Icon Credits
All credits go to [Tatiana](https://dribbble.com/DarumaCreative)

License
--------


    Copyright 2018-2023 Trendyol.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.




