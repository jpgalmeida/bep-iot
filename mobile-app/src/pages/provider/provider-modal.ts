import { ProviderService } from '../../providers/provider-service';
import { Component, ViewChild } from '@angular/core';
import { NavParams, ViewController, ToastController, NavController, LoadingController } from 'ionic-angular';
import { NgForm } from '@angular/forms';

@Component({
  templateUrl: './provider-modal.html'
})
export class ProviderModalPage {
  @ViewChild('sectorid') sectorid;
  @ViewChild('price') price;

  sector: any = {};
  error: any;

  constructor(public providerService: ProviderService,
              public params: NavParams,
              public viewCtrl: ViewController,
              public toastCtrl: ToastController,
              public navCtrl: NavController,
              public loadingCtrl: LoadingController) {
    
  }

  dismiss() {
    this.viewCtrl.dismiss();
  }

  save(form: NgForm) {
    console.log(form);

    this.providerService.save(form).subscribe(result => {
      this.presentLoadingDefault();

    }, error => this.error = error)

    this.presentLoadingDefault();
  }


  presentLoadingDefault() {
    let loading = this.loadingCtrl.create({
      content: 'Creating sector...'
    });
  
    loading.present();
  
    setTimeout(() => {
      loading.dismiss();
      this.dismiss();
    }, 5000);

  }

}