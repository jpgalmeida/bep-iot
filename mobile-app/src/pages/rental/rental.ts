import { Component } from '@angular/core';
import { IonicPage, ModalController, NavController, NavParams } from 'ionic-angular';
import { RentalService } from '../../providers/rental-service';
import { RentalModalPage } from './rental-modal';
import { SectorsPage } from '../sectors/sectors';
import { EndRentalPage } from '../end-rental/end-rental';

/**
 * Generated class for the RentalPage page.
 *
 * See https://ionicframework.com/docs/components/#navigation for more info on
 * Ionic pages and navigation.
 */

@IonicPage()
@Component({
  selector: 'page-rental',
  templateUrl: 'rental.html',
})
export class RentalPage {

  private rentalsAux: Array<any>;

  constructor(public navCtrl: NavController, public navParams: NavParams,
    public rentalService: RentalService,
    public modalCtrl: ModalController) {
}

ionViewWillEnter() {
  this.rentalService.get().subscribe(rentals => {
    if(rentals.errorMessage!="Error"){
        this.rentalsAux = new Array();
        rentals = JSON.parse(rentals.errorMessage);
        console.log(rentals);
        for (var i = 0; i < rentals.length; i++) {
          this.rentalsAux.push(JSON.parse(rentals[i].Record));
        }

    }
    
  })
}

  openModal() {
    let modal = this.modalCtrl.create(RentalModalPage);
    modal.present();
    // refresh data after modal dismissed
    modal.onDidDismiss(() => this.ionViewDidLoad())
  }

  openSectorList() {
    this.navCtrl.push(SectorsPage);
  }

  ionViewDidLoad(){
    this.rentalService.get().subscribe(rentals => {
      if(rentals.errorMessage!="Error"){
        this.rentalsAux = new Array();
        rentals = JSON.parse(rentals.errorMessage);
        console.log(rentals);
        for (var i = 0; i < rentals.length; i++) {
          this.rentalsAux.push(JSON.parse(rentals[i].Record));
        }

      }
    })
  }

  endRental(index: any){
    console.log('getting rental: '+index);
    this.navCtrl.push(EndRentalPage, { rentalId: index});
    //this.navCtrl.push(EndRentalPage);
  }

}
