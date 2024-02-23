import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService } from '../config/api.service';
import { ConfigService } from '../config/config.service';

@Injectable({
  providedIn: 'root'
})
export class MasterService {

  constructor(private apiService: ApiService, private configService: ConfigService, private http: HttpClient) { }

  getCompanyEnterpriseDetails() {
    return this.apiService.get(this.configService.company_url);
  }

  getPlantEnterpriseDetails() {
    return this.apiService.get(this.configService.plant_url);
  }

  getWarehouseEnterpriseDetails() {
    return this.apiService.get(this.configService.warehouse_url);
  }
  getFloorEnterpriseDetails() {
    return this.apiService.get(this.configService.floor_url);
  }

  //masters
  getCompanyMasterDetails() {
    return this.apiService.get(this.configService.companyMaster_url);
  }

  getVerticalMasterDetails() {
    return this.apiService.get(this.configService.veticalMaster_url);
  }

  getCurrencyMasterDetails() {
    return this.apiService.get(this.configService.currencyMaster_url);
  }

  getCountryMasterDetails() {
    return this.apiService.get(this.configService.countryMaster_url);
  }

  getStateMasterDetails() {
    return this.apiService.get(this.configService.stateMaster_url);
  }

  getCityMasterDetails() {
    return this.apiService.get(this.configService.cityMaster_url);
  }

  getItemTypeMasterDetails() {
    return this.apiService.get(this.configService.itemTypeMaster_url);
  }

  getItemGroupMasterDetails() {
    return this.apiService.get(this.configService.itemGroupMaster_url);
  }

  getPlantMasterDetails() {
    return this.apiService.get(this.configService.plantMaster_url);
  }

  getWarehouseMasterDetails() {
    return this.apiService.get(this.configService.warehouseMaster_url);
  }

  getFloorMasterDetails() {
    return this.apiService.get(this.configService.floorMaster_url);
  }

  getStorageSectionMasterDetails() {
    return this.apiService.get(this.configService.storageSectionMaster_url);
  }

  getStorageClassMasterDetails() {
    return this.apiService.get(this.configService.storageClassMaster_url);
  }

  getStorageTypeMasterDetails() {
    return this.apiService.get(this.configService.storageTypeMaster_url);
  }

  getStorageBinTypeMasterDetails() {
    return this.apiService.get(this.configService.storageBinTypeMaster_url);
  }

  getSubItemGroupMasterDetails() {
    return this.apiService.get(this.configService.subItemGroupMaster_url);
  }

  getLevelMasterDetails() {
    return this.apiService.get(this.configService.levelMaster_url);
  }

  getVariantMasterDetails() {
    return this.apiService.get(this.configService.variantMaster_url);
  }

  getStrategyTypeMasterDetails() {
    return this.apiService.get(this.configService.strategyTypeMaster_url);
  }

  getAllStorageBin(obj: any) {
    return this.apiService.post(this.configService.getStorageBin, obj);
  }

  getAllHHTUser() {
    return this.apiService.get(this.configService.getAllHHTUserList);
  }


  searchCompany(obj: any){
      return this.http.post<any>('/wms-idmaster-service/companyid/findcompanyid', obj);
    }
    searchPlant(obj: any){
      return this.http.post<any>('/wms-idmaster-service/plantid/findplantid', obj);
    }
    searchWarehouse(obj: any){
      return this.http.post<any>('/wms-idmaster-service/warehouseid/findWarehouseId', obj);
    }
    searchFloor(obj: any){
      return this.http.post<any>('/wms-idmaster-service/floorid/findfloorid', obj);
    }
    searchModule(obj: any){
      return this.http.post<any>('/wms-idmaster-service/moduleid/findModuleId', obj);
    }
    searchState(obj: any){
      return this.http.post<any>('/wms-idmaster-service/state/findState', obj);
    }
    searchCity(obj: any){
      return this.http.post<any>('/wms-idmaster-service/city/findcity', obj);
    } 
    searchstorageclass(obj: any){
      return this.http.post<any>('/wms-idmaster-service/storageclassid/findStorageClassId', obj);
    }
    searchstoragebintype(obj: any){
      return this.http.post<any>('/wms-idmaster-service/storagebintypeid/findstoragebintypeid', obj);
    }
    searchcountry(obj: any){
      return this.http.post<any>('/wms-idmaster-service/country/findcountry', obj);
    }
    searchstoragetype(obj: any){
      return this.http.post<any>('/wms-idmaster-service/storagetypeid/findStorageTypeId', obj);
    }
    searchstoragesection(obj: any){
      return this.http.post<any>('/wms-idmaster-service/storagesectionid/findstoragesectionid', obj);
    }
    searchrow(obj: any){
      return this.http.post<any>('/wms-idmaster-service/rowid/findRowId', obj);
    }
    searchaisle(obj: any){
      return this.http.post<any>('/wms-idmaster-service/aisleid/findAisleId', obj);
    }
    searchbartype(obj: any){
      return this.http.post<any>('/wms-idmaster-service/barcodetypeid/findBarcodeTypeId', obj);
    }
    searchspan(obj: any){
      return this.http.post<any>('/wms-idmaster-service/spanid/findSpanId', obj);
    }
    searchlevel(obj: any){
      return this.http.post<any>('/wms-idmaster-service/levelid/findLevelId', obj);
    }
    searchvariant(obj: any){
      return this.http.post<any>('/wms-idmaster-service/variantid/findvariantid', obj);
    }
    searchprocess(obj: any){
      return this.http.post<any>('/wms-idmaster-service/processid/findProcessId', obj);
    }
    searchitemtype(obj: any){
      return this.http.post<any>('/wms-idmaster-service/itemtypeid/findItemTypeId', obj);
    }
    searchsubitemgroup(obj: any){
      return this.http.post<any>('/wms-idmaster-service/subitemgroupid/findSubItemGroupId', obj);
    }
    searchitemgroup(obj: any){
      return this.http.post<any>('/wms-idmaster-service/itemgroupid/findItemGroupId', obj);
    }
    searchmovementtype(obj: any){
      return this.http.post<any>('/wms-idmaster-service/movementtypeid/findMovementTypeId', obj);
    }
     searchapprovalprocess(obj: any){
      return this.http.post<any>('/wms-idmaster-service/approvalprocessid/findApprovalProcessId', obj);
    }
    searchstock(obj: any){
      return this.http.post<any>('/wms-idmaster-service/stocktypeid/findStockTypeId', obj);
    }
    searchvertical(obj: any){
      return this.http.post<any>('/wms-idmaster-service/vertical/findVertical', obj);
    }
    searchcurrency(obj: any){
      return this.http.post<any>('/wms-idmaster-service/currency/findCurrency', obj);
    }
    searchgetitemdes(obj: any){
      return this.http.post<any>('/wms-idmaster-service/itemtypeid/findItemTypeId', obj);
    }
}
