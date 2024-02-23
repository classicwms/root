import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTable, MatTableDataSource } from '@angular/material/table';
//import { table } from 'console';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { DialogExampleComponent } from 'src/app/common-field/innerheader/dialog-example/dialog-example.component';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { BOMElement, BOMService } from '../bom.service';
import { ChilditemcodeComponent } from './childitemcode/childitemcode.component';

export interface PeriodicElement {

  position: number;
}


interface SelectItem {
  item_id: number;
  item_text: string;
}

// const ELEMENT_DATA: PeriodicElement[] = [
//   //{position: 1,},
// ];
@Component({
  selector: 'app-bom-new',
  templateUrl: './bom-new.component.html',
  styleUrls: ['./bom-new.component.scss']
})
export class BomNewComponent implements OnInit {

  email = new FormControl('', [Validators.required, Validators.email, Validators.pattern("[a-zA-Z0-9.-_]{1,}@[a-zA-Z.-]{2,}[.]{1}[a-zA-Z]{2,}")]);
  displayedColumns: string[] = ['no','name', 'weight', 'delete'];
  dataSource = new MatTableDataSource<any>([]);
  screenid: 1037 | undefined;
  noOfCase: number = 1;
  sub = new Subscription();
  bomNumber?: number;
  //creation of Form
  BomLine = this.fb.group({
    bomNumber: [],
    childItemCode: [, Validators.required],
    childItemQuantity: [],
    companyCode: ["1000"],
    createdBy: [],
    createdOn: [],
    //deletionIndicator: 0,
    languageId: ["EN"],
    plantId: ["1001"],
    // statusId: [1],
    updatedBy: ["string"],
    updatedOn: [],
    warehouseId: [this.auth.warehouseId],
    referenceField2: [],
  });
  rows: FormArray = this.fb.array([this.BomLine]);

