import { RentalService } from '../../providers/rental-service';
import { Component, ViewChild } from '@angular/core';
import { NavParams, ViewController, ToastController, NavController, LoadingController } from 'ionic-angular';
import { NgForm } from '@angular/forms';

@Component({
  templateUrl: './rental-modal.html'
})
export class RentalModalPage {
  @ViewChild('lockerid') lockerid;
  @ViewChild('duration') duration;
  @ViewChild('sector') sector;

  rental: any = {};
  error: any;

  constructor(public rentalService: RentalService,
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
    this.rentalService.save(form).subscribe(result => {
      this.presentLoadingDefault();
    }, error => this.error = error)

    this.presentLoadingDefault();


  }

  presentLoadingDefault() {
    let loading = this.loadingCtrl.create({
      content: 'Renting locker...'
    });
  
    loading.present();
  
    setTimeout(() => {
      loading.dismiss();
      this.dismiss();
    }, 5000);

  }

  ionViewDidLoad() {
  }
}