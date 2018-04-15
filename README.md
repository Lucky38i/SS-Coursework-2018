# SS-Coursework-2018
[![Build Status](https://travis-ci.com/Lucky38i/SS-Coursework-2018.svg?token=bqyyqGbK2qGAr9uo5heB&branch=master)](https://travis-ci.com/Lucky38i/SS-Coursework-2018)


The assignment is about delivering a graphical social network system with a centralised server implementing a
Spotify-like service. The clients connect to the “Music Social Network” server to register new members to the social
network system and upload their profile in terms of “music preferences” (e.g. opera, pop music, rock music, and so
on), personal photo and a set of personal information such as place of birth, place of residence, sentimental state of
the corresponding new social network member. Moreover, each client uploads to the server, at the time of
registration or subsequently, the songs (mp3 files) that it intends to share in the social network. After the registration
step, the new member is enabled to send new friendship requests, post new messages to the social network, and
listen to the songs shared by his friends. Only people that are friends among them can see their respective posts and
listen their respective music. Moreover, social network friends can chat among them by means of the additional
“Chat” server. In particular, the client can ask to the Chat server to act as a bridge to connect it to its friends and
start a new chat session.
The “Music Social Network” server accepts the requests from clients and adds the information related to each
request to a data structure containing the name of the new member, the related profile (information and music files)
and the IP address of the client. This data structure can be stored in a database in order to share it with the Chat
server. Once the “Music Social Network” server completes a new member registration, it sends messages containing
the list of current members to the clients. The “Music Social Network” server will send an updated list of current
members to the clients every two seconds in order to allow each client to always know who are the people connected
to system and enable it to send new friendship requests. Moreover, the “Music Social Network” server has to show,
to each client, the list of songs that the client is enabled to listen, i.e., the songs shared by its friends. In addition, the
Social Network server must forward posts uploaded by other members to the eligible members. The client uses a
graphical window to show the list. The “Music Social Network” server can store the information about friendships
in the same data structure used to manage member registration.
The social network members can contact their friends by starting a chat session by means of the Chat Server. In
particular, the member can contact a friend directly by clicking on the name, or can perform a profile-matching task
to search the set of friends with a particular profile. During the chat session, clients can exchange text, files and
images.
You must implement the client-client and client-server communication and manage all of the music social network
functionalities. A client connecting to the system should be able to: obtain (from the server) a list of who is currently
online; select a person to start a conversation; send text messages to the selected person; listen songs shared by its
friends.

Credits to:
Alex McBean
N0696066

# Declaration of Ownership 

I, Alex McBean (N0696066) herby declare that am I the sole author of the associated report and software exclusive of the software declared below.

I am aware of the University’s rules on plagiarism and collusion and I understand that, if I am found to have broken these rules, it will be treated as Academic misconduct and dealt with accordingly.

I understand that I must submit this coursework by the time and date published.

I understand that it is up to me and me alone to ensure that I submit my full and complete coursework and that any missing elements submitted after the deadline will be disregarded.

I understand that the above rules apply even in the eventuality of computer or other information technology failures.

# Libraries used used


# Code Sourced
