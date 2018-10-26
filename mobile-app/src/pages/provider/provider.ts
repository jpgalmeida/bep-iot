import { Component } from '@angular/core';
import { IonicPage, ModalController, NavController, NavParams } from 'ionic-angular';
import { ProviderService } from '../../providers/provider-service';
import { ProviderModalPage } from './provider-modal'
/**
 * Generated class for the RentalPage page.
 *
 * See https://ionicframework.com/docs/components/#navigation for more info on
 * Ionic pages and navigation.
 */

@IonicPage()
@Component({
  selector: 'page-provider',
  templateUrl: 'provider.html',
})
export class ProviderPage {

  private sectorsAux: Array<any>;

  constructor(public navCtrl: NavController, public navParams: NavParams,
    public providerService: ProviderService,
    public modalCtrl: ModalController) {
}

ionViewWillEnter() {
  this.providerService.get().subscribe(sectors => {
    
    if(sectors.errorMessage!="Error"){
      this.sectorsAux = new Array();
      sectors = JSON.parse(sectors.errorMessage);

      for (var i = 0; i < sectors.length; i++) {
        this.sectorsAux.push(JSON.parse(sectors[i].Record));
      }        
    }
    
  })
}
  

  openModal() {
    let modal = this.modalCtrl.create(ProviderModalPage);
    modal.present();
    // refresh data after modal dismissed
    modal.onDidDismiss(() => this.ionViewDidLoad())
  }

  ionViewDidLoad(){
    this.providerService.get().subscribe(sectors => {
      
      if(sectors.errorMessage!="Error"){
        this.sectorsAux = new Array();
        sectors = JSON.parse(sectors.errorMessage);

        for (var i = 0; i < sectors.length; i++) {
          this.sectorsAux.push(JSON.parse(sectors[i].Record));
        }        
      }
    })
  }

}
