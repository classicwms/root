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

  constructor(private service: PutawayService,
    public toastr: ToastrService, public dialog: MatDialog,
    private spin: NgxSpinnerService, private router: Router,
    private auth: AuthService, public dialogRef: MatDialogRef<ReversalPopupComponent>,
    private fb: FormBuilder,
    private cs: CommonService,) { }
  sub = new Subscription();
  refDocNumber: any;
  packBarcodes: any;
  refDocNumberList: any[] = [];
  filterrefDocNumberList: any[] = [];
  packBarcodesList: any[] = [];

  statusId22 = 22

  selectedItems: SelectItem[] = [];
  selectedItems1: SelectItem[] = [];
  multipallet1list: SelectItem[] = [];
  multiSelectpallet1List: SelectItem[] = [];
  multiorderList: SelectItem[] = [];
  multiSelectorderList: SelectItem[] = [];


  dropdownSettings = {
    singleSelection: true, 
    text:"Select",
    selectAllText:'Select All',
    unSelectAllText:'UnSelect All',
    enableSearchFilter: true,
    badgeShowLimit: 2
  };


  ngOnInit(): void {

    this.spin.show();
    this.sub.add(this.service.search({}).subscribe(res => {
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
      this.filterrefDocNumberList.forEach(x => this.multiorderList.push({ id: x.refDocNumber, itemName: x.refDocNumber }))
      this.multiSelectorderList = this.multiorderList;
      this.spin.hide();

    }, err => {

      this.cs.commonerrorNew(err);
      this.spin.hide();

    }));



  }
  getpalletid() {
    this.spin.show();
    this.sub.add(this.service.search({ refDocNumber: [this.refDocNumber[0].id] }).subscribe(res => {
      this.spin.hide();
      this.packBarcodesList = res.filter(element => {
        return element.statusId != this.statusId22;
      });;
      this.filterrefDocNumberList = this.packBarcodesList;
      this.packBarcodesList.forEach(x => this.multipallet1list.push({ id: x.packBarcodes, itemName: x.packBarcodes }))
      this.multiSelectpallet1List = this.multipallet1list;
      this.spin.hide();
    }, err => {

      this.cs.commonerrorNew(err);
      this.spin.hide();

    }));
  }
  submitted = false;
  reverse() {
    this.submitted = true;
    //this.refDocNumber.patchValue({refDocNumber: this.selectedItems[0].id});
    // this.refDocNumber=this.selectedItems[0].id
    if (!this.packBarcodes) {
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

    if (!this.refDocNumber[0].id) {
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
    this.sub.add(this.service.reverse(this.refDocNumber[0].id, this.packBarcodes).subscribe(res => {
      this.spin.hide();
      this.toastr.success(this.refDocNumber[0].id + " Saved Successfully!","",{
        timeOut: 2000,
        progressBar: false,
      });
      this.dialogRef.close();
    }, err => {

      this.cs.commonerrorNew(err);
      this.spin.hide();

    }));
  }

  onItemSelect(item: any) {
    this.refDocNumber=this.selectedItems[0].id;
    this.getpalletid()
    console.log(item);

  }

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
