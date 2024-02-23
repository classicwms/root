import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class LanguageidService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/languageid');
  }
  Get(code: string) {
    return this.http.get<any>('/wms-idmaster-service/languageid/' + code );
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/languageid', obj);
  }
  Update(obj: any, code: any) {
    return this.http.patch<any>('/wms-idmaster-service/languageid/' + code, obj);
  }
  Delete(languageId: any,) {
    return this.http.delete<any>('/wms-idmaster-service/languageid/' + languageId);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/languageid/findlanguageid', obj);
}
}




