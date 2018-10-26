import { NgModule } from '@angular/core';
import { IonicPageModule } from 'ionic-angular';
import { RentalPage } from './rental';
import { RentalService } from '../../providers/rental-service';
import { RentalModalPage } from './rental-modal';

@NgModule({
  declarations: [
    RentalPage,
    RentalModalPage
  ],
  imports: [
    IonicPageModule.forChild(RentalPage),
  ],
  providers: [
    RentalService
  ],
  entryComponents: [
    RentalModalPage
  ]
})
export class RentalPageModule {}
