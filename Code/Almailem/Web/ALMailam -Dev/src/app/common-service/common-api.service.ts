import { Injectable } from '@angular/core';
import { AuthService } from '../core/core';
import { HttpClient } from '@angular/common/http';
import { NgxSpinnerService } from 'ngx-spinner';
import { forkJoin, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from './common-service.service';
export interface dropdownelement {
  key: any;
  value: any;
  referenceField?: any;
}

export interface dropdownelement1 {
  value: any;
  label: any;
  referenceField?: any;
}
export interface dropdownelement2 {
  key: any;
  value: any;
  referenceField?: any;
  languageId?: any;
}
export interface dropdownelement3 {
  value: any;
  label: any;
  languageId?: any;
}
@Injectable({
  providedIn: 'root'
})
export class CommonApiService {

  constructor(private http: HttpClient, private cs: CommonService, private auth: AuthService, private spin: NgxSpinnerService, public toastr: ToastrService,) { }

  setup = '/wms-idmaster-service/';
  masters='/wms-masters-service/';
  dropdownlist: any = {
    setup: {
      binclassid:{url:this.setup + 'binclassid',key:{key:'binClassId',value:'binClass',languageId:'languageId'} },
      billingmodeid:{url:this.setup + 'billingmodeid',key:{key:'billModeId',value:'description',languageId:'languageId'} },
      billingfrequencyid:{url:this.setup + 'billingfrequencyid',key:{key:'billFrequencyId',value:'description',languageId:'languageId'} },
      paymenttermid:{url:this.setup + 'paymenttermid',key:{key:'paymentTermId',value:'description',languageId:'languageId'} },
      palletizationlevelid:{url:this.setup + 'palletizationlevelid',key:{key:'palletizationLevelId',value:'palletizationLevel',languageId:'languageId'}},      paymentmodeid:{url:this.setup + 'paymentmodeid',key:{key:'paymentModeId',value:'description',languageId:'languageId'} },
      barcodetypeid: { url: this.setup + 'barcodetypeid', key: { key: 'barcodeTypeId' , value: 'barcodeType', languageId: 'languageId' } },
      companyid: { url: this.setup + 'companyid', key: { key: 'companyCodeId' , value: 'description', languageId: 'languageId' } },
      aisleid: { url: this.setup + 'aisleid', key: { key: 'aisleId' , value: 'aisleDescription', languageId: 'languageId' } },
      approvalprocessid: { url: this.setup + 'approvalprocessid', key: { key: 'approvalProcessId' , value: 'approvalProcess', languageId: 'languageId' } },
      cyclecounttypeid: { url: this.setup + 'cyclecounttypeid', key: { key: 'cycleCountTypeId' , value: 'cycleCountTypeText', languageId: 'languageId' } },
      currency: { url: this.setup + 'currency', key: { key: 'currencyId' , value: 'currencyDescription',languageId:'languageId' } },
      vertical: { url: this.setup + 'vertical', key: { key: 'verticalId', value: 'verticalName',languageId:'languageId' } },
      city: { url: this.setup + 'city', key: { key: 'cityId' , value: 'cityName',languageId:'languageId' } },
      state: { url: this.setup + 'state', key: { key: 'stateId' , value: 'stateName',  languageId: 'languageId'} },
      stocktypeid: { url: this.setup + 'stocktypeid', key: { key: 'stockTypeId' , value: 'stockTypeText',  languageId: 'languageId'} },
      country: { url: this.setup + 'country', key: { key: 'countryId' , value: 'countryName', languageId: 'languageId' } },
      plantid: { url: this.setup + 'plantid', key: { key: 'plantId' , value: 'description',  languageId: 'languageId'} },
      processid: { url: this.setup + 'processid', key: { key: 'processId' , value: 'processText',  languageId: 'languageId'} },
      warehouseid: { url: this.setup + 'warehouseid', key: { key: 'warehouseId' , value: 'warehouseDesc',languageId: 'languageId' } },
      warehousetypeid: { url: this.setup + 'warehousetypeid', key: { key: 'warehouseTypeId' , value: 'warehouseTypeText',languageId: 'languageId' } },
      floorid: { url: this.setup + 'floorid', key: { key: 'floorId' , value: 'description', languageId: 'languageId' } },
      itemtypeid: { url: this.setup + 'itemtypeid', key: { key: 'itemTypeId' , value: 'itemType',  languageId: 'languageId'} },
      itemgroupid: { url: this.setup + 'itemgroupid', key: { key: 'itemGroupId' , value: 'itemGroup',languageId: 'languageId' } },
      rowid: { url: this.setup + 'rowid', key: { key: 'rowId' , value: 'rowNumber', languageId: 'languageId' } },
      shelfid: { url: this.setup + 'shelfid', key: { key: 'shelfId' , value: 'shelfDescription', languageId: 'languageId' } },
      subitemgroupid: { url: this.setup + 'subitemgroupid', key: { key: 'subItemGroupId' , value: 'subItemGroup' , languageId: 'languageId'} },
      storagesectionid: { url: this.setup + 'storagesectionid', key: { key: 'storageSectionId' , value: 'storageSection', languageId: 'languageId'} },
      storageclassid: { url: this.setup + 'storageclassid', key: { key: 'storageClassId' , value: 'description',languageId: 'languageId'  } },
      storagemethodid: { url: this.setup + 'storagemethodid', key: { key: 'storageMethodId' , value: 'storageMethod',languageId:'languageId' } },
      storagebintypeid: { url: this.setup + 'storagebintypeid', key: { key: 'storageBinTypeId' , value: 'description',languageId:'languageId' } },
      storagetypeid: { url: this.setup + 'storagetypeid', key: { key: 'storageTypeId' , value: 'description',languageId: 'languageId' } },
      strategyid: { url: this.setup + 'strategyid', key: { key: 'strategyTypeId' , value: 'strategyTypeText',languageId: 'languageId'}},
      spanid: { url: this.setup + 'spanid', key: { key: 'spanId' , value: 'spanDescription',languageId: 'languageId' } },
      levelid: { url: this.setup + 'levelid', key: { key: 'levelId' , value: 'level',languageId: 'languageId' } },
      languageid: { url: this.setup + 'languageid', key: { key: 'languageId' , value: 'languageDescription' } },
      moduleid: { url: this.setup + 'moduleid', key: { key: 'moduleId' , value: 'moduleDescription',languageId: 'languageId'  } },
      movementtypeid: { url: this.setup + 'movementtypeid', key: { key: 'movementTypeId' , value: 'movementTypeText',languageId: 'languageId'  } },
      variantid: { url: this.setup + 'variantid', key: { key: 'variantCode' , value: 'variantText',languageId: 'languageId'  } },
      variantType: { url: this.setup + 'variantid', key: { key: 'variantType' , value: 'variantType',languageId: 'languageId'  } },
      uomid: { url: this.setup + 'uomid', key: { key: 'uomId' , value: 'description',languageId: 'languageId'  } },
      dockid: { url: this.setup + 'dockid', key: { key: 'dockId' , value: 'dockId',languageId: 'languageId'  } },
      servicetypeid: { url: this.setup + 'servicetypeid', key: { key: 'serviceTypeId' , value: 'serviceTypeDescription',languageId: 'languageId'  } },
      workcenterid: { url: this.setup + 'workcenterid', key: { key: 'workCenterId' , value: 'workCenterDescription',languageId: 'languageId'  } },
      userRole: { url: this.setup + 'roleaccess', key: { key: 'roleId' , value: 'userRoleName',languageId: 'languageId'  } },
      userType: { url: this.setup + 'usertypeid', key: { key: 'userTypeId' , value: 'userTypeDescription',languageId: 'languageId'  } },
      outboundordertypeid: { url: this.setup + 'outboundordertypeid', key: { key: 'outboundOrderTypeId' , value: 'outboundOrderTypeText',languageId: 'languageId'  } },
    },
    master: {
      businesspartner: { url: this.masters + 'businesspartner', key: { key: 'partnerCode' , value: 'partnerCode'} },
      businesspartnertype: { url: this.masters + 'businesspartner', key: { key: 'businessPartnerType' , value: 'businessPartnerType'} },
      handlingunit: { url: this.masters + 'handlingunit', key: { key: 'handlingUnit' , value: 'handlingUnit',languageId:'languageId'} },
      handlingequipment: { url: this.masters + 'handlingequipment', key: { key: 'handlingEquipmentId' , value: 'handlingEquipmentId',languageId:'languageId'} },
      pricelist: { url: this.masters + 'pricelist', key: { key: 'priceListId' , value: 'priceListId',languageId: 'languageId' } },
      imbasicdata1:{url: this.masters + 'imbasicdata1', key: { key: 'ItemCode' , value: 'itemCode',languageId: 'languageId'}},
      imbasic:{url: this.masters + 'imbasicdata1', key: { key: 'uomId' , value: 'uomId',languageId: 'languageId'}},
      driver:{url: this.masters + 'driver', key: { key: 'driverId' , value: 'driverName',languageId: 'languageId'}},
      vehicle:{url: this.masters + 'vehicle', key: { key: 'vehicleNumber' , value: 'vehicleNumber',languageId: 'languageId'}},
      route:{url: this.masters + 'route', key: { key: 'routeId' , value: 'routeId',languageId: 'languageId'}},
      packingmaterial:{url: this.masters + 'route', key: { key: 'packingmaterial' , value: 'packingMaterialNo',languageId: 'languageId'}},
    },
  }
 


  getalldropdownlist(url: string[]) {
    let observableBatch: any[] = [];
    url.forEach((url: string) => { observableBatch.push(this.http.get<any>(url).pipe(catchError(err => of(err)))) });
    return forkJoin(observableBatch);
  }

  foreachlist(list: any, val: { key: any, value: any }, _filter: any = {}, addblank: boolean = false,) {
    let dropdownlist: dropdownelement[] = [];
    let dropdownlist1: dropdownelement1[] = [];
    let filter = list;
    if (_filter)
      filter = this.cs.filterArray(list, _filter);
    if (addblank)
      dropdownlist.push({ key: '', value: '' });
    for (const l of filter) {
      let filter2 = this.cs.filterArray(dropdownlist, { key: l[val.key] })
      if (filter2.length == 0)
      dropdownlist1.push({ value: l[val.value], label: l[val.key] + ' - ' + l[val.value] });
    }
    return dropdownlist1.sort((a, b) => (a.value > b.value) ? 1 : -1);
  }

  foreachlist2(list: any, val: { key: any, value: any }, _filter: any = {}, addblank: boolean = false,) {
    let dropdownlist: dropdownelement[] = [];
    let dropdownlist1: dropdownelement1[] = [];
    let filter = list;
    if (_filter)
      filter = this.cs.filterArray(list, _filter);
    if (addblank)
      dropdownlist.push({ key: '', value: '' });
    for (const l of filter) {
      let filter2 = this.cs.filterArray(dropdownlist, { key: l[val.key] })
      if (filter2.length == 0)
      dropdownlist1.push({ value: l[val.key], label: l[val.key] + ' - ' + l[val.value] });
    }
    return dropdownlist1.sort((a, b) => (a.value > b.value) ? 1 : -1);
  }

  forLanguageFilter(list: any, val: { key: any, value: any, languageId: any }, _filter: any = {}, addblank: boolean = false,) {
    let dropdownlist: dropdownelement2[] = [];
    let dropdownlist1: dropdownelement3[] = [];
    let dropdownlist2: dropdownelement3[] = [];
    let filter = list;
    if (_filter)
      filter = this.cs.filterArray(list, _filter);
    if (addblank)
      dropdownlist.push({ key: '', value: '', languageId: '' });
    for (const l of filter) {
      let filter2 = this.cs.filterArray(dropdownlist, { key: l[val.key] })
      if (filter2.length == 0)
      dropdownlist1.push({ value: l[val.key], label: l[val.key] + ' - ' + l[val.value], languageId: l[val.languageId]});
    }
    dropdownlist1.forEach(x=> {
      if(this.auth.languageId == x.languageId){
        dropdownlist2.push(x)
      }
    })
    return dropdownlist2.sort((a, b) => (a.value > b.value) ? 1 : -1);
  }



  foreachlist1(list: any, val: { key: any, value: any }, _filter: any = {}, addblank: boolean = false,) {
    let dropdownlist: dropdownelement[] = [];
    let dropdownlist1: dropdownelement1[] = [];
    let filter = list;
    if (_filter)
      filter = this.cs.filterArray(list, _filter);
    if (addblank)
      dropdownlist.push({ key: '', value: '' });
    for (const l of filter) {
      let filter2 = this.cs.filterArray(dropdownlist, { key: l[val.key] })
      if (filter2.length == 0)
      dropdownlist1.push({ value: l[val.value], label: l[val.value] });
    }
    return dropdownlist1.sort((a, b) => (a.value > b.value) ? 1 : -1);
  }


  foreachlist3(list: any, val: { key: any, value: any }, _filter: any = {}, addblank: boolean = false,) {
    let dropdownlist: dropdownelement[] = [];
    let dropdownlist1: dropdownelement1[] = [];
    let filter = list;
    if (_filter)
      filter = this.cs.filterArray(list, _filter);
    if (addblank)
      dropdownlist.push({ key: '', value: '' });
    for (const l of filter) {
      let filter2 = this.cs.filterArray(dropdownlist, { key: l[val.key] })
      if (filter2.length == 0)
      dropdownlist1.push({ value: l[val.key], label: l[val.value] });
    }
    return dropdownlist1.sort((a, b) => (a.value > b.value) ? 1 : -1);
  }

  foreachlist_searchpage(list: any, val: { key: any, value: any }, _filter: any = {}, addblank: boolean = false,) {
    let dropdownlist: dropdownelement[] = [];
    let filter = list;

    if (_filter)
      filter = this.cs.filterArray(list, _filter);
    if (addblank)
      dropdownlist.push({ key: '', value: '' });
    for (const l of filter) {
      let filter2 = this.cs.filterArray(dropdownlist, { key: l[val.key] })
      if (filter2.length == 0)
        dropdownlist.push({ key: l[val.key], value: l[val.value] });
    }
    return dropdownlist.sort((a, b) => (a.key > b.key) ? 1 : -1);
  }
  dockerSignStatusUpdatepotentialClientId(url: any[]) {
    let observableBatch: any[] = [];
    url.forEach((url) => { observableBatch.push(this.http.patch<any>(url.url, url.obj).pipe(catchError(err => of(err)))) });
    return forkJoin(observableBatch);
  }

  removeDuplicatesFromArray(list:any = []){
    let dataObj: any = {};
    for (let i = 0; i < list.length; i++) {
      dataObj[list[i]['key']] = list[i];
    }
    list = new Array();
    for (let key in dataObj) {
      list.push(dataObj[key]);
    }
    return list;
  }

  removeDuplicatesFromArray1(list:any = []){
    let dataObj: any = {};
    for (let i = 0; i < list.length; i++) {
      dataObj[list[i]['id']] = list[i];
    }
    list = new Array();
    for (let key in dataObj) {
      list.push(dataObj[key]);
    }
    return list;
  }

  removeDuplicatesFromArrayNew(list:any = []){
    let dataObj: any = {};
    for (let i = 0; i < list.length; i++) {
      dataObj[list[i]['value']] = list[i];
    }
    list = new Array();
    for (let key in dataObj) {
      list.push(dataObj[key]);
    }
    return list;
  }

}

