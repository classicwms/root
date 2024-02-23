import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PutawayService } from '../../putaway/putaway.service';


interface SelectItem {
  id: number;
  itemName: string;
}


@Component({
  selector: 'app-reversal-popup',
  templateUrl: './reversal-popup.component.html',
  styleUrls: ['./reversal-popup.component.scss']
})
export class ReversalPopupComponent implements OnInit {
screenid=3056;
  constructor(private service: PutawayService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    private auth: AuthService, public dialogRef: MatDialogRef<ReversalPopupComponent>,
    private fb: FormBuilder,
    private cs: CommonService,) { }
  sub = new Subscription();
  referenceField5:any;
  refDocNumber: any;
  packBarcodes: any;
  refDocNumberList: any[] = [];
  filterrefDocNumberList: any[] = [];
  packBarcodesList: any[] = [];
  selectedputaway: any[] = [];
  statusId22 = 22
  putaway:any;
  selectedItems: SelectItem[] = [];
  selectedItems1: SelectItem[] = [];
  multipallet1list: SelectItem[] = [];
  multiSelectpallet1List: SelectItem[] = [];
  multiorderList: any[] = [];
  multiSelectorderList: any[] = [];
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
  panelOpenState = false;
  panelOpenState1 = false;
  panelOpenState2 = false;
  dropdownSettings = {
    singleSelection: true, 
    text:"Select",
    selectAllText:'Select All',
    unSelectAllText:'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2
  };


  ngOnInit(): void {
    let obj: any = {};
    obj.companyCodeId = [this.auth.companyId];
    obj.plantId = [this.auth.plantId];
   obj.languageId = [this.auth.languageId];
   obj.warehouseId = [this.auth.warehouseId];
   obj.statusId=[20, 19];
    this.spin.show();
    this.sub.add(this.service.searchv2(obj).subscribe(res => {
      this.spin.hide();
      const key = 'refDocNumber';

      const arrayUniqueByKey = [...new Map(res.map((item: any) =>
        [item[key], item])).values()];

      this.refDocNumberList = arrayUniqueByKey;
      this.filterrefDocNumberList = this.refDocNumberList
      // .filter(element => {
      // return element.statusId != this.statusId22;
      //});;
    //  console.log(this.filterrefDocNumberList)
      this.filterrefDocNumberList.forEach(x => this.multiorderList.push({ value: x.refDocNumber, label: x.refDocNumber}))
      this.multiSelectorderList = this.multiorderList;
      this.spin.hide();

    }, err => {

      this.cs.commonerrorNew(err);
      this.spin.hide();

    }));



  }
  multibarcodeList:any[]=[];
  // getpalletid() {
  //   this.multibarcodeList = [];
  //   this.spin.show();
  //   this.sub.add(this.service.searchv2({ refDocNumber: [this.refDocNumber],companyCodeId:[this.auth.companyId],plantId:[this.auth.plantId],warehouseId:[this.auth.warehouseId],statusId:[20,19] }).subscribe(res => {
  //     this.spin.hide();
  //     res.forEach(x => this.multibarcodeList.push({ id: x.referenceField5, label:x.referenceField5 }));
  //     this.spin.hide();
  //   }, err => {

  //     this.cs.commonerrorNew(err);
  //     this.spin.hide();

  //   }));
  // }
  submitted = false;
  reverse() {
   // this.packBarcodes=this.referenceField5;
    this.submitted = true;
    //this.refDocNumber.patchValue({refDocNumber: this.selectedItems[0].id});
    // this.refDocNumber=this.selectedItems[0].id



    if (!this.refDocNumber) {
      this.toastr.error(
        "Please fill required fields to continue",
        "Notification",{
          timeOut: 2000,
          progressBar: false,
        }
      );

      this.cs.notifyOther(true);
      return;
    }
  
    this.spin.show();
    // this.selectedputaway.forEach(x => {
    //   let obj: any = {};
    //   obj.companyCodeId = this.auth.companyId,
    //   obj.itemCode= x.itemCode,
    //   obj.languageId= this.auth.languageId,
    //   obj.manufacturerName= x.manufacturerName,
    //   obj.packBarcodes= x.packBarcodes,
    //   obj.plantId= this.auth.plantId,
    //   obj.putAwayNumber= x.putAwayNumber,
    //   obj.refDocNumber= this.refDocNumber,
    //   obj.warehouseId= this.auth.warehouseId,
      this.selectedputaway.filter(x => x.itemCode = x.referenceField5);
      this.sub.add(this.service.reverseNew(this.selectedputaway).subscribe(res => {
        this.spin.hide();
        this.toastr.success(this.refDocNumber + " Saved Successfully!","",{
          timeOut: 2000,
          progressBar: false,
        });
        this.dialogRef.close();
   
      }, err => {
    
        this.cs.commonerrorNew(err);
        this.spin.hide();
  
      }));
  //  })
  }
  
onItemchnage(){
  let data: any[] = [];
  let obj: any = {};
  obj.companyCodeId = [this.auth.companyId];
  obj.plantId = [this.auth.plantId];
 obj.languageId = [this.auth.languageId];
 obj.warehouseId = [this.auth.warehouseId];
 obj.refDocNumber=[this.refDocNumber];
// obj.itemCode=[this.referenceField5.id];
  this.sub.add(this.service.searchv2(obj).subscribe(res => {
    this.putaway=res;
    this.selectedputaway=res;
    data.forEach((x: any) => {
      x.selectedputaway.packBarcodes = res.packBarcodes;
    });
  }
  
  ))
  this.panelOpenState1 = !this.panelOpenState1;

}
  // onItemSelect(item: any) {
  //   this.refDocNumber=this.selectedItems[0].id;
  //   this.getpalletid()
  //   console.log(item);

  // }

  onSelectAll(items: any) {
    console.log(items);
  }
  onDeSelectAll(items: any){
    console.log(items);
}
OnItemDeSelect(item:any){
  console.log(item);
  console.log(this.selectedItems);
}
}
