import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { MasterService } from 'src/app/shared/master.service';
@Component({
  selector: 'app-addlines',
  templateUrl: './addlines.component.html',
  styleUrls: ['./addlines.component.scss']
})
export class AddlinesComponent implements OnInit {

 
  constructor(    private fb: FormBuilder, private auth: AuthService,private masterService: MasterService,  private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService,
    private cas: CommonApiService,
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA) public data: any,) { }
    warehouseId: any;
    languageId: any;
    plantId: any;
    companyCodeId: any;
    itemCode: any;
    variantCode: any;
    variantType: any;
    variantSubCode: any;
    createdOn: any;
    createdBy:any;
    updatedBy:any;
    updatedOn:any;
    createdOnFE:any;
    id:any;
    updatedOnFE:any;
    warehouseSelection: any;
    languageSelection: any;
    companySelection: any;
    plantSelection: any;

  form = this.fb.group({
    companyCodeId: new FormControl(),
    companyIdAndDescription: new FormControl(),
    createdBy: new FormControl(),
    createdOn: new FormControl(),
    deletionIndicator: [],
    languageId: new FormControl(),
    plantId: new FormControl(),
    plantIdAndDescription: new FormControl(),
    referenceField1: new FormControl(),
    referenceField10: new FormControl(),
    referenceField2: new FormControl(),
    referenceField3: new FormControl(),
    referenceField4: new FormControl(),
    referenceField5: new FormControl(),
    referenceField6: new FormControl(),
    referenceField7: new FormControl(),
    referenceField8: new FormControl(),
    referenceField9: new FormControl(),
    specificationFrom: new FormControl(),
    specificationTo: new FormControl(),
    specificationUom:new FormControl(),
    updatedBy: new FormControl(),
    itemCode:new FormControl(),
    updatedOn: new FormControl(),
    variantCode: new FormControl(),
    variantSubCode: new FormControl(),
    variantBarcode : new FormControl(),
    variantSubType: new FormControl(),
    variantText: new FormControl(),
    variantType: new FormControl(),
    warehouseId: new FormControl(),
    createdOnFE:new FormControl(),
    updatedOnFE: new FormControl(),
    warehouseIdAndDescription: new FormControl(),
  });

  ngOnInit(): void {

    if(this.data.pageflow == 'Edit'){

      this.fill();
    }
    this.dropdownlist();
  }
  variantList:any[]=[];
  varianttList:any[]=[];
  fill(){
    this.masterService.searchvariant({companyCodeId: this.auth.companyId, warehouseId:this.auth.warehouseId, plantId:this.auth.plantId, languageId: [this.auth.languageId]}).subscribe(res => {
      this.variantList = [];
      res.forEach(element => {
        this.variantList.push({value: element.variantCode, label: element.variantCode + '-' + element.variantText});
      })
    });
    this.masterService.searchvariant({
      warehouseId: this.auth.warehouseId,
      companyCodeId: this.auth.companyId,
      plantId: this.auth.plantId,
      languageId: [this.auth.languageId],
      variantCode:[this.data.code.variantCode],
    }).subscribe(res => {

      this.varianttList = [];
      res.forEach(element => {
        this.varianttList.push({
          value: element.variantType,
          label: element.variantCode 
        });
      })
      this.form.patchValue(this.data.code, { emitEvent: false });
    });
    
  }


  onvariantChange(value) {
    console.log(value);
    this.masterService.searchvariant({companyCodeId: this.auth.companyId, warehouseId:this.auth.warehouseId, plantId:this.auth.plantId, languageId: [this.auth.languageId],variantCode:[value.value]}).subscribe(res => {
      this.varianttList = [];
      res.forEach(element => {
        this.varianttList.push({value: element.variantCode, label: element.variantType });
      })
    });
  }


  submit(){
    let obj: any = {};
    obj.data = this.form.value;
    obj.pageflow = this.data.pageflow
    this.dialogRef.close(obj);
  }
  dropdownlist(){
    this.spin.show();
    this.cas.getalldropdownlist([
      
      this.cas.dropdownlist.setup.variantid.url,
      this.cas.dropdownlist.setup.variantType.url,
    
    ]).subscribe((results) => {
 
    //this.variantList=this.cas.forLanguageFilter(results[0],this.cas.dropdownlist.setup.variantid.key);
    this.varianttList=this.cas.forLanguageFilter(results[1],this.cas.dropdownlist.setup.variantType.key);
   this.companyCodeId = (this.auth.companyId);
   this.masterService.searchvariant({companyCodeId: this.auth.companyId, warehouseId:this.auth.warehouseId, plantId:this.auth.plantId, languageId: [this.auth.languageId]}).subscribe(res => {
     this.variantList = [];
     res.forEach(element => {
       this.variantList.push({value: element.variantCode, label: element.variantCode + '-' + element.variantText});
     })
   });
    
      this.warehouseId = (this.auth.warehouseId);
      this.warehouseSelection = this.auth.warehouseId;
      this.languageId = (this.auth.languageId);

      this.languageSelection = this.auth.languageId;
      this.plantId = (this.auth.plantId);
      this.plantSelection = this.auth.plantId;
      this.companyCodeId = (this.auth.companyId);
      this.companySelection = this.auth.companyId;
    
    });
    this.spin.hide();
  }
}

