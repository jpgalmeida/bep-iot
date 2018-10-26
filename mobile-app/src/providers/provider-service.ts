import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class ProviderService {
  public API = 'https://localhost:8443/rest/';
  public RENTAL_API = this.API + 'initPrice/';
  public GET_API = this.API + 'getSectorsByRange/';
  
  constructor(public http: HttpClient) {
  }

  get(): Observable<any> {
    let result: Observable<Object>;
    result = this.http.post(this.GET_API + '1/5','');

    console.log('result '+result);
    return result.catch(error => Observable.throw(error));
  }
  
  save(sector: any): Observable<any> {
    let result: Observable<Object>;

    console.log(this.RENTAL_API+sector.sectorId+'/'+sector.price);
    result = this.http.post(this.RENTAL_API+sector.sectorId+'/'+sector.price, '');
    return result.catch(error => Observable.throw(error));
  }

}