# To do app
</br>
This is a simple app where you can create to do lists and add items within every list.


</br></br>
## Description
</br>
The app is a school project in the subject IKT205 - application development at the University of Agder, Grimstad. It is written in Android Studio, Kotlin. The app uses Firebase realtime database with email/password authentication.


</br></br>
## Dummy account for easy testing
</br>
For easy testing use the following account:
</br>
Email address:	todouser1@gmail.com

Password:			**todouser111111**

Use same password with username in app.


</br></br>
## Link for APK download via Firebase App distribution
</br>
https://appdistribution.firebase.dev/i/8ce95a62a597f51f


</br></br>
## App content and features
</br>

#### App icon
</br>

<img src=".\screenshots\app_icon.jpg" alt="app_icon"  width="30%" height="30%" /> 


</br></br>
#### Login and register new user
</br>
To use the app you need to have an user account and verified email address. The storage of the to do lists in Firebase is stored under a unique user ID that is associated with the email you register with. The login activity gives the user the options to log in, get email to set new password if forgotten and register and sign up. If the user already has an account and has logged in previously, the user is forwarded directly to their to do lists. If the user previously logged in, the app can be used as usual even if the user does not have an internet connection. With the exception of editing the user profile. When connected to the internet again, the app synchronizes changes to the database.

The register activity checks that the user fills in all the fields, email format is correct, password doesn't contain whitespace and that the passwords match. The app then sends the user a verification mail to the registered email address.

<img src=".\screenshots\login.jpg" alt="login"  width="30%" height="30%" /> <img src=".\screenshots\register.jpg" alt="register" width="30%" height="30%" />


</br></br>
#### To do lists
</br>
The lists is shown with cards inside a scroll view. The floating action button is for adding new lists that triggers a alert dialog where the users can enter new list name or cancel.

The cards have a progress bar which shows progress of checked items in the list and an itembutton for deleting the list. The delete feature has given me a lot of headaches because the values for setting and updating the progress bar are connected to eventListeners in list items activity. I got help for a workaround for getting the delete feature to work, but this caused the progress bars and item deletion to malfunction. The solution to this problem I chose is to promt the user to delete all items in the list before deleting the list. This is not the most user friendly solution but deletion works fine if the list is empty.

The toolbar has two itembuttons for navigating to "my profile" activity and for changing to night mode theme.

<img src=".\screenshots\taskList_light.jpg" alt="taskList_light" width="30%" height="30%" /> <img src=".\screenshots\taskList_dark.jpg" alt="taskList_dark" width="30%" height="30%" />


</br></br>
#### To do items
</br>
The extended floating action button expands and contain buttons for adding a new item and deleting all items. The progressbar updates dynamically when the checkboxes are clicked.

<img src=".\screenshots\taskItems_light.jpg" alt="taskItems_01" width="30%" height="30%" /> <img src=".\screenshots\taskItems_light_fab.jpg" alt="taskItems_02" width="30%" height="30%" />


</br></br>
#### My profile
</br>
Here the user can edit the password, change the associated email address, delete their account or log out. Associated items are initially disabled until the user authenticates their account.

<img src=".\screenshots\my_profile_authenticate.jpg" alt="my_profile_authenticate" width="30%" height="30%" /> <img src=".\screenshots\my_profile_authenticated.jpg" alt="my_profile_authenticated" width="30%" height="30%" />



#### Extra information
</br>
For all fields for user input there are added checks for required fields and also valid and correct input for each field. For saving a new lists and list items, input are checked so that it doesn't contain characters that are incompatible entries in the database. These features are displayed with alert dialogs which has "save/ok" and "cancel" buttons. The app returns toasts for all input errors.

<img src=".\screenshots\database.jpg" alt="database" width="30%" height="30%" /> 
