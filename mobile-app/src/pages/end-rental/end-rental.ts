import { Component } from '@angular/core';
import { IonicPage, NavController, NavParams, ToastController, ViewController } from 'ionic-angular';
import { RentalService } from '../../providers/rental-service';
/**
 * Generated class for the EndRentalPage page.
 *
 * See https://ionicframework.com/docs/components/#navigation for more info on
 * Ionic pages and navigation.
 */

@IonicPage()
@Component({
  selector: 'page-end-rental',
  templateUrl: 'end-rental.html',
})
export class EndRentalPage {
  private rentalId: any;
  error: any;

  constructor(public navCtrl: NavController, public navParams: NavParams, public rentalService: RentalService, public toastCtrl: ToastController, public viewCtrl: ViewController) {
    this.rentalId = navParams.get('rentalId');
  }

  ionViewDidLoad() {
    console.log(this.rentalId);
  }

  dismiss() {
    this.viewCtrl.dismiss();
  }

  endRental(){
    console.log('here');
    this.rentalService.endRental(this.rentalId).subscribe(result => {
      let toast = this.toastCtrl.create({
        message: 'Ended rental',
        duration: 2000
      });
      toast.present();
      this.dismiss();
    }, error => this.error = error)
  }

}
