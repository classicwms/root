import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { FormBuilder, Validators } from "@angular/forms";
import { MatTableDataSource } from "@angular/material/table";
import { Router, ActivatedRoute } from "@angular/router";
import { IDropdownSettings } from "ng-multiselect-dropdown";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { Subscription } from "rxjs";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { basicdataElement, BasicdataService } from "./basicdata.service";

// export interface  batch {


//   no: [],
//   x: [],
// }
// const ELEMENT_DATA:  batch[] = [
//   { no: "1", x: 'dropdown', },
//   { no: "2", x: 'dropdown', },
//   { no: "3", x: 'dropdown', },
//   { no: "4", x: 'dropdown', },
//   { no: "5", x: 'dropdown', },
//   { no: "6", x: 'dropdown', },
//   { no: "7", x: 'dropdown', },
//   { no: "8", x: 'dropdown', },
//   { no: "9", x: 'dropdown', },



// ];

interface SelectItem {
  id: number;
  itemName: string;
}

interface SelectStringItem {
  id: string;
  itemName: string;
}

@Component({
  selector: 'app-basic-data',
  templateUrl: './basic-data.component.html',
  styleUrls: ['./basic-data.component.scss']
})
export class BasicDataComponent implements OnInit {

  filterstoragesectionList: any[] =[];
  filterfloorList: any[] =[];
  binLocation: any;
  storageBin: any;
 
  sub = new Subscription();
  form = this.fb.group({
    //aisleNumber: [],
    binBarcode: [],
    binClassId: [],
    //binSectionId: [],
    blockReason: [],
    companyCodeId: ['1000'],
    createdBy: [],
    createdOn: [],
    //deletionIndicator: [],
    description: [],
    floorId: [],
    languageId: ['EN'],
    pickingBlock: [],
    plantId: ['1001'],
       putawayBlock: [],
    referenceField1: [],
    // referenceField10: [],
    referenceField2: [],
    referenceField3: [],
    referenceField4: [],
    referenceField5: [],
    referenceField6: [],
    // referenceField7: [],
    // referenceField8: [],
    // referenceField9: [],
    rowId: [],
    // shelfId: [],
     spanId: [],
    statusId: ['0'],
    storageBin: [],
    storageSectionId: [,Validators.required],
    //storageTypeId: [],
    updatedBy: [],
    updatedOn: [],
    warehouseId: [this.auth.warehouseId],

    });


    submitted = false;
    public errorHandling = (control: string, error: string = "required") => {
      return this.form.controls[control].hasError(error) && this.submitted;
    }
  
    // getErrorMessage() {
    //   if (this.email.hasError('required')) {
    //     return ' Field should not be blank';
    //   }
    //   return this.email.hasError('email') ? 'Not a valid email' : '';
    // }
    formgr: basicdataElement | undefined;
    constructor(
      private router: Router,
      private service: BasicdataService,
      public toastr: ToastrService,
      private cas: BasicdataService,
      private spin: NgxSpinnerService,
      private auth: AuthService,
      private fb: FormBuilder,
      private cs: CommonService,
        private route: ActivatedRoute
     //  private route: ActivatedRoute
    ) {
      if (sessionStorage.getItem('binLocation') != null && sessionStorage.getItem('binLocation') != undefined) {
        this.binLocation = JSON.parse(sessionStorage.getItem('binLocation') as '{}');
        this.storageBin = this.binLocation.storageBin;
        console.log(this.storageBin);
      }
     }
   
    
    ngOnInit(): void {
      this.dropdownlist();
    }
    

    warehouseIdList: any[] = [];
    itemgroupList: any[] = [];
    floorList: any = [];
    storagesectionList: any = [];
    selectedItems: any[] = [];
    selectedItems1: any[] = [];
    multifloorlist :  any[] = [];
    multiSelectfloorList: any[] = [];  
    multistoragelist :  any[] = [];
    multiSelectstorageList: any[] = [];


    dropdownSettings = {
      singleSelection: false, 
      text:"Select",
      selectAllText:'Select All',
      unSelectAllText:'UnSelect All',
      enableSearchFilter: true,
      badgeShowLimit: 2
    };
  


