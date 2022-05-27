# Inbox-App

This project is part of my ongoing effort to develop my full-stack skills. Currently, it is using Thymeleaf, Spring Boot, 
Spring Security and Apache Cassandra. 

While I had learnt Spring Boot and related frameworks before, I had not yet practiced 
working on full-stack projects and this is my first time implementing one, though the UI is rather simple.
It is mostly focused on the backend development but improvements to the frontend will be coming soon.

I do not claim full credit for this project, Java Brains has an amazing guide on how to make a basic messaging app 
which I had followed the whole way in order to arrive at the current product.

You can find it here! 
<br/>
[Playlist for messaging app guide](https://www.youtube.com/playlist?list=PLqq-6Pq4lTTak0b5DnJ-x85MWMPaTdl4A)

That said, I will be
looking to improve on various aspects like UI and backend functionality.

## Instructions

1. In order to use this app, you will need to sign up for a free DataStax account to connect to Apache Cassandra
and create your own database.
2. At your dashboard, go to the Databases section, click on the hamburger menu icon for your database of choice and
select *Generate a Token*
3. Select role as *Administrator User* and generate the token.
4. **Save** the token details in a secure location because you will not be able to view the details after you navigate
away from the page.
5. Next, we will need authentication details for this app.
6. Go to Settings -> Developer settings and create a new GitHub App.
7. **Save** your client secret because you will not be able to view your secret after the page closes. It is recommended
to save all the important details in the same secure location so for ease of access and security.
8. Now, we have all the information we need in order to use the app properly.
9. The port can be any port that you want.
10. Authentication will be made through your GitHub account details, so *client-id* and *client-secret* will be the 
corresponding values for your GitHub app.
11. In order to connect to the database, you will need to provide the token details given earlier.
12. The keyspace name will be the name of the keyspace that you are using for your database.
13. The username and password will be the client id and client secret given in the token details respectively.
14. The token will be the token given and the rest of the details for *astra-db* will be displayed at the
database section of your dashboard.
15. That's it! You should now be able to use the app.

## Upcoming features
- [ ] Re-design UI with React
- [ ] Use Colour field in Folder.java
- [ ] Allow users to move emails to any folder
- [ ] Allow users to create custom folders
- [ ] Allow users to delete email
- [ ] Implement search functionality for emails

### Credits
Java Brains - Detailed and easy guide for a messaging app