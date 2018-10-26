# mobile-app
This folder contains the necessary information to run the developed mobile application. This application was developed using Ionic Framework. It is necessary to have Ionic installed.

To install Ionic it is necessary to install Node and npm and run:

```sh
# Install ionic
npm install -g ionic cordova
```

The requests to the application are done in the files:

- `src/providers/rental-service.ts` - request for the rental.

- `src/providers/provider-service.ts` - request for the provider to set the sector price.

In order to change the address of deployment of the Smart Hub, it is necessary to change the address in the requests in the previous files.

### Instructions:
The command to run the application is bellow. It will open a browser window and Ionic provides the possibility of trying the application in a Web, iOs, Android or Windows Phone environment.

```sh
# Running the application
ionic serve --lab
```