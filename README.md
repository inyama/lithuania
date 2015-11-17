# lithuania
This program uses basic authorisation. Each request should be with special field in http header. For example

curl -H "Authorization: Basic <long encoded string>" http://localhost:8088/users" where "long encoded string"
is Base64 encoded string with username and password

Repo contains basic integration unit tests.

Server returns data in a special format

{
"success":true/false,
"error":<error text here>
"data":<response data>
}

If there are success=true, then error field is empty and data contains response information.
If success = false then error field contains error code and data is empty.

JSON api consist of 4 main groups. Each part has special entity formats. This formats help us to get and modify data.

/votes   - provides information about votes. allows user to vote on restaurant.

This part works with:

VoteEntity
{
"id":<int>
"voteDate":<long>
"userId":<int>
"restaurantId":<int>
}

We put post and get votes in this format.

/votes PUT - allows to modify today's vote if it were done before 11. it will not change vote time

/votes POST - allows to vote.

/votes GET - returns a list of all today votes. list of voteEntity

/votes/{voteId} GET - returns concrete vote

/votes?currentTime ={time} GET - returns all vote records for specified time

/votes?restaurantId={rId} GET -returns all vote records for pecified restaurantId for today

/votes?restaurantId={rId}&currentTime ={time} GET - returns all vote records for pecified restaurantId for currentTime time

/votes?userId={rId}&currentTime ={time} GET - returns all vote records for user with given userId for for currentTime time

/votesStatistic - provides vote statistics. how much votes has each restaurant


This part works with
StatisticItem:
{
"votesCount":<int>
"restaurantId":<int>
}

/votesStatistic GET - returns vote statistics for today as a list of statisticItem

/votesStatistic?restaurantId={rId}&currentTime={time} GET - returns vote statistics for specified time as a list of statisticItem

/users - provides functional for user management. helps us to create remove and update user information

This part works with
UserEntity:
{
"name":<string>
"password":<string>
"id":<int>
"role":ROLE_USER/ROLE_ADMIN
}


/users GET - returns all users. only admins can view users passwords non admin users will have null in password field

/users POST - allows to create new users. only ADMIN users can create new once.

/users/{id} GET - returns data for user with given id. only admins and account holders can view passwords.
non admin users will have null in password field

/users/{id} PUT - allows to modify user information. only ADMIN users can do this

/users/[id} DELETE - allows to delete user records. only ADMIN users can do this

/restaurants - provides restaurant management functional.

This part of service works with two different entity types

for restaurant/
RestaurantEntity
{
"id":<int>
"name":<string>
}

and for restaurant/{id}/menu
MenuEntity

{
"id":<int>
"restaurantId":<int>
"time":<long>
"price":<int>
"name":<string>
}

/restaurants/{id} DELETE - allows admin users to delete restaurant with specified id

/restaurants/{id} PUT- allows admin users to modify restaurant data with specified id

/restaurants POST - - allows admin users to create new restaurant record

/restaurants/{id} GET - returns data for restaurant with specified id

/restaurants GET - return list of all restaurants


/restaurants/{id}/menu/{menuId} DELETE - allows admin users to delete menu item with specified id

/restaurants/{id}/menu POST - allows admin users to create new menu record for today. when you post new menu item server
will automatically fill time field

/restaurants/{id}/menu GET - returns all menu records for today

/restaurants/{id}/menu?currentTime={time} - returns all menu records for given time


