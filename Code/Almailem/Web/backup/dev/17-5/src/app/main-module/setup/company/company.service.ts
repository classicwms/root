import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from 'src/app/config/api.service';
import { ConfigService } from 'src/app/config/config.service';

@Injectable({
  providedIn: 'root'
})
export class CompanyService {

  constructor(private apiService: ApiService, private configService: ConfigService) { }

  saveCompanyDetails(data: any): Observable<any> {
    return this.apiService.post(this.configService.company_url, data);
  }

  getCompanyDetails(companyId: string) {
    return this.apiService.get(this.configService.company_url + '/' + companyId);
  }

  updateCompanyDetails(data: any): Observable<any> {
    return this.apiService.patchWithBody(this.configService.company_url + '/' + data.companyId, data);
  }
}
