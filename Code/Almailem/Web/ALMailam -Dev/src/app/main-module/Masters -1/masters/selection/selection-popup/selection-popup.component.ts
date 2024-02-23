import { Component, Inject, OnInit, Optional } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { DialogExampleComponent } from 'src/app/common-field/innerheader/dialog-example/dialog-example.component';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { basicdataelement, Basicdata1Service } from '../../basic-data1/basicdata1.service';
import { MasterproductselectionService } from '../masterproductselection.service';

interface SelectItem {
  item_id: number;
  item_text: string;
}

@Component({
  selector: 'app-selection-popup',
  templateUrl: './selection-popup.component.html',
  styleUrls: ['./selection-popup.component.scss']
})
export class SelectionPopupComponent implements OnInit {
  screenid: 1020 | undefined;

  filteritemtypeList: any[] = [];
  filteritemgroupList: any[] = [];
  filteritemsubgroupList: any[] = [];
  isDisabled = false;
  routeto() {
    sessionStorage.setItem('crrentmenu', '1002');
    this.router.navigate(["/main/masters/basic-data1"]);
  }
  // routeto(url: any, id: any) {
  //   sessionStorage.setItem('crrentmenu', id);
  //   this.router.navigate([url]);
  // }

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

  panelOpenState = false;


