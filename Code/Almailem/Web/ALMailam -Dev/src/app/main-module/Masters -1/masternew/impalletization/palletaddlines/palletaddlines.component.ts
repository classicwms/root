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
  selector: 'app-palletaddlines',
  templateUrl: './palletaddlines.component.html',
  styleUrls: ['./palletaddlines.component.scss']
})
export class PalletaddlinesComponent implements OnInit {


 
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
    caseDimensionUom: new FormControl(),
    caseHeight: new FormControl(),
    caseLength:  new FormControl(),
    caseWidth:  new FormControl(),
    casesPerPallet:  new FormControl(),
    companyCodeId:  new FormControl(),
    deletionIndicator:  new FormControl(),
    itemCaseQuantity:  new FormControl(),
    itemCode:  new FormControl(),
    itemPerPalletQuantity:  new FormControl(),
    languageId:  new FormControl(),
    palletDimensionUom:  new FormControl(),
    palletHeight:  new FormControl(),
    palletLength:  new FormControl(),
    palletWidth:  new FormControl(),
    palletizationIndicator: new FormControl(),
    palletizationLevel:  new FormControl(),
    plantId:  new FormControl(),
    referenceField1:  new FormControl(),
    referenceField10:  new FormControl(),
    referenceField2:  new FormControl(),
    referenceField3:  new FormControl(),
    referenceField4:  new FormControl(),
    referenceField5:  new FormControl(),
    referenceField6:  new FormControl(),
    referenceField7:  new FormControl(),
    referenceField8:  new FormControl(),
    referenceField9:  new FormControl(),
    statusId:  new FormControl(),
    warehouseId:  new FormControl(),
  });
 
  ngOnInit(): void {
    if(this.data.palletizationLevel == '1'){
      this.data.caseHeight.disable();
    }
    if(this.data.pageflow == 'Edit'){
      this.fill();
    }
    this.dropdownlist();
  }
  variantList:any[]=[];
  varianttList:any[]=[];
  palletizationlevelList:any[]=[];
  fill(){
    this.masterService.searchpalletization({companyCodeId: this.auth.companyId, warehouseId:this.auth.warehouseId, plantId:this.auth.plantId, languageId: [this.auth.languageId]}).subscribe(res => {
      this.palletizationlevelList = [];
      res.forEach(element => {
        this.palletizationlevelList.push({value: element.palletizationLevelId, label: element.palletizationLevelId + '-' + element.palletizationLevel});
      })
    });
   
      this.form.patchValue(this.data.code, { emitEvent: false });

    
  }

onpalletlevelchange(value){

  if(this.form.controls.palletizationLevel.value=="1"){
    this.form.controls.caseHeight.disable();
    this.form.controls.caseLength.disable();
    this.form.controls.caseWidth.disable();
    this.form.controls.caseDimensionUom.disable();
    this.form.controls.casesPerPallet.disable();
  }
  if(this.form.controls.palletizationLevel.value=="2"){
    this.form.controls.palletHeight.disable();
    this.form.controls.palletLength.disable();
    this.form.controls.palletWidth.disable();
    this.form.controls.palletDimensionUom.disable();
    this.form.controls.itemPerPalletQuantity.disable();
  }
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
      
      this.cas.dropdownlist.setup.palletizationlevelid.url,
    
    ]).subscribe((results) => {
 
    //this.variantList=this.cas.forLanguageFilter(results[0],this.cas.dropdownlist.setup.variantid.key);
  
    this.masterService.searchpalletization({companyCodeId: this.auth.companyId, warehouseId:this.auth.warehouseId, plantId:this.auth.plantId, languageId: [this.auth.languageId]}).subscribe(res => {
      this.palletizationlevelList = [];
      res.forEach(element => {
        this.palletizationlevelList.push({value: element.palletizationLevelId, label: element.palletizationLevelId + '-' + element.palletizationLevel});
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

