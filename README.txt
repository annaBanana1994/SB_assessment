#Set up

Default Data includes set up information as well as default messages.

server.port=9090

http://localhost:9090/round_up/transfer
With request body accessToken as String in JSON type
(example assessment/src/main/resources/accessToken)

For testing Json files are used for the responses, directory path can be set in TestData as userDirectory

There is a JavaDoc to help with viewing overall structure but further documentation has not been done


#Assumptions
Use first in list of accounts (Unsure  because wasn't sure on how were connected or how to handle, ERD would have helped
with that. If multiple could be present can introduce loop but would have to adjust response messages
Using GMT
Added feature to unsure correct savings account used added a naming convention, savings account names with round up
services need the name "Future Adventures". If there is not one, one is created, if there is multiple the last will be used

#Notes

* Started with creating the different layers of the application, bearing in mind achieving loose coupling

* Struggled to understand the structure of accounts. Account holder can have a list of accounts, and each of those accounts can
have various 'accounts', ie. personal and savings.
The savingsGoalUid is the alternative category id, along with the default category id

* Had 'amount' of each feed item as string as last two characters will always be the moduli when dividing by 100. Might
not be good as doesn't use math so changed back to int.

* Why I did last time stamp - because not automated to go weeekly yet so when manually triggered it provides an indication

* Need to work on what exceptions can be thrown when and what can go wrong

* Would like to improve the building of request entity

* Used random generation for UUID, very low chances they wouldn't be unique

* To me the requirements were't clear enough at the start to do TDD. I figured out and made assumptions as I went along.
For me mapping out before the required payloads, models and an ERD is so helpful. Would like to improve on thinking of
scenarios to test

* Lots of unfinished/not resilient methods

* Break methods down more

* Did not want to create model classes for all of the payloads and response, used Json node to extract relevant data. This
meant during testing it took along time to get my head around how to mock and ensure services working correctly

*Have left a few unused classes incase of future ideas ie. ClientStub

#Uncertanties

* Unsure about all methods being public/private in services impl due to using interface

* Unsure of terminology/naming conventions with regards to calling external API

* Separating the model classes to keep simple, not clear such as entity or payload

* global logger variable in each class.. is it necessary?

* Unsure if finer testing of internal logic in methods is required, but did not know how to access the variables to test

#Continued work:

* Improve building requests and reading responses from external API

* Handle last transaction being out
(Quite a lot of notes in the code that I would like to handle/create logic for)

* Need to try and break - some of bad scenarios not being handeled

* Integration tests

* Break methods down more

* Not have full directory path when opening a file

* Weekly automated trigger - but time reference and start, would not know where to start