    dropdownlist() {
      this.spin.show();
      this.sub.add(this.service.GetWh().subscribe(res => {
        this.warehouseIdList = res;
        this.spin.hide();
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
       this.sub.add(this.service.Getfloor().subscribe(res => {
        this.floorList = res;
        this.filterfloorList = this.floorList.filter(element => {
          return element.warehouseId === this.form.controls['warehouseId'].value;

        }); 
        this.filterfloorList.forEach(x => this.multifloorlist.push({value: x.floorId, label: x.floorId + '-' + x.description}))
        this.multiSelectfloorList = this.multifloorlist;
        this.spin.hide();
        
      }, 
      err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
      this.sub.add(this.service.Getstorage().subscribe(res => {
        this.storagesectionList = res;
        this.filterstoragesectionList = this.storagesectionList.filter(element => {
          return element.warehouseId === this.form.controls['warehouseId'].value;

        }); 
        this.filterstoragesectionList.forEach(x => this.multistoragelist.push({value: x.storageSectionId, label: x.storageSection}))
        this.multiSelectstorageList = this.multistoragelist;
        this.multiSelectstorageList = this.cs.removeDuplicatesFromArrayNewstatus(this.multiSelectstorageList)
        this.spin.hide();

        if (this.storageBin !== undefined && this.storageBin !== null) {
          this.getStorageBinDetails();
        }
        
      }, 
      err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    }

    getStorageBinDetails()
    {
      this.sub.add(this.service.Get(this.storageBin).subscribe(res => {
        this.form.patchValue(res, { emitEvent: false });
        console.log(res);
        this.form.controls.pickingBlock.patchValue(res.pickingBlock.toString())
        this.form.controls.putawayBlock.patchValue(res.putawayBlock.toString())
        console.log(this.form.controls.pickingBlock.value)
        if (res.storageSectionId != null) {
          let filteredStorageSection = this.multiSelectstorageList.filter(x => x.value == res.storageSectionId);
          let selectItem: SelectStringItem[] = [];
          if (filteredStorageSection != null && filteredStorageSection != undefined && filteredStorageSection.length > 0) {
            selectItem.push({ id: filteredStorageSection[0].id, itemName: filteredStorageSection[0].itemName });
            this.selectedItems = selectItem;
          }
        }

        if (res.floorId != null) {
          let filteredFloor = this.multiSelectfloorList.filter(x => x.value == res.floorId);
          let selectItem: SelectItem[] = [];
          if (filteredFloor != null && filteredFloor != undefined && filteredFloor.length > 0) {
            selectItem.push({ id: filteredFloor[0].id, itemName: filteredFloor[0].itemName });
            this.selectedItems1 = selectItem;
          }
        }
        this.spin.hide();
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    }


    save() {
      if (this.storageBin != null && this.storageBin != undefined && this.storageBin != '') {
        this.updateCompanyDetails();
      }
      else {
        this.saveCompanyDetails();
      }
        
    }

      saveCompanyDetails() {
        // if (this.selectedItems1 != null && this.selectedItems1 != undefined && this.selectedItems1.length > 0) {
        // this.form.patchValue({floorId: this.selectedItems1[0].id});
        // }else{this.form.patchValue({floorId: null});}
        // if (this.selectedItems != null && this.selectedItems != undefined && this.selectedItems.length > 0) {
        //   this.form.patchValue({storageSectionId: this.selectedItems[0].id});
        //   }else{this.form.patchValue({storageSectionId: null});}
     
        this.form.patchValue({ updatedby: this.auth.username });
        this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(
      result => {
        console.log(result);
        // this.isLoadingResults = false;
        // this.successMessage = 'User created successfully';
        // this.snackBar.open(this.successMessage, 'Close', {
        //   duration: 5000,
        //   panelClass: ['bg-success']
        // });
        this.toastr.success("Basic Data details Saved Successfully","Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/masters-storage/selection']);
      }, (err: any) => {
        this.cs.commonerrorNew(err);
        //this.isLoadingResults = false;
      }
    ));
  }

  updateCompanyDetails() {
    // if (this.selectedItems != null && this.selectedItems != undefined && this.selectedItems.length > 0) {
    //   this.form.patchValue({ storageSectionId: this.selectedItems[0].id });
    // }

    // if (this.selectedItems1 != null && this.selectedItems1 != undefined && this.selectedItems1.length > 0) {
    //   this.form.patchValue({ floorId: this.selectedItems1[0].id });
    // }

    this.form.patchValue({ updatedby: this.auth.username });
    this.sub.add(this.service.Update(this.form.getRawValue(), this.storageBin).subscribe(
      result => {
        console.log(result);
        // this.form.controls.createdOn.patchValue(this.cs.dateapi(this.form.controls.createdOn.value));
        // this.isLoadingResults = false;
        // this.successMessage = 'User created successfully';
        // this.snackBar.open(this.successMessage, 'Close', {
        //   duration: 5000,
        //   panelClass: ['bg-success']
        // });
        this.toastr.success("Basic Data details Updated Successfully", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/masters-storage/selection']);
      }, (err: any) => {
        this.cs.commonerrorNew(err);
        //this.isLoadingResults = false;
      }
    ));
  }
 

    submit() {
      this.submitted = true;
      if (this.form.invalid) {
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
  
      this.cs.notifyOther(false);
      this.spin.show();
  
        this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
          this.toastr.success(res.companyCodeId + " Saved Successfully!","Notification",{
            timeOut: 2000,
            progressBar: false,
          });
          this.spin.hide();
          
  
        }, err => {
          this.cs.commonerrorNew(err);
          this.spin.hide();
  
        }));
    };
  
    ngOnDestroy() {
      if (this.sub != null) {
        this.sub.unsubscribe();
      }
  
    }


    isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  toggleFloat() {
    this.isShowDiv = !this.isShowDiv;
    this.toggle = !this.toggle;

    if (this.icon === 'expand_more') {
      this.icon = 'chevron_left';
    } else {
      this.icon = 'expand_more'
    }
    this.showFloatingButtons = !this.showFloatingButtons;
     console.log('show:' + this.showFloatingButtons);
  }

  onItemSelect(item:any){
    console.log(item);
    console.log(this.selectedItems);
}
OnItemDeSelect(item:any){
    console.log(item);
    console.log(this.selectedItems);
}
onSelectAll(items: any){
    console.log(items);
}
onDeSelectAll(items: any){
    console.log(items);
}

}