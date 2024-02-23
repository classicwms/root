import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from 'src/app/core/core';
import { MasterService } from 'src/app/shared/master.service';

@Component({
  selector: 'app-add-lines',
  templateUrl: './add-lines.component.html',
  styleUrls: ['./add-lines.component.scss']
})
export class AddLinesComponent implements OnInit {

  constructor(    private fb: FormBuilder, private auth: AuthService,  private masterService: MasterService, private toastr: ToastrService,
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA) public data: any,) { }


  form = this.fb.group({
    brandName: new FormControl(),
    businessPartnerCode: new FormControl('',[Validators.required]),
    businessPartnerType: new FormControl("1",[Validators.required]),
    companyCodeId: new FormControl(this.auth.companyId),
    createdBy: new FormControl(),
    createdOn: new FormControl(),
    deletionIndicator: new FormControl(),
    itemCode: new FormControl(),
    id: new FormControl(),
    languageId: new FormControl(this.auth.languageId),
    manufacturerCode: new FormControl(),
    manufacturerName: new FormControl(),
    mfrBarcode: new FormControl(),
    oldPartnerItemBarcode: new FormControl(),
    partnerItemBarcode: new FormControl('', [Validators.required]),
    partnerItemNo: new FormControl(),
    partnerName: new FormControl(),
    plantId: new FormControl(this.auth.plantId),
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
    statusId: new FormControl(),
    stock: new FormControl(),
    stockUom: new FormControl(),
    updatedBy: new FormControl(),
    updatedOn: new FormControl(),
    vendorItemBarcode: new FormControl(),
    warehouseId: new FormControl(this.auth.warehouseId),
  });

  ngOnInit(): void {

    if(this.data.pageflow == 'Edit'){

      this.fill();
    }
    this.masterService.searchpartner({
      warehouseId: [this.auth.warehouseId],
      companyCodeId: [this.auth.companyId],
      plantId: [this.auth.plantId],
      languageId: [this.auth.languageId],
      businessPartnerType: ["1"]
    }).subscribe(res => {
      this.partnercodeList = [];
      res.forEach(element => {
        this.partnercodeList.push({
          value: element.partnerName,
          label: element.partnerCode + '-' + element.partnerName
        });
      })
      
    });
    
  }

  fill(){
   
    this.masterService.searchpartner({
      warehouseId: [this.auth.warehouseId],
      companyCodeId: [this.auth.companyId],
      plantId: [this.auth.plantId],
      languageId: [this.auth.languageId],
      businessPartnerType: [this.data.code.businessPartnerType]
    }).subscribe(res => {
      this.partnercodeList = [];
      res.forEach(element => {
        this.partnercodeList.push({
          value: element.partnerName,
          label: element.partnerCode + '-' + element.partnerName
        });
      })
      this.form.patchValue(this.data.code, { emitEvent: false });
    });
    
  }

  partnercodeList: any[] = [];
  onpartnertytpeChange(value) {
    this.masterService.searchpartner({
      warehouseId: [this.auth.warehouseId],
      companyCodeId: [this.auth.companyId],
      plantId: [this.auth.plantId],
      languageId: [this.auth.languageId],
      businessPartnerType: [value.value]
    }).subscribe(res => {
      this.partnercodeList = [];
      res.forEach(element => {
        this.partnercodeList.push({
          value: element.partnerName,
          label: element.partnerCode + '-' + element.partnerName
        });
      })
    });
  }
  manufacturerCode:any;
  manufacturerName:any;
  onpartnercodeChange(value){
    console.log(value);
    this.form.controls.manufacturerCode.patchValue(value);
    this.form.controls.manufacturerName.patchValue(value);
  }
  


  submit(){
    if (this.form.invalid) {
      this.toastr.error(
        "Please fill required fields to continue",
        "Notification", {
          timeOut: 2000,
          progressBar: false,
        }
      );

      return;
    }

    let obj: any = {};
    obj.data = this.form.value;
    obj.pageflow = this.data.pageflow
    console.log(obj);
    this.dialogRef.close(obj);
  }
}
