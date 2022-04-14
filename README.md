# Let'sChat Application
## Table of contents
* [General info](#general-info)
* [Architecture](#architecture)
* [Technologies](#technologies)
* [Demo](#demo)

## General Info
Chatting android application in which the user can create an account, add friends, search for friends with user name, accept/reject friend requests, and of course chat with friends in real-time.

## Architecture
### MVVM
![MVVM](https://user-images.githubusercontent.com/69528783/163326714-dd72ca23-7ce7-477f-92ae-afe0062b4606.png)
#### View

This layer contains the Activities, and Fragments. It takes the user events and gives them to the View Model layer to handle these events.

#### View Model
This layer receives user events from the View layer, then it gives them to the data layer waiting for the response. It also observes the exposed observables of the data layer. When the data is arrived, it puts that data in an observable that is exposed to the View.

#### Model
This layer contains the ResponseObjects, Data sources, and the Repository. It gets a request from the View Model layer, then it decides the correct data source with the help of the Repository. When it gets the data, it puts it in an exposed observable that is observed by the View Model layer.


## Technologies
* Kotlin
* ViewModel
* Live data
* Coroutines
* Room Database
* Hilt
* Navigation Component
* Glide
* DataStore


## Demo
Working on it.

