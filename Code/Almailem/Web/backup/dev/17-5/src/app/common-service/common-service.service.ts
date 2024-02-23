import { Injectable, Inject } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Subject } from 'rxjs';

import * as CryptoJS from 'crypto-js';
import { DatePipe } from '@angular/common';

import * as XLSX from 'xlsx';
import { HttpClient } from '@angular/common/http';

import { environment } from 'src/environments/environment';

export interface dropdownelement {
  item_id: any;
  item_text: any;

}

export interface dropdownelement1 {
  id: any;
  itemName: any;

}
@Injectable({
  providedIn: 'root'
})
export class CommonService {
  private notify = new Subject<any>();
  inboundorderType = [
    { id: 1, text: 'ASN' },
    { id: 2, text: 'Store Return' },
    { id: 3, text: 'WH2WH' },
    { id: 4, text: 'SaleOrder Return' },
  ]

  public getinboundorderType_text(id: any) {
    let result = this.inboundorderType.filter(x => x.id === id);
    if (result) { return result[0].text }
    else { return null }

  }
  day_callapiSearch(date: any) {
    const del_date = this.datepipe.transform(date, 'yyyy-MM-ddT00:00:00.000');
    return del_date;
  }
  outboundOrderType = [
    { id: 0, text: 'SO' },
    { id: 1, text: 'WH2WH' },
    { id: 2, text: 'Return PO' },
    { id: 3, text: 'TE Order' },
  ]

  public getoutboundOrderType_text(id: any) {
    let result = this.outboundOrderType.filter(x => x.id === id);
    if (result) { return result[0].text }
    else { return null }

  }
  public getstatus_text(id: any) {
    if (!sessionStorage.getItem('status')) {
      this.callstatus(id);
    }

  
    let result = JSON.parse(sessionStorage.getItem('status') as '[]');
    if (result != null && result != undefined && result != '[]') {
      result = result.filter(x => x.statusId === id && x.languageId ==  "EN");
    }


    if (result) { return result[0].status }
    else { return null }

  }
  private callstatus(id: any) {
    this.http.get<any>('/wms-idmaster-service/statusid').subscribe((res) => {
      sessionStorage.setItem('status', JSON.stringify(res));
      this.getstatus_text(id)
    }, err => {

    })

  }
  /**
   * Observable string streams
   */
  notifyObservable$ = this.notify.asObservable();

  currentEnv: string;
  prod: boolean;
  product: boolean;

  constructor(private toastr: ToastrService, private datepipe: DatePipe, private http: HttpClient,) { 
    this.currentEnv = environment.name; 

  }


  public notifyOther(data: any) {
    this.notify.next(data);
  }


  public commonerror(msg: any) {
    // 
    // if (msg.name == "HttpErrorResponse")
    //   this.toastr.error("Check your internet connection/check with Admin.", "Error");
    // else
    this.toastr.error(msg.error, "Error", {
      timeOut: 2000,
      progressBar: false,
    });


  }
  
  public commonerrorNew(msg: any) {
      if(msg.status != 0){
        this.toastr.error(msg.error.error, "Error", {
          timeOut: 2000,
          progressBar: false,
        });
      }else{
        this.toastr.error("Please make sure you are connected to the internet and then reload the page", "Network Error", {
          timeOut: 2000,
          progressBar: false,
        });
      }
  }

  dateapi(date: any) {
    const del_date = this.datepipe.transform(date, "dd-MM-yyyy", "GMT-06:00");
    return del_date;
  }

  dateTimeApi(date: any) {
    const del_date = this.datepipe.transform(date, "dd-MM-yyyy  HH:mm:ss", "UTC 0");
    return del_date;
  }

  day_callapi(date: any) {
    const del_date = this.datepipe.transform(date, 'yyyy-MM-ddT00:00:00.000');
    return del_date;
  }
  day_callapi1(date: any) {
    const del_date = this.datepipe.transform(date, 'yyyy-MM-ddT00:00:00.000', "UTC +03:00");
    return del_date;
  }
  day_callapi11(date: any) {
    const del_date = this.datepipe.transform(date, 'yyyy-MM-ddT00:00:00.000', "UTC 0");
    return del_date;
  }
  dateapiutc0(date: any) {
    const del_date = this.datepipe.transform(date, "dd-MM-yyyy", "UTC 0");
    return del_date;
  }

  dateNewFormat(date: any) {
    const del_date = this.datepipe.transform(date, 'yyyy-MM-dd HH:mm:ss');
    return del_date;
  }
 timeFormat(date: any) {
    const del_date = this.datepipe.transform(date, 'HH:mm a');
    return del_date;
  }
  dateNewFormat1(date: any) {
    const del_date = this.datepipe.transform(date, 'yyyy-MM-dd');
    return del_date;
  }
  
  dateExcel(date: any) {
    const del_date = this.datepipe.transform(date, "dd-MM-yyyy");
    return del_date;
  }
  uploadDate(date: any) {
    const del_date = this.datepipe.transform(date, "MM-dd-yyyy");
    return del_date;
  }
  getdate() {
    const del_date = this.datepipe.transform(Date.now(), 'dd/MM/yyyy');
    return del_date;

  }
  getOnlyDate(date) {
    const del_date = this.datepipe.transform(date, 'MM/dd/yyyy');
    return del_date;

  }
  date(date: any) {
    return new Date(date);
  }

