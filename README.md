# Shows App - Kristina Koneva
This project represents an Android application
used for listing and watching TV shows which will be developed
during the Infinum Academy Android Course. <br /> <br />
The application will be built using Kotlin as the language of choice,
Android Studio as the official IDE and MVVM as the application architecture. <br /> <br />
Further information about the project will be provided when new features are implemented.

## Login Screen
<p align="left">
<img src="README_images/login_screen_without_input.png" width="30%" height="30%"/>
&nbsp; &nbsp; &nbsp; &nbsp;
<img src="README_images/login_screen_with_input.png" width="30%" height="30%"/>
</p>
The application starts with the login screen where the user is required to enter an email and a password to continue. <br /><br />
The email and password must be inputted in the correct format, otherwise the LOGIN button is disabled, an error message is displayed and the user cannot continue to the next screen. 
An example of a correct email format is: username@gmail.com and the password must contain at least 6 characters. <br /> <br />
A click on the LOGIN button takes the user to the next screen - the shows screen.

## Shows Screen
<p align="left">
<img src="README_images/shows_screen.png" width="30%" height="30%"/>
&nbsp; &nbsp; &nbsp; &nbsp;
<img src="README_images/shows_screen_empty_state.png" width="30%" height="30%"/>
</p>
This is the Shows screen where a list of shows is displayed as seen in the first image. <br /> <br />
If the user click on a show card, a new screen is shown where details about that particular show are displayed. <br /> <br />
When the user clicks on the SHOW/HIDE EMPTY STATE button several times, 
depending on the previous state, the shows empty state will be shown/hidden accordingly. 
The design of the empty state screen is shown in the second image <br /> <br />
On the top right corner, the user's profile photo is shown and when it is clicked, it opens a bottom sheet dialog whose design is shown in the following photo: <br /> <br />
<img src="README_images/profile_photo_dialog" width="30%" height="30%"/> <br /> <br />
In the bottom sheet dialog, the user's profile photo and email are displayed. Below them, there are two button: one for changing the profile photo and one for logging out. <br /> <br />
If the user click on the button - Change profile photo, 
a new bottom sheet dialog is opened where the user can choose whether to change the photo 
by taking a photo directly from the Camera app or choose a photo from his/her gallery. This bottom sheet dialog is shown below: <br /> <br />
<img src="README_images/change_profile_photo_method" width="30%" height="30%"/> <br /> <br />
If the user clicks on the logout button from the previously shown bottom sheet dialog, an alert dialog will be shown where the user has to confirm their logout: <br /> <br />
<img src="README_images/logout_alert_dialog" width="30%" height="30%"/> <br /> <br />
If the user confirms their logout, he/she is taken back to the Login screen.

## Show Details Screen
<p align="left">
<img src="README_images/show_details_screen.png" width="30%" height="30%"/>
</p>
This Show Details screen appears when a user clicks on a particular show card in the shows list. <br /> <br />
There is a photo and description about the show and at the bottom there is a reviews section which is initially empty. 
When the user clicks on the WRITE A REVIEW button, a bottom sheet dialog is opened, so that the user can enter a review. <br /> <br />
On the top of this screen, there is a toolbar with a back button (represented as a back arrow) on it. When the back button is clicked, 
the user is taken back to the Shows screen.

## Write a Review Bottom Sheet Dialog
<p align="left">
<img src="README_images/write_review_dialog_without_input.png" width="30%" height="30%"/>
&nbsp; &nbsp; &nbsp; &nbsp;
<img src="README_images/write_review_dialog_with_input.png" width="30%" height="30%"/>
</p>
This bottom sheet dialog is opened once the user clicks on the WRITE A REVIEW button from the Show Details screen.
The review consists of a rating and a comment. <br /> <br />
The first image shows how the dialog looks before the user has inputted anything. The SUBMIT button is disabled
as long as the user hasn't given a rating. To be able to submit a review, 
only the rating is mandatory and the comment is optional. <br /> <br />
The second image shows how the dialog looks once the user has given a rating 
and inputted some text in the comment field which leads to the SUBMIT button becoming enabled. <br /> <br />
The dialog can be dismissed by clicking somewhere outside the dialog or by clicking the x button on the top right corner of the dialog.

## Reviews Section
<p align="left">
<img src="README_images/reviews_section.png" width="30%" height="30%"/>
</p>
The image above shows how the reviews will be displayed once they are submitted. <br /> <br />
A placeholder profile photo, the username, the rating and the comment are displayed for each review.
On the top of all reviews, a status about them is shown: how many reviews are there and what is the average rating calculated from all of them. 
The average rating is additionally represented with a rating bar.