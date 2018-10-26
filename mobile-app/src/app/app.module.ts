import { NgModule, ErrorHandler } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { IonicApp, IonicModule, IonicErrorHandler } from 'ionic-angular';
import { MyApp } from './app.component';

import { HomePage } from '../pages/home/home';

import { StatusBar } from '@ionic-native/status-bar';
import { SplashScreen } from '@ionic-native/splash-screen';

import { RentalPageModule } from '../pages/rental/rental.module';
import { ProviderPageModule } from '../pages/provider/provider.module';
import { EndRentalPageModule } from '../pages/end-rental/end-rental.module';
import { HttpClientModule } from '@angular/common/http';
import { SectorsPageModule } from '../pages/sectors/sectors.module';

@NgModule({
  declarations: [
    MyApp,
    HomePage
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    IonicModule.forRoot(MyApp),
    RentalPageModule,
    ProviderPageModule,
    SectorsPageModule,
    EndRentalPageModule
  ],
  bootstrap: [IonicApp],
  entryComponents: [
    MyApp,
    HomePage
  ],
  providers: [
    StatusBar,
    SplashScreen,
    {provide: ErrorHandler, useClass: IonicErrorHandler}
  ]
})
export class AppModule {}
