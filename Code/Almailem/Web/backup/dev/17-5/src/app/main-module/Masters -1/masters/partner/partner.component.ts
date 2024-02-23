import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PartnerElement, PartnerService } from './partner.service';

@Component({
  selector: 'app-partner',
  templateUrl: './partner.component.html',
  styleUrls: ['./partner.component.scss']
})
export class PartnerComponent implements OnInit {
  filterpartnercodeList: any[] = [];
  sub = new Subscription();
  masterProduct: any;
  form = this.fb.group({
    
  companyCodeId: ['1000'],
  languageId: ['EN'],
  plantId: ['1001'],
  uomId: ['EA'],
  brandName: [],
  businessParnterType: [],
  businessPartnerCode: [],
  createdby: [],
  createdon:[],
  deletionIndicator: [],
  itemCode: [],
  mfrBarcode: [],
  referenceField1: [],
  referenceField10: [],
  referenceField2: [],
  referenceField3: [],
  referenceField4: [],
  referenceField5: [],
  referenceField6: [],
  referenceField7: [],
  referenceField8: [],
  referenceField9: [],
  statusId: [],
  stock: [],
  stockUom: [],
  updatedby: [],
  updatedon:[],
  vendorItemBarcode: [],
  vendorItemNo: [],
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
    formgr: PartnerElement | undefined;
    constructor(
      private router: Router,
      private service: PartnerService,
      public toastr: ToastrService,
      private cas: PartnerService,
      private spin: NgxSpinnerService,
      private auth: AuthService,
      private fb: FormBuilder,
      private cs: CommonService,
      private route: ActivatedRoute
    ) {   this.form.controls.itemCode.patchValue (this.route.snapshot.params['itemCode']);
    if (sessionStorage.getItem('masterProduct') != null && sessionStorage.getItem('masterProduct') != undefined) {
      this.masterProduct = JSON.parse(sessionStorage.getItem('masterProduct') as '{}');
      // this.itemCode = this.masterProduct.itemCode;
      // this.warehouse = this.masterProduct.warehouseId;
      console.log(this.masterProduct);

      // this.company.companyId = this.storageDetails.companyId;
      // this.companyIdForEdit = this.company.companyId;
    }
  }
   
    
    beacon = "my default value"
    ngOnInit(): void {
      this.dropdownlist();
      this.form.controls.itemCode.disable();
      this.form.controls.warehouseId.disable();
     
    }
    warehouseIdList: any[] = [];
    partnercodeList: any[] = [];
    dropdownlist() {
      this.spin.show();
      this.sub.add(this.service.GetWh().subscribe(res => {
        this.warehouseIdList = res;
        this.spin.hide();
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
      this.sub.add(this.service.Getpartnercode().subscribe(res => {
        this.partnercodeList = res;
        this.filterpartnercodeList = this.partnercodeList.filter(element => {
          return element.warehouseId === this.form.controls['warehouseId'].value;
          //      return element.warehouseId === this.form.controls['warehouseId'].value && element.businessPartnerType === this.form.controls['businessPartnerType'].value;
        }); 
        this.spin.hide();
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    }

    // getItemDetails() {
    //   this.sub.add(this.service.Get(this.itemCode).subscribe(
    //     result => {
    //       console.log(result);
    //       this.form.patchValue(result, { emitEvent: false });
    //       if (result.itemType != null) {
    //         let filteredItemType = this.multiSelectItemtypeList.filter(x => x.id = result.itemType);
    //         let selectItem: SelectItem[] = [];
    //         selectItem.push({ id: filteredItemType[0].id, itemName: filteredItemType[0].itemName });
    //         this.selectedItems = selectItem;
    //       }
  
  
  
    //       if (result.itemGroup != null) {
    //         let filteredItemGroup = this.multiSelectItemGroupList.filter(x => x.id = result.itemGroup);
    //         let selectItem2: SelectItem[] = [];
    //         selectItem2.push({ id: filteredItemGroup[0].id, itemName: filteredItemGroup[0].itemName });
    //         this.selectedItems2 = selectItem2;
    //         console.log(this.selectedItems2);
    //       }
  
    //       if (result.storageSectionId != null) {
    //         let filteredStorageSection = this.multiSelectstorageList.filter(x => x.id = result.storageSectionId);
    //         let selectItem1: SelectItem[] = [];
    //         selectItem1.push({ id: filteredStorageSection[0].id, itemName: filteredStorageSection[0].itemName });
    //         this.selectedItems1 = selectItem1;
    //       }
    //       // result.itemType = null;
    //       // result.itemGroup = null;
    //       // result.storageSectionId = null;
    //       // this.form.patchValue(result);
    //     },
    //     error => {
    //       console.log(error);
    //     }
    //   ));
    // }

    save() {
        this.saveCompanyDetails();
      
    }

      saveCompanyDetails() {
        this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(
      result => {
        console.log(result);
        // this.isLoadingResults = false;
        // this.successMessage = 'User created successfully';
        // this.snackBar.open(this.successMessage, 'Close', {
        //   duration: 5000,
        //   panelClass: ['bg-success']
        // });
        this.toastr.success("Partner details Saved Successfully","Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        this.router.navigate(['/main/masters/Alternate-uom',this.form.controls.itemCode.value]);
       
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
     
      this.form.patchValue({ updatedby: this.auth.username });
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

}