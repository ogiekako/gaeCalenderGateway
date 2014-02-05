# Google App Engine Calendar Gateway
"I wanted to have the Exchange events in my Google calendar"

## Installation
```git clone <this repo>```

### Account, app and permissions
* Goto https://appengine.google.com/ and create a new application with the default settings. Remember the application identifier ($appIdent$)
* Goto https://cloud.google.com/console/project and select your project. 
    - In "APIs & auth" > "APIs" enable "Calendar API", "Google Cloud SQL" (?) and "Google Cloud Storage" (???).
    - In "APIs & auth" > "Credentials" click "Create new client id". 
        + Type: Web Application
        + Authorized Javascript origins: make it blank
        + Authorized redirect URI:         
    http://localhost:8888/oauth2callback
    http://$appIdent§.appspot.com/oauth2callback
    https://$appIdent$.appspot.com/oauth2callback
        + Click "Download JSON" and save the file under ```src/main/resources/client_secrets.json```
    - You may change some details in "APIs & auth" > "Consent screen"

### Project setup for Eclipse
* ```mvn eclipse:eclipse dependency:copy-dependencies``
* Install Eclipse and the Google App Engine Plugin (https://developers.google.com/appengine/docs/java/tools/eclipse)
* Import the project in Eclipse
* Fix the project setup (you have to do this after every ```mvn eclipse:eclipse```
    - In Project > Google > App Engine
        + Check "Use Google App Engine" 
        + Check "Use default SDK"
        + Add your Application ID $appIdent$ in "Deployment"
    - Press OK
    - Go again to Project > Google > App Engine
        + Check "Use Google App Engine" (again)
    - in "Java Build Path" > "Order and Export" move every "M2_REPO" lib to the bottom
    - Press OK
* Copy the ```src/main/webapp/WEB-INF/appengine-web.xml.template``` to ```src/main/webapp/WEB-INF/appengine-web.xml``` and insert your $appIdent$ in the "application" node. 
* Everything should build now. 

### Project setup for the frontend

    cd frontend
    npm install
    bower install 
    grunt build

## Deploy to App Engine
* ```cd frontend && npm install && bower install && grunt build```
* Right click on the project > Google > Deploy to App Engine
* In one of the following dialogs you are forced to select a WAR Directory. Select here the "webapp" directory. 

## Usage
* Goto $appIdent$.appspot.com/ and login with your Gmail account
* Click the Configuration tab
    - Select your Calendar 
    - Enter a contact email address in case of errors
* Create a filter in your email configuration to forward every email with an event to mailin@$appIdent$.appspotmail.com.

## Develop
TODO

## ToOos
- [ ] Floating message after save/update

## Helpful resources
* Manage application permissions https://plus.google.com/apps?hl=de and https://security.google.com/settings/security/permissions?pli=1
* Calendar API https://developers.google.com/google-apps/calendar/v3/reference/?hl=de
* OAuth https://developers.google.com/accounts/docs/OAuth2WebServer
* Security/Access https://developers.google.com/appengine/docs/java/config/webxml#Security_and_Authentication
* Play and testing Google APIs https://developers.google.com/oauthplayground/
* REST interface
    - htps://docs.jboss.org/resteasy/docs/3.0.5.Final/userguide/html/
    - http://wiki.fasterxml.com/JacksonInFiveMinutes
* JPA in GAE https://developers.google.com/appengine/docs/java/datastore/jpa/overview?hl=de
* Tasks in GAE https://developers.google.com/appengine/docs/java/taskqueue/overview-push


## Notes about the refresh token

    The refresh_token is only provided on the first authorization from the user. Subsequent authorizations, such as the kind you make while testing an OAuth2 integration, will not return the refresh_token again. :)
1. Go to your account security settings: https://www.google.com/settings/u/1/security.
2. Click the edit button next to "Authorizing applications and sites"
3. Then click "Revoke Access" next to your app.  
4. The next OAuth2 request you make will return a refresh_token.
