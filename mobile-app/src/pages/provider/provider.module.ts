import { NgModule } from '@angular/core';
import { IonicPageModule } from 'ionic-angular';
import { ProviderPage } from './provider';
import { ProviderService } from '../../providers/provider-service';
import { ProviderModalPage } from './provider-modal';

@NgModule({
  declarations: [
    ProviderPage,
    ProviderModalPage
  ],
  imports: [
    IonicPageModule.forChild(ProviderPage),
  ],
  providers: [
    ProviderService
  ],
  entryComponents: [
    ProviderModalPage
  ]
})
export class ProviderPageModule {}
