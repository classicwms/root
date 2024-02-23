import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import pdfMake from "pdfmake/build/pdfmake";
import { iwExpressLogo } from "../../../../../assets/font/iwExpress.js";

@Component({
  selector: 'app-consignments-open',
  templateUrl: './consignments-open.component.html',
  styleUrls: ['./consignments-open.component.scss']
})
export class ConsignmentsOpenComponent implements OnInit {


  disabled = false;
  step = 0;
  //dialogRef: any;

  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    this.step++;
  }

  prevStep() {
    this.step--;
  }
  form = this.fb.group({
   
  });
  panelOpenState = false;
  constructor(
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,
    private cas: CommonApiService,


  ) { }
  ngOnInit(): void {

  }

  // }
  // sub = new Subscription();
  // submitted = false;
  // fill() {
  //   this.spin.show();
  //   this.sub.add(this.service.Get(this.data.code,this.data.warehouseId,this.data.languageId,this.data.companyCodeId,this.data.plantId,).subscribe(res => {
  //     this.form.patchValue(res, { emitEvent: false });
  //    this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
  //    this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
  //    if(this.auth.userTypeId != 1){
  //     this.dropdownlist();
  //   }else{
  //     this.dropdownlistSuperAdmin();
  //   }
  //     this.spin.hide();
  //   },
  //    err => {
  //   this.cs.commonerrorNew(err);
  //   this.dropdownlist();
  //     this.spin.hide();
  //   }
  //   ));
  // }
  // dropdownlistSuperAdmin(){
  //   this.spin.show();
  //   this.cas.getalldropdownlist([
  //     this.cas.dropdownlist.setup.languageid.url,
  //     this.cas.dropdownlist.setup.companyid.url,
  //     this.cas.dropdownlist.setup.warehouseid.url,

  //   //this.cas.dropdownlist.setup.plantid.url,
  //   ]).subscribe((results) => {
  //   this.languageidList = this.cas.foreachlist2(results[0], this.cas.dropdownlist.setup.languageid.key);
  //   this.companyidList = this.cas.foreachlist2(results[1], this.cas.dropdownlist.setup.companyid.key);
  //   this.warehouseidList = this.cas.foreachlist2(results[2], this.cas.dropdownlist.setup.warehouseid.key);

  //  // this.plantidList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.plantid.key);
  //  this.masterService.searchPlant({companyCodeId: [this.form.controls.companyCodeId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
  //     this.plantidList = [];
  //     res.forEach(element => {
  //       this.plantidList.push({value: element.plantId, label: element.plantId + '-' + element.description});
  //     });
  //   });
  //  this.spin.hide();
  //   }, (err) => {
  //     this.toastr.error(err, "");
  //     this.spin.hide();
  //   });
  // }
  // languageidList: any[] = [];
  // companyidList:any[]=[];
  // warehouseidList:any[]=[];
  // plantidList:any[]=[];
  // dropdownlist(){
  //   this.spin.show();
  //   this.cas.getalldropdownlist([
  //     this.cas.dropdownlist.setup.languageid.url,
  //     this.cas.dropdownlist.setup.companyid.url,
  //     this.cas.dropdownlist.setup.warehouseid.url,
  //     this.cas.dropdownlist.setup.plantid.url,
  //   ]).subscribe((results) => {
  //   this.languageidList = this.cas.foreachlist2(results[0], this.cas.dropdownlist.setup.languageid.key);
  //   this.companyidList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.companyid.key);
  //   this.warehouseidList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.warehouseid.key);
  //  //this.plantidList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.plantid.key);
  //  this.masterService.searchPlant({companyCodeId: [this.form.controls.companyCodeId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
  //   this.plantidList = [];
  //   res.forEach(element => {
  //     this.plantidList.push({value: element.plantId, label: element.plantId + '-' + element.description});
  //   });
  // });
  //   this.spin.hide();
  //   }, (err) => {
  //     this.toastr.error(err, "");
  //     this.spin.hide();
  //   });
  // }
  // onLanguageChange(value){
  //   this.masterService.searchCompany({languageId: [value.value]}).subscribe(res => {
  //     this.companyidList = [];
  //     res.forEach(element => {
  //       this.companyidList.push({value: element.companyCodeId, label: element.companyCodeId + '-' + element.description});
  //     });
  //   });
  //   this.masterService.searchPlant({companyCodeId: [this.form.controls.companyCodeId.value], languageId: [value.value]}).subscribe(res => {
  //     this.plantidList = [];
  //     res.forEach(element => {
  //       this.plantidList.push({value: element.plantId, label: element.plantId + '-' + element.description});
  //     });
  //   });
  //   this.masterService.searchWarehouse({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, languageId: [value.value]}).subscribe(res => {
  //     this.warehouseidList = [];
  //     res.forEach(element => {
  //       this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.warehouseDesc});
  //     });
  //   });

  // }
  // onCompanyChange(value){
  //   this.masterService.searchPlant({companyCodeId: [value.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
  //     this.plantidList = [];
  //     res.forEach(element => {
  //       this.plantidList.push({value: element.plantId, label: element.plantId + '-' + element.description});
  //     });
  //   });
  //   this.masterService.searchWarehouse({companyCodeId: value.value, plantId: this.form.controls.plantId.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
  //     this.warehouseidList = [];
  //     res.forEach(element => {
  //       this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.warehouseDesc});
  //     });
  //   });

  // }
  // onPlantChange(value){
  //     this.masterService.searchWarehouse({companyCodeId: this.form.controls.companyCodeId.value, plantId: value.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
  //       this.warehouseidList = [];
  //       res.forEach(element => {
  //         this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.warehouseDesc});
  //       });
  //     });
  //    ;
  // }
  // submit(){
  //   this.submitted = true;
  //   if (this.form.invalid) {
  //     this.toastr.error(
  //       "Please fill required fields to continue",
  //       "Notification",{
  //         timeOut: 2000,
  //         progressBar: false,
  //       }
  //     );

  //     this.cs.notifyOther(true);
  //     return;
  //   }

  // this.cs.notifyOther(false);
  // this.spin.show();

  // if (this.data.code) {
  //   this.sub.add(this.service.Update(this.form.getRawValue(), this.data.code,this.data.warehouseId,this.data.languageId,this.data.companyCodeId,this.data.plantId).subscribe(res => {
  //     this.toastr.success(this.data.code + " updated successfully!","Notification",{
  //       timeOut: 2000,
  //       progressBar: false,
  //     });
  //     this.spin.hide();
  //     this.dialogRef.close();

  //   }, err => {

  //     this.cs.commonerrorNew(err);
  //     this.spin.hide();

  //   }));
  // }else{
  //   this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
  //     this.toastr.success(res.floorId + " Saved Successfully!","Notification",{
  //       timeOut: 2000,
  //       progressBar: false,
  //     });
  //     this.spin.hide();
  //     this.dialogRef.close();

  //   }, err => {
  //     this.cs.commonerrorNew(err);
  //     this.spin.hide();

  //   }));
  // }

  //  }
  //  email = new FormControl('', [Validators.required, Validators.email]);
  //  public errorHandling = (control: string, error: string = "required") => {
  //    return this.form.controls[control].hasError(error) && this.submitted;
  //  }
  //  getErrorMessage() {
  //    if (this.email.hasError('required')) {
  //      return ' Field should not be blank';
  //    }
  //    return this.email.hasError('email') ? 'Not a valid email' : '';
  //  }

  send_date=new Date();
