import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from 'src/app/core/core';

@Injectable({
  providedIn: 'root'
})
export class StatussmessagesidService {

  constructor(private http: HttpClient, private auth: AuthService) { }

  Getall() {
    return this.http.get<any>('/wms-idmaster-service/statusmessagesid');
  }
  Get(code: string, languageId: string, messageType:string) {
    return this.http.get<any>('/wms-idmaster-service/statusmessagesid/' + code + '?languageId=' + languageId + '&messageType=' + messageType);
  }
  Create(obj: any) {
    return this.http.post<any>('/wms-idmaster-service/statusmessagesid', obj);
  }
  Update(obj: any, code: any,languageId:any,messageType:any) {
    return this.http.patch<any>('/wms-idmaster-service/statusmessagesid/'+ code + '?languageId=' +languageId+'&messageType='+messageType, obj);
  }
  Delete(messagesId: any,languageId:any,messageType:any) {
    return this.http.delete<any>('/wms-idmaster-service/statusmessagesid/' + messagesId + '?languageId=' +languageId+'&messageType='+messageType);
}
search(obj: any) {
  return this.http.post<any>('/wms-idmaster-service/statusmessagesid/findStatusMessagesId', obj);
}
}



