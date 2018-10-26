import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class RentalService {
  public API = 'https://localhost:8443/rest/';
  public RENTAL_API = this.API + 'initRental/';
  public GET_API = this.API + 'getRentalsByRange/';

  constructor(public http: HttpClient) {
  }
  
  get(): Observable<any> {
    let result: Observable<Object>;
    result = this.http.post(this.GET_API + '1/30','');
    
    
    return result.catch(error => Observable.throw(error));
  }
  
  save(rental: any): Observable<any> {
    let result: Observable<Object>;

    result = this.http.post(this.RENTAL_API+rental.lockerid+'/'+rental.sector+'/'+rental.duration , '');
    return result.catch(error => Observable.throw(error));
  }

  endRental(rentalId: any): Observable<any> {
    let result: Observable<Object>;
    console.log('sending post');
    result = this.http.post(this.API+'endRental/'+rentalId, '');
    return result.catch(error => Observable.throw(error));
  }

}