import { Component } from '@angular/core';
import { IonicPage, ModalController, NavController, NavParams } from 'ionic-angular';
import { ProviderService } from '../../providers/provider-service';
/**
 * Generated class for the RentalPage page.
 *
 * See https://ionicframework.com/docs/components/#navigation for more info on
 * Ionic pages and navigation.
 */

@IonicPage()
@Component({
  selector: 'page-sectors',
  templateUrl: 'sectors.html',
})
export class SectorsPage {

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