  form = this.fb.group({
    bomLines: this.rows,
    bomNumber: [],
    companyCode: ["1000"],
    createdOn: [],
    createdby: [],
    //deletionIndicator: 0,
    languageId: ["EN"],
    parentItemCode: [],
    parentItemQuantity: ["1", Validators.required],
    plantId: ["1001"],
    statusId: ["1"],
    updatedOn: [],
    updatedby: [this.auth.username],
    warehouseId: [this.auth.warehouseId],
 

  });
  submitted = false;
  disabled = false;
  step = 0;
  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    this.step++;
  }

  prevStep() {
    this.step--;
  }


  // statusList: any[] = [
  //   { key: "Active", value: 'Active' },
  //   { key: "InActive", value: 'InActive' }];

  formgr: BOMElement | undefined;

  panelOpenState = false;
  constructor(
    public dialogRef: MatDialogRef<DialogExampleComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, private dialog: MatDialog,
    private service: BOMService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    private auth: AuthService,
    private fb: FormBuilder,
    private cs: CommonService,
  ) { }
  ngOnInit(): void {

    //this.dropdownlist();
    // this.form.controls.createdBy.disable();
    // this.form.controls.createdOn.disable();
    // this.form.controls.updatedBy.disable();
    // this.form.controls.updatedOn.disable();
    if (this.data.pageflow != 'New') {
      if (this.data.pageflow == 'Display')
        this.form.disable();
      //this.fill();
    
      this.sub.add(this.service.Get(this.data.code.parentItemCode, this.data.code.warehouseId).subscribe(res => {
        this.data.code = res;

      this.form.patchValue(this.data.code);
      console.log(this.data.code);
      this.bomNumber = this.data.code.bomNumber;

      let count = 0;
      this.data.code.bomLines.forEach(element => {

        console.log(element);
        if (count != 0) {
          console.log(element);
          this.addRowOnEdit(element);
        }
        count++;
      });

      this.dataSource = new MatTableDataSource<any>(this.data.code.bomLines);
      this.dataSource._updateChangeSubscription();
      this.form.patchValue({ statusId: String(this.form.controls['statusId'].value) });
    }))
    }
  }

  itemcodeList: any[] = [];
  filtertemgroupList: any[] = [];
  selectedItems: SelectItem[] = [];
  selectedItems1: SelectItem[] = [];
  multiiitemcodelist: SelectItem[] = [];
  multiSelectitemcodeList: SelectItem[] = [];


  dropdownSettings: IDropdownSettings = {
    singleSelection: true,
    idField: 'item_id',
    textField: 'item_text',
    selectAllText: 'Select All',
    unSelectAllText: 'UnSelect All',
    itemsShowLimit: 3,
    allowSearchFilter: true
  };


  dropdownlist() {
    this.spin.show();
    this.sub.add(this.service.Getitemcode().subscribe(res => {
      this.itemcodeList = res;
      this.filtertemgroupList = this.itemcodeList.filter(element => {
        return element.warehouseId === this.form.controls['warehouseId'].value;
      });
      this.filtertemgroupList.forEach(x => this.multiiitemcodelist.push({ item_id: x.itemCode, item_text: x.itemCode }))
      // this.filtertemgroupList.forEach(x => this.multiiitemcodelist.push({item_id: x.itemCode, item_text: x.itemCode + (x.description == null ? '' : '- ' + x.description)}))
      this.multiSelectitemcodeList = this.multiiitemcodelist;
      if (this.data.pageflow != 'New') {
        let tmp: SelectItem[] = [];
        tmp.push({ item_id: this.form.controls['parentItemCode'].value, item_text: this.multiSelectitemcodeList.filter(x => x.item_id == this.form.controls['parentItemCode'].value)[0].item_text });
        console.log(tmp);
        this.selectedItems = tmp;

        let tmp2: SelectItem[] = [];
        let bomLines = this.form.controls['bomLines'].value;
        console.log(bomLines);
        tmp2.push({ item_id: bomLines[0].childItemCode, item_text: this.multiSelectitemcodeList.filter(x => x.item_id == bomLines[0].childItemCode)[0].item_text });
        console.log(tmp2);
        this.selectedItems1 = tmp2;
      }

      this.spin.hide();
      this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }
  childitem(index: any): void {
    // if (this.selection.selected.length === 0) {
    //   this.toastr.error("Kindly select one Row", "",{
    //     timeOut: 2000,
    //     progressBar: false,
    //   });
    //   return;
    // }

    console.log(index);

    const dialogRef = this.dialog.open(ChilditemcodeComponent, {
      disableClose: true,
      width: '50%',
      maxWidth: '80%',
      position: { top: '9%', },
    });

    dialogRef.afterClosed().subscribe(result => {
      const bomLine = this.form.get('bomLines') as FormArray;
      bomLine.controls[index].patchValue({ childItemCode: result });


      // list.forEach((x: any) => { x.assignedUserId = result; });
      // this.spin.show();
      // this.sub.add(this.service.assignHHTUser(list, result).subscribe(res => {
      //   this.toastr.success(result + " Saved Successfully!","",{
      //     timeOut: 2000,
      //     progressBar: false,
      //   });
      //   this.spin.hide();


      // }, err => {
      //   this.cs.commonerrorNew(err);
      //   this.spin.hide();
      // }));
    });
  }

  fill() {
    this.spin.show();
    this.sub.add(this.service.Get(this.data.code.parentItemCode, this.data.code.warehouseId,).subscribe(res => {
      console.log(res)
      this.form.patchValue(res, { emitEvent: false });
      // this.form.controls.createdOn.patchValue(this.cs.dateapi(this.form.controls.createdOn.value));
      // this.form.controls.updatedOn.patchValue(this.cs.dateapi(this.form.controls.updatedOn.value));
      this.spin.hide();
    },
      err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }
    ));
  }

  submit() {
    this.form.patchValue({ updatedby: this.auth.username });
    //this.form.patchValue({ parentItemCode: this.selectedItems[0].item_id });
    //this.BomLine.patchValue({ childItemCode: this.selectedItems1[0].item_id });
    this.submitted = true;
    if (this.form.invalid) {
      this.toastr.error(
        "Please fill required fields to continue",
        "Notification", {
        timeOut: 2000,
        progressBar: false,
      }
      );

      this.cs.notifyOther(true);
      return;
    }

    this.cs.notifyOther(false);
    this.spin.show();

    let bomLines = this.form.controls['bomLines'].value;
    console.log(bomLines)
    let i = 0;
    this.form.controls['bomLines'].value.forEach(element => {

      element.referenceField2 = i+1 ;
      i ++ ;
    });
    console.log(this.form.controls['bomLines'].value)

    this.form.controls['bomLines'].patchValue((this.form.controls['bomLines'].value));
    console.log(this.form.getRawValue())

    if (this.data.code) {
      console.log(this.form.getRawValue);
      this.sub.add(this.service.Update(this.form.getRawValue(), this.data.code.parentItemCode, this.data.code.warehouseId).subscribe(res => {
        this.toastr.success(this.data.code.parentItemCode + " updated successfully!", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.spin.hide();
        this.dialogRef.close();

      }, err => {

        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    }
    else {
      this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
        this.toastr.success(res.parentItemCode + " Saved Successfully!", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.spin.hide();
        this.dialogRef.close();

      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    }
  };

  onNoClick(): void {
    this.dialogRef.close();
  }
  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }

  }

  elementdata: any;
  @ViewChild(MatTable)
  table!: MatTable<PeriodicElement>;
  add() {

    if (this.dataSource.data.length > 0) {
      this.addRow();
    }
    this.dataSource.data.push(this.dataSource.data.length);

    // this.elementdata.push(this.elementdata[0]);

    this.dataSource._updateChangeSubscription();


  }

  addRow() {
    const row = this.fb.group({
      bomNumber: [null],
      childItemCode: [],
      childItemQuantity: [],
      companyCode: ["1000"],
      createdBy: ["wms"],
      createdOn: [],
      //deletionIndicator: 0,
      languageId: ["EN"],
      plantId: ["1001"],
      // statusId: [1],
      updatedBy: ["string"],
      updatedOn: [],
      warehouseId: ["110"],
      referenceField2: [,],
    });
    this.rows.push(row);
    // if (!noUpdate) { this.updateView(); }
  }

  addRowOnEdit(bom: any) {
    const row = this.fb.group({
      bomNumber: [this.bomNumber],
      childItemCode: [bom.childItemCode],
      childItemQuantity: [bom.childItemQuantity],
      companyCode: ["1000"],
      createdBy: ["wms"],
      createdOn: [],
      //deletionIndicator: 0,
      languageId: ["EN"],
      plantId: ["1001"],
      // statusId: [1],
      updatedBy: ["string"],
      updatedOn: [],
      warehouseId: [bom.warehouseId],
      referenceField2: [,],
    });
    this.rows.push(row);
    console.log(this.rows);
    // if (!noUpdate) { this.updateView(); }
  }

  removeRow(index: any) {
    this.rows.removeAt(index);
    const rowNo = this.dataSource.data.indexOf(index);
    console.log(rowNo);
    if(rowNo == -1)
    {
      this.dataSource.data.splice(index, 1);
    }
    else
    {
      this.dataSource.data.splice(rowNo, 1);
    }
    
    this.dataSource._updateChangeSubscription();
  }


  onItemSelect(item: any) {
    console.log(item);
  }

  onSelectAll(items: any) {
    console.log(items);
  }

}