  dateMMYY(date: any) {
    const del_date = this.datepipe.transform(date, "dd-MM-yyyy");
    return del_date;
  }
  dateddMMYY(date: any) {
    const del_date = this.datepipe.transform(date, "MM-dd-yyyy");
    return del_date;
  }
  dateHHSS(date: any) {
    const del_date = this.datepipe.transform(date, "HH:mm");
    return del_date;
  }

  getDataDiff(startDate: any, endDate: any) {

    var diff = this.date(endDate).getTime() - this.date(startDate).getTime();
    var days = Math.floor(diff / (60 * 60 * 24 * 1000));
    var hours = Math.floor(diff / (60 * 60 * 1000)) - (days * 24);
    var minutes = Math.floor(diff / (60 * 1000)) - ((days * 24 * 60) + (hours * 60));
    var seconds = Math.floor(diff / 1000) - ((days * 24 * 60 * 60) + (hours * 60 * 60) + (minutes * 60));
    days == 0 ? '' : days + 'Days ';
    return days + hours + ':' + minutes + ':' + seconds
  };
  encrypt(storageData: any) {
    //Encrypt Info

    return encodeURIComponent(CryptoJS.AES.encrypt(JSON.stringify(storageData), 'secret key 123').toString());

  }

  decrypt(storageData: any) {
    var deData = CryptoJS.AES.decrypt(decodeURIComponent(storageData), 'secret key 123');

    return JSON.parse(deData.toString(CryptoJS.enc.Utf8));
  }

  todayapi() {
    const del_date = this.datepipe.transform(new Date().toLocaleDateString(), 'yyyy-MM-ddTh:mm:ss.SSS');
    return del_date;
  }

  exportAsExcel(data: any, filename: string = "Downloaded_Excel") {
    // const asnConf = data;

    const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(data);
    // converts a DOM TABLE element to a worksheet
    const wb: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Sheet1');
    /* save to file */
    XLSX.writeFile(
      wb,
      filename + `_${new Date().getDate() +
      '-' +
      (new Date().getMonth() + 1) +
      '-' +
      new Date().getFullYear()
      }.xlsx`
    );
  }



  removeDuplicatesFromArrayNew(list: any = []) {
    let dataObj: any = {};
    for (let i = 0; i < list.length; i++) {
      dataObj[list[i]['case_no']] = list[i];
    }
    list = new Array();
    for (let key in dataObj) {
      list.push(dataObj[key]);
    }
    return list;
  }
  removeDuplicatesOldDropdown(list: any = []) {
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

  removeDuplicatesFromArrayNew1(list: any = []) {
    let dataObj: any = {};
    for (let i = 0; i < list.length; i++) {
      dataObj[list[i]['lineNo']] = list[i];
    }
    list = new Array();
    for (let key in dataObj) {
      list.push(dataObj[key]);
    }
    return list;
  }
  removeDuplicatesFromArrayNewstatus(list: any = []) {
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

  removeDuplicatesFromArraydropdown(list: any = []) {
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

  datesearch(date: any): Date {

    return new Date(date);
  }

  groupByData(list: any[], keyGetter: any) {
    let map = new Map();
    list.forEach((item) => {
      let key = keyGetter(item);
      let collection = map.get(key);
      if (!collection) {
        map.set(key, [item]);
      } else {
        collection.push(item);
      }
    });
    return map;
  }

  ngxDropdownFilter(list: any = []) {
    let dataObj: any = {};
    for (let i = 0; i < list.length; i++) {
      dataObj[list[i]['item_id']] = list[i];
    }
    list = new Array();
    for (let key in dataObj) {
      list.push(dataObj[key]);
    }
    return list;
  }

  removeDuplicatesFromArrayList(list: any = [], fieldName) {
    let dataObj: any = {};
    for (let i = 0; i < list.length; i++) {
      dataObj[list[i][fieldName]] = list[i];
    }
    list = new Array();
    for (let key in dataObj) {
      list.push(dataObj[key]);
    }
    return list;
  }

  
environmentPROD(){
  if(this.currentEnv == 'prod'){
    this.prod = true;
    return true;
  }else{
     this.prod = false;
     return false;
  }
}

environmentProduct(){
  if(this.currentEnv == 'product'){
    this.product = true;
    return true;
  }else{
     this.product = false;
     return false;
  }
}

public filterArray(targetArray: any[], filters: any) {

  var filterKeys = Object.keys(filters);
  let data = targetArray.filter(function (eachObj: any) {
    return filterKeys.every(function (eachKey) {
      if (!filters[eachKey])
        return true;
      if (!eachObj[eachKey])
        return true;
      if (eachObj[eachKey] === undefined)
        return true;
      // if (filters[eachKey]) {
      //   if (!filters[eachKey].length) {
      //     return true;
      //   }
      // }

      const fo: string = String(filters[eachKey])?.toLowerCase();
      const eo: string = String(eachObj[eachKey])?.toLowerCase();
      if (fo == "")
        return true;

      const folist = fo.split(',');

      return folist.includes(eo);
    });
  });

  if (filters.createdOn_from &&
    filters.createdOn_to) {

    data = data.filter(x =>
      this.datesearch(this.day_callapiSearch(new Date(x.createdOn.replace("+00:00", "")))) >= this.datesearch(this.day_callapiSearch(filters.createdOn_from))
      && this.datesearch(this.day_callapiSearch(new Date(x.createdOn.replace("+00:00", "")))) <= this.datesearch(this.day_callapiSearch(filters.createdOn_to))
    );

  }

  return data;
}


}