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
    searchusertype(obj: any){
      return this.http.post<any>('/wms-idmaster-service/usertypeid/findUserTypeId', obj);
    }
    searchPlantenter(obj: any){
      return this.http.post<any>('/wms-enterprise-service/plant/findPlant', obj);
    }
    searchWarehouse(obj: any){
      return this.http.post<any>('/wms-idmaster-service/warehouseid/findWarehouseId', obj);
    }
    searchWarehousetype(obj: any){
      return this.http.post<any>('/wms-idmaster-service/warehousetypeid/findWarehouseTypeId', obj);
    }
    searchoutboundordertype(obj: any){
      return this.http.post<any>('/wms-idmaster-service/outboundordertypeid/findOutboundOrderTypeId', obj);
    }
    searchWarehouseenter(obj: any){
      return this.http.post<any>('/wms-enterprise-service/warehouse/findWarehouse', obj);
    }
    searchFloor(obj: any){
      return this.http.post<any>('/wms-idmaster-service/floorid/findfloorid', obj);
    }
    searchFloorenter(obj: any){
      return this.http.post<any>('/wms-enterprise-service/floor/findFloor', obj);
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
    searchstorageclassenter(obj: any){
      return this.http.post<any>('/wms-enterprise-service/storageclass/findStorageClass', obj);
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
    searchstoragetypeenter(obj: any){
      return this.http.post<any>('/wms-enterprise-service/storagetype/findStorageType', obj);
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
    searchstrategy(obj: any){
      return this.http.post<any>('/wms-idmaster-service/strategyid/findStrategyId', obj);
    }
    searchpackingmaterial(obj: any){
      return this.http.post<any>('/wms-masters-service/packingmaterial/findPackingMaterial', obj);
    }
    searchitemtypeenter(obj: any){
      return this.http.post<any>('/wms-enterprise-service/itemtype/findItemType', obj);
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
    searchMenu(obj: any){
      return this.http.post<any>('/wms-idmaster-service/menuid/findMenuId', obj);
    }
    searchbuisnesspartner(obj: any){
      return this.http.post<any>('/wms-masters-service/businesspartner/findBusinessPartner', obj);
    }
    searchimbasic(obj: any){
      return this.http.post<any>('/wms-masters-service/imbasicdata1/findImBasicData1', obj);
    }
    searchvehicle(obj: any){
      return this.http.post<any>('/wms-masters-service/vehicle/findVehicle', obj);
    }
    searchroute(obj: any){
      return this.http.post<any>('/wms-masters-service/route/findRoute', obj);
    }
    searchdriver(obj: any){
      return this.http.post<any>('/wms-masters-service/driver/findDriver', obj);
    }
    searchstratergytype(obj: any){
      return this.http.post<any>('/wms-idmaster-service/strategyid/findStrategyId', obj);
    }
    searchpartner(obj: any){
      return this.http.post<any>('/wms-masters-service/businesspartner/findBusinessPartner', obj);
    }
    searchbinclass(obj: any){
      return this.http.post<any>('/wms-idmaster-service/binclassid/findBinClassId', obj);
    }
    searchuom(obj: any){
      return this.http.post<any>('/wms-idmaster-service/uomid/findUomId', obj);
    }
    searchpalletization(obj: any){
      return this.http.post<any>('/wms-idmaster-service/palletizationlevelid/findPalletizationLevelId', obj);
    }
    searchdock(obj: any){
      return this.http.post<any>('/wms-idmaster-service/dockid/findDockId', obj);
    } 
    searchworkcenter(obj: any){
      return this.http.post<any>('/wms-idmaster-service/workcenterid/findWorkCenterId', obj);
    }
    searchRole(obj: any){
      return this.http.post<any>('/wms-idmaster-service/roleaccess/findRoleAccess', obj);
    }
    searchCycelCount(obj: any){
      return this.http.post<any>('/wms-idmaster-service/cyclecounttypeid/findCycleCountTypeId', obj);
    }
    searchshelf(obj: any){
      return this.http.post<any>('/wms-idmaster-service/shelfid/findShelfId', obj);
    }
    searchpaymentterm(obj: any){
      return this.http.post<any>('/wms-idmaster-service/paymenttermid/findPaymentTermId', obj);
    }
    searchbillfrequency(obj: any){
      return this.http.post<any>('/wms-idmaster-service/billingfrequencyid/findBillingFrequencyId', obj);
    }
    searchserviceType(obj: any){
      return this.http.post<any>('/wms-idmaster-service/servicetypeid/findServiceTypeId', obj);
    }
    searchbillmode(obj: any){
      return this.http.post<any>('/wms-idmaster-service/billingmodeid/findBillingModeId', obj);
    }
    searchpaymentmode(obj: any){
      return this.http.post<any>('/wms-idmaster-service/paymentmodeid/findPaymentModeId', obj);
    }
    searchhandlingunit(obj: any){
      return this.http.post<any>('/wms-masters-service/handlingunit/findHandlingUnit', obj);
    }
    searchhandlingequipment(obj: any){
      return this.http.post<any>('/wms-masters-service/handlingequipment/findHandlingEquipment', obj);
    }
    getAllStorageBinNew(obj: any) {
      return this.apiService.post(this.configService.getStorageBinNew, obj);
    }
} 
