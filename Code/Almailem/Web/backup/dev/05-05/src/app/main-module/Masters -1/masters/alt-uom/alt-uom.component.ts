import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { AbstractControl, FormBuilder } from "@angular/forms";
import { MatTable, MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute, Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { BehaviorSubject, Subscription } from "rxjs";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { PeriodicElement } from "../../other-masters/bom/bom-new/bom-new.component";
import { Altuomelement, AltuomService } from "./altuom.service";



export interface  batch {


  no: string;
  x: string;
  altuom: string;
  y: string;
  base: string;
  qpc20: string;
  qpc: string;
  barcode: string;
}
// const ELEMENT_DATA:  batch[] = [
//   { no: "1", x: 'Enter', altuom: 'Enter', y: 'Enter',base: 'Enter',qpc20: 'Enter',qpc: 'Enter',barcode: 'Enter', },



// ];


@Component({
  selector: 'app-alt-uom',
  templateUrl: './alt-uom.component.html',
  styleUrls: ['./alt-uom.component.scss']
})


export class AltUomComponent implements OnInit {
  displayedColumns: string[] = ['x', 'altuom', 'y','qpc20','qpc','delete' ];
  dataSource = new MatTableDataSource<any>([]);
  selection = new SelectionModel< any>(true, []);

  ELEMENT_DATA: any[] = [];
  //dataSource = new BehaviorSubject<AbstractControl[]>([]);

 // displayedColumns: string[] = ['select',  'fromUnit', 'alternateUom', 'toUnit', 'base','qpc20Ft','qpc40Ft','alternateUomBarcode', ];
// displayedColumns: string[] = [  'fromUnit', 'alternateUom', 'toUnit','qpc20Ft','qpc40Ft','alternateUomBarcode', ];
  
  sub = new Subscription();
  form = this.fb.group({
      alternateUom: [],
      alternateUomBarcode: [],
      companyCodeId: ['1000'],
      createdby: [],
      createdon:[],
      deletionIndicator: [],
      fromUnit: [],
      itemCode: [],
      languageId: ['EN'],
      plantId: ['1001'],
      qpc20Ft: [],
      qpc40Ft: [],
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
      slNo: ['1'],
      statusId: ['1'],
      toUnit: [],
      uomId: ['EA'],
      updatedby: [],
      updatedon:[],
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
    formgr: Altuomelement | undefined;
    constructor(
      private router: Router,
      private service: AltuomService,
      public toastr: ToastrService,
      private cas: AltuomService,
      private spin: NgxSpinnerService,
      private auth: AuthService,
      private fb: FormBuilder,
      private cs: CommonService,
      private route: ActivatedRoute
    ) {
      //this.form.controls.itemCode.value({ItemCode: this.route.snapshot.params['itemCode']})
      //this.form.patchValue({itemCode: this.route.snapshot.params['itemCode']});
      this.form.controls.itemCode.patchValue (this.route.snapshot.params['itemCode']);
     }
   
    
    ngOnInit(): void {
      this.dropdownlist();
      this.form.controls.itemCode.disable();
      this.form.controls.warehouseId.disable();
    }
    warehouseIdList: any[] = [];
    dropdownlist() {
      this.spin.show();
      this.sub.add(this.service.GetWh().subscribe(res => {
        this.warehouseIdList = res;
        this.spin.hide();
      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();
      }));
    }

    elementdata: any;
    @ViewChild(MatTable)
  table!: MatTable<PeriodicElement>;
  
  add() {

    this.dataSource.data.push(this.elementdata);

    // this.elementdata.push(this.elementdata[0]);

    this.dataSource._updateChangeSubscription();
  }

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
        this.toastr.success("Alt Uom details Saved Successfully","Notification",{
          timeOut: 2000,
          progressBar: false,
        });
       this.router.navigate(['/main/masters/selection']);
 
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