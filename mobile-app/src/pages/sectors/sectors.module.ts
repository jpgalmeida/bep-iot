import { NgModule } from '@angular/core';
import { IonicPageModule } from 'ionic-angular';
import { SectorsPage } from './sectors';
import { ProviderService } from '../../providers/provider-service';

@NgModule({
  declarations: [
    SectorsPage
  ],
  imports: [
    IonicPageModule.forChild(SectorsPage),
  ],
  providers: [
    ProviderService
  ]
})
export class SectorsPageModule {}