formattedDate : any;
storeNumberList : any
generatePdf() {
  // res.totalAfterDiscount
     //Receipt List
       var dd: any;
       let headerTable: any[] = [];
       
       headerTable.push([
        { image: iwExpressLogo.headerLogo, fit: [200, 200], alignment: 'left',  bold: false, fontSize: 12, border: [false, false, false, false] },
        { text: 'Consignment', bold: true, alignment: 'right',  margin: [0, 10, 20, 0], fontSize: 30, color:'#949494', border: [false, false, false, false] },
   //     { image: workOrderLogo1.headerLogo1, fit: [180, 180], alignment: 'center',  bold: false, fontSize: 12, border: [false, false, false, false] },
       ]);
      
       dd = {
         pageSize: "A4",
         pageOrientation: "portrait",
         pageMargins: [30, 90, 30, 20],
         header(currentPage: number, pageCount: number, pageSize: any): any {
           return [
             {
               table: {
                 // layout: 'noBorders', // optional
                 // heights: [,60,], // height for each row
                 headerRows: 1,
                 widths: ['*', '*'],
                 body: headerTable
               },
               margin: [10, 20, 10, 20]
             }
           ]
         },
         styles: {
           anotherStyle: {
             bordercolor: '#6102D3'
           }
         },
         
         footer(currentPage: number, pageCount: number, pageSize: any): any {
           return [
             { text: 'This is computer generated Invoice no signature required', bold: false, border: [false, false, false, false], alignment: 'left', fontSize:9,margin:[30, -40, 0, 10] },
             {
             text: 'Page ' + currentPage + ' of ' + pageCount,
             style: 'header',
             alignment: 'center',
             bold: false,
             fontSize: 6,
             margin:[10, 0, 10, 0]
           }
           
          // { image: res.requirement == "Packing & Moving" ? workOrderLogo1.ulogistics1 : workOrderLogo1.ustorage, width: 570,   bold: false, margin: [10, -80, 0, 0], fontSize: 12, border: [false, false, false, false] },
         ]
         },
         content: ['\n'],
      //   defaultStyle,
         
       };
       let headerArray2: any[] = [];
       headerArray2.push([
        { text: 'Delivery Order No :'+''+'6767676', bold: true, border: [false, false, false, false], alignment: 'left', fontSize:11, },
        { text: ' ', bold: true, border: [false, false, false, false], alignment: 'left', fontSize:11,  },
        { text: 'Date :'+''+'29th March,2023', bold: true, border: [false, false, false, false], alignment: 'right', fontSize:11,  },
        
        ]);
        dd.content.push(
         {
            table: {
              headerRows: 1,
              widths: ['*','*','*'],
            body: headerArray2
         },
            margin: [0, 10, 10, 0]
          }
        )
 
       let headerArray: any[] = [];
       headerArray.push([
         { text: 'S.No  ', bold: true, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
         { text:  'Invoice No', bold: false, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
         { text: 'Order No ', bold: true, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
         { text: 'Order Type', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
         { text: 'Customer/Branch', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
         { text: 'Part No', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
         { text: 'Description', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
         { text: 'Delivery Qty', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        
       ]);headerArray.push([
        { text: '  ', bold: true, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
         { text:  '', bold: false, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
        { text: ' ', bold: true, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        
       ]);
       headerArray.push([
        { text: '  ', bold: true, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
         { text:  '', bold: false, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
        { text: ' ', bold: true, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        
       ]);
       headerArray.push([
        { text: '  ', bold: true, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
         { text:  '', bold: false, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
        { text: ' ', bold: true, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        
       ]);
       headerArray.push([
        { text: '  ', bold: true, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
         { text:  '', bold: false, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
        { text: ' ', bold: true, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        
       ]);
       headerArray.push([
        { text: '  ', bold: true, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
         { text:  '', bold: false, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
        { text: ' ', bold: true, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        
       ]);
       headerArray.push([
        { text: '  ', bold: true, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
         { text:  '', bold: false, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
        { text: ' ', bold: true, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        
       ]);
       headerArray.push([
        { text: '  ', bold: true, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
         { text:  '', bold: false, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
        { text: ' ', bold: true, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        
       ]);
 
       headerArray.push([
        { text: '  ', bold: true, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
         { text:  '', bold: false, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
        { text: ' ', bold: true, fontSize: 10, border: [true, true, true, true] ,alignment: 'center'},
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        { text: '', bold: true, fontSize: 10, border: [true, true, true, true],alignment: 'center' },
        
       ]);
       
       dd.content.push(
         {
           table: {
             headerRows: 1,
             widths: ['*','*','*', '*','*','*','*','*'],
             body: headerArray
           },
           margin: [0, 50, 10, 0]
         }
       )
    
     
    
   //    pdfMake.createPdf(dd).download('Invoice');
         pdfMake.createPdf(dd).open();
  
     this.spin.hide();
 }
}