  sub = new Subscription();
  form = this.fb.group({

    companyCodeId: ['1000'],
    createdby: [this.auth.userID,],
    createdon: [],
    deletionIndicator: [],
    description: [],
    eanUpcNo: [],
    hsnCode: [],
    itemCode: [],
    itemGroup: [],
    itemType: [],
    languageId: ['EN'],
    manufacturerPartNo: [],
    maximumStock: [],
    minimumStock: [],
    model: [],
    plantId: ['1001'],
    reorderLevel: [],
    replenishmentQty: [],
    safetyStock: [],
    specifications1: [],
    specifications2: [],
    statusId: [],
    storageSectionId: [],
    subItemGroup: [],
    totalStock: [],
    uomId: [],
    updatedby: [],
    updatedon: [],
    warehouseId: [this.auth.warehouseId],
    //
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
  formgr: basicdataelement | undefined;
  constructor(
    private router: Router,
    private service: MasterproductselectionService,
    public toastr: ToastrService,
    private cas: MasterproductselectionService,
    private spin: NgxSpinnerService,
    private auth: AuthService,
    private fb: FormBuilder,
    private cs: CommonService,
    private route: ActivatedRoute,
    @Optional() @Inject(MAT_DIALOG_DATA) public data: any
    //  private route: ActivatedRoute
  ) {
    console.log(data);
    if (data.type == 'edit') {
      this.isDisabled = true;
    }
    // this.form.patchValue({itemCode: this.route.snapshot.params['itemCode']})
    //this.form.controls.companyId = this.route.snapshot.params['itemCode'];
  }


  ngOnInit(): void {
    this.dropdownlist();
    // this.form.controls["warehouseId"].valueChanges.subscribe(selectedValue => {
    //   this.filteritemtypeList = this.itemTypeIdList.filter(element => {
    //     return element.warehouseId === this.form.controls['warehouseId'].value;
    //   });
    // })
    // //for item type
    // this.form.controls["warehouseId"].valueChanges.subscribe(selectedValue => {
    //   this.filteritemgroupList = this.itemgroupList.filter(element => {
    //     return element.warehouseId === this.form.controls['warehouseId'].value;
    //   });
    // })
    // //item sub group
    //   //for item type
    //   this.form.controls["warehouseId"].valueChanges.subscribe(selectedValue => {
    //     this.filteritemsubgroupList = this.subitemList.filter(element => {
    //       return element.warehouseId === this.form.controls['warehouseId'].value;
    //     });
    //   })
  }


  warehouseIdList: any[] = [];
  itemgroupList: any[] = [];
  itemTypeIdList: any = [];
  subitemList: any = [];
  selectedItems: SelectItem[] = [];
  selectedItems1: SelectItem[] = [];
  selectedItems2: SelectItem[] = [];
  multiitemlistList: SelectItem[] = [];
  multiSelectItemGroupList: SelectItem[] = [];
  multiitemtypelist: SelectItem[] = [];
  multiSelectItemtypeList: SelectItem[] = [];
  multiisubtemtypelist: SelectItem[] = [];
  multiSelectsubItemtypeList: SelectItem[] = [];

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
    this.form.controls.warehouseId.disable();
    this.spin.show();
    this.sub.add(this.service.GetWh().subscribe(res => {
      this.warehouseIdList = res;

      this.spin.hide();

    },
      err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    this.sub.add(this.service.Getsubitem().subscribe(res => {
      this.subitemList = res;
      this.filteritemsubgroupList = this.subitemList.filter(element => {
        return element.warehouseId === this.form.controls['warehouseId'].value;
      });
      this.filteritemsubgroupList.forEach(x => this.multiisubtemtypelist.push({ item_id: x.subItemGroupId, item_text: x.subItemGroup }))
      this.multiSelectsubItemtypeList = this.multiisubtemtypelist;
      this.spin.hide();
    },
      err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    this.sub.add(this.service.Getitemgroup().subscribe(res => {
      this.itemgroupList = res;
      this.filteritemgroupList = this.itemgroupList.filter(element => {
        return element.warehouseId === this.form.controls['warehouseId'].value;
      });
      this.filteritemgroupList.forEach(x => this.multiitemlistList.push({ item_id: x.itemGroupId, item_text: x.itemGroup }))
      this.multiSelectItemGroupList = this.multiitemlistList;
      this.spin.hide();

    },
      err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    this.sub.add(this.service.Getitemtype().subscribe(res => {
      this.itemTypeIdList = res;
      this.filteritemtypeList = this.itemTypeIdList.filter(element => {
        return element.warehouseId === this.form.controls['warehouseId'].value;
      });
      this.filteritemtypeList.forEach(x => this.multiitemtypelist.push({ item_id: x.itemTypeId, item_text: x.itemType }))
      this.multiSelectItemtypeList = this.multiitemtypelist;
      this.spin.hide();

    },
      err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
  }
  save() {
    //     this.saveCompanyDetails();
    this.router.navigate(['/main/masters/Alternate-uom']);
  }

  //     saveCompanyDetails() {
  //       this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(
  //     result => {
  //       console.log(result);
  //      // this.form.controls.createdOn.patchValue(this.cs.dateapi(this.form.controls.createdOn.value));
  //       // this.isLoadingResults = false;
  //       // this.successMessage = 'User created successfully';
  //       // this.snackBar.open(this.successMessage, 'Close', {
  //       //   duration: 5000,
  //       //   panelClass: ['bg-success']
  //       // });
  //       this.toastr.success("Basic Data details Saved Successfully");
  //       this.router.navigate(['/main/masters/Alternate-uom',this.form.controls.itemCode.value]);
  //     }, (err: any) => {
  //       this.cs.commonerrorNew(err);
  //       //this.isLoadingResults = false;
  //     }
  //   ));
  // }


  // submit() {
  //   this.submitted = true;
  //   if (this.form.invalid) {
  //     this.toastr.error(
  //       "Please fill required fields to continue",
  //       ""
  //     );

  //     this.cs.notifyOther(true);
  //     return;
  //   }

  //   this.cs.notifyOther(false);
  //   this.spin.show();

  //   this.form.patchValue({ updatedby: this.auth.username });
  //     this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
  //       this.toastr.success(res.companyCodeId + " Saved Successfully!");
  //       this.spin.hide();


  //     }, err => {
  //       this.cs.commonerrorNew(err);
  //       this.spin.hide();

  //     }));
  // };

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

  onItemSelect(item: any) {
    console.log(item);
  }

  onSelectAll(items: any) {
    console.log(items);
  }

}




