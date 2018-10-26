import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { NgForm } from '@angular/forms';
import { RentalPage } from '../rental/rental'
import { ProviderPage } from '../provider/provider'

@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {
  login: any = {};

  constructor(public navCtrl: NavController) {

  }

  save(form: NgForm) {

    if(this.login.userid=="prov")
      this.navCtrl.push(ProviderPage);
    
    else if (this.login.userid=="user")
      this.navCtrl.push(RentalPage);
  }


}
