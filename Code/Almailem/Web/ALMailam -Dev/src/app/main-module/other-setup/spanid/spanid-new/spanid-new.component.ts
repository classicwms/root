import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { SpanidService } from '../spanid.service';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { MasterService } from 'src/app/shared/master.service';
@Component({
  selector: 'app-spanid-new',
  templateUrl: './spanid-new.component.html',
  styleUrls: ['./spanid-new.component.scss']
})
export class SpanidNewComponent implements OnInit {

  disabled = false;
  step = 0;
 // dialogRef: any;

  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    this.step++;
  }

  prevStep() {
    this.step--;
  }
  form = this.fb.group({
    companyCodeId: [,Validators.required],
        plantId: [,Validators.required],
        warehouseId: [,Validators.required],
        floorId: [,Validators.required],
        storageSectionId: [,Validators.required],
        aisleId: [,Validators.required],
        rowId: [,Validators.required],
        spanId: [,Validators.required],
        languageId: [,Validators.required],
        spanDescription: [],
        deletionIndicator: [],
        referenceField1: [],
        referenceField2: [],
        referenceField3: [],
        referenceField4: [],
        referenceField5: [],
        referenceField6: [],
        referenceField7: [],
        referenceField8: [],
        referenceField9: [],
        referenceField10: [],
        createdBy: [],
        createdOn: [],
        createdOnFE: [],
        updatedBy: [],
        updatedOn: [],
        updatedOnFE: [],
        companyIdAndDescription:[],
        plantIdAndDescription:[],
        warehouseIdAndDescription:[],   
  });
  panelOpenState = false;
  constructor(
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public auth: AuthService,
    private fb: FormBuilder,
    public cs: CommonService,
    private service: SpanidService,
    private cas: CommonApiService,
    private masterService: MasterService,
  ) { }
  ngOnInit(): void {
    this.form.controls.updatedBy.disable();
    this.form.controls.updatedOnFE.disable();
    this.form.controls.createdBy.disable();
    this.form.controls.createdOnFE.disable();
    if(this.auth.userTypeId != 1){
      this.form.controls.languageId.patchValue(this.auth.languageId);
      this.form.controls.plantId.patchValue(this.auth.plantId);
      this.form.controls.companyCodeId.patchValue(this.auth.companyId);
      this.form.controls.warehouseId.patchValue(this.auth.warehouseId);
      this.form.controls.floorId.patchValue(this.form.controls.floorId.value);
      this.form.controls.storageSectionId.patchValue(this.form.controls.storageSectionId.value);
      this.form.controls.warehouseId.disable();
      this.form.controls.languageId.disable();
      this.form.controls.plantId.disable();
      this.dropdownlist();
    }else{
      this.dropdownlistSuperAdmin();
    }
    if (this.data.pageflow != 'New') {
      this.form.controls.spanId.disable();
      this.form.controls.warehouseId.disable();
      this.form.controls.languageId.disable();
      this.form.controls.plantId.disable();
   this.form.controls.floorId.disable();
      this.form.controls.companyCodeId.disable();
     //this.form.controls.rowNumber.disable();   
     this.form.controls.storageSectionId.disable();
     this.form.controls.aisleId.disable();
     this.form.controls.rowId.disable();
      if (this.data.pageflow == 'Display')
     
     // this.form.controls.spanDescription.disable();
        this.form.disable();
      this.fill();
  }
  }
  sub = new Subscription();
  submitted = false;
  fill() {
    this.spin.show();
    this.sub.add(this.service.Get(this.data.code,this.data.warehouseId,this.data.languageId,this.data.companyCodeId,this.data.plantId,this.data.aisleId,this.data.rowId,this.data.storageSectionId,this.data.floorId).subscribe(res => {
      this.form.patchValue(res, { emitEvent: false });
    this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
    this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
    if(this.auth.userTypeId != 1){
      this.dropdownlist();
    }else{
      this.dropdownlistSuperAdmin();
    }
    this.spin.hide();
    },
     err => {
    this.cs.commonerrorNew(err);
      this.spin.hide();
    }
    ));
  }
  languageidList: any[] = [];
  companyidList:any[]=[];
  warehouseidList:any[]=[];
  plantidList:any[]=[];
  flooridList:any[]=[];
  storagesectionList:any[]=[];
  rowList:any[]=[];
  aisleList:any[]=[];
  dropdownlist(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.languageid.url,
      this.cas.dropdownlist.setup.companyid.url,
      this.cas.dropdownlist.setup.warehouseid.url,
      this.cas.dropdownlist.setup.plantid.url,
      this.cas.dropdownlist.setup.floorid.url,
      this.cas.dropdownlist.setup.storagesectionid.url,
      this.cas.dropdownlist.setup.aisleid.url,
      this.cas.dropdownlist.setup.rowid.url,
    ]).subscribe((results) => {
    this.languageidList = this.cas.foreachlist2(results[0], this.cas.dropdownlist.setup.languageid.key);
    this.companyidList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.companyid.key);
    this.warehouseidList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.warehouseid.key);
    this.plantidList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.plantid.key);
    this.flooridList = this.cas.forLanguageFilter(results[4], this.cas.dropdownlist.setup.floorid.key);
     this.storagesectionList=this.cas.forLanguageFilter(results[5], this.cas.dropdownlist.setup.storagesectionid.key);
     this.aisleList=this.cas.forLanguageFilter(results[6], this.cas.dropdownlist.setup.aisleid.key);
     this.rowList=this.cas.forLanguageFilter(results[7], this.cas.dropdownlist.setup.rowid.key);
    this.masterService.searchFloor({companyCodeId: [this.auth.companyId], plantId: [this.auth.plantId], warehouseId: [this.auth.warehouseId], languageId: [this.auth.languageId]}).subscribe(res => {
      this.flooridList = [];
      res.forEach(element => {
        this.flooridList.push({value: element.floorId, label: element.floorId + '-' + element.description});
      });
    });
    this.masterService.searchstoragesection({companyCodeId: [this.auth.companyId], plantId: [this.auth.plantId], warehouseId: [this.auth.warehouseId], languageId: [this.auth.languageId]}).subscribe(res => {
      this.storagesectionList = [];
      res.forEach(element => {
        this.storagesectionList.push({value: element.storageSectionId, label: element.storageSectionId + '-' + element.storageSection});
      });
    });
    this.masterService.searchstoragesection({companyCodeId: [this.auth.companyId], plantId: [this.auth.plantId], warehouseId: [this.auth.warehouseId], languageId: [this.auth.languageId]}).subscribe(res => {
      this.storagesectionList = [];
      res.forEach(element => {
        this.storagesectionList.push({value: element.storageSectionId, label: element.storageSectionId + '-' + element.storageSection});
      });
    });
    this.masterService.searchaisle({companyCodeId: [this.auth.companyId], plantId: [this.auth.plantId], warehouseId: [this.auth.warehouseId], languageId: [this.auth.languageId]}).subscribe(res => {
      this.aisleList = [];
      res.forEach(element => {
        this.aisleList.push({value: element.aisleId, label: element.aisleId + '-' + element.aisleDescription});
      });
    });
    this.masterService.searchrow({companyCodeId: this.auth.companyId, plantId: this.auth.plantId, warehouseId: this.auth.warehouseId, languageId: [this.auth.languageId]}).subscribe(res => {
      this.rowList = [];
      res.forEach(element => {
        this.rowList.push({value: element.rowId, label: element.rowId + '-' + element.rowNumber});
      });
    });
   
   
    this.spin.hide();
    }, (err) => {
      this.toastr.error(err, "");
      this.spin.hide();
    });
  }
  dropdownlistSuperAdmin(){
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.languageid.url,
      this.cas.dropdownlist.setup.companyid.url,
      this.cas.dropdownlist.setup.warehouseid.url,
      this.cas.dropdownlist.setup.plantid.url,
      this.cas.dropdownlist.setup.floorid.url,
    this.cas.dropdownlist.setup.storagesectionid.url,
    this.cas.dropdownlist.setup.aisleid.url,
    this.cas.dropdownlist.setup.rowid.url,
    ]).subscribe((results) => {
      this.languageidList = this.cas.foreachlist2(results[0], this.cas.dropdownlist.setup.languageid.key);
  //     this.companyidList = this.cas.foreachlist2(results[1], this.cas.dropdownlist.setup.companyid.key);
  //     this.warehouseidList = this.cas.foreachlist2(results[2], this.cas.dropdownlist.setup.warehouseid.key);
  //     this.flooridList = this.cas.foreachlist2(results[4], this.cas.dropdownlist.setup.floorid.key);
  //  this.plantidList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.plantid.key);
  //  this.storagesectionList=this.cas.foreachlist2(results[5], this.cas.dropdownlist.setup.storagesectionid.key);
  //  this.aisleList=this.cas.foreachlist2(results[6], this.cas.dropdownlist.setup.aisleid.key);
  //  this.rowList=this.cas.foreachlist2(results[7], this.cas.dropdownlist.setup.rowid.key);
  this.masterService.searchCompany({languageId: [this.form.controls.languageId.value]}).subscribe(res => {
    this.companyidList = [];
     res.forEach(element => {
    this.companyidList.push({value: element.companyCodeId, label: element.companyCodeId + '-' + element.description});
     });
   });
 
 //this.plantidList = this.cas.forLanguageFilter(results[3], this.cas.dropdownlist.setup.plantid.key);
 this.masterService.searchPlant({companyCodeId: [this.form.controls.companyCodeId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
  this.plantidList = [];
  res.forEach(element => {
    this.plantidList.push({value: element.plantId, label: element.plantId + '-' + element.description});
  });
});  this.masterService.searchWarehouse({languageId: [this.form.controls.languageId.value],companyCodeId:this.form.controls.companyCodeId.value,plantId:this.form.controls.plantId.value}).subscribe(res => {
  this.warehouseidList = [];
   res.forEach(element => {
  this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.warehouseDesc});
   });
 });
   this.masterService.searchFloor({companyCodeId: [this.form.controls.companyCodeId.value], plantId: [this.form.controls.plantId.value], warehouseId: [this.form.controls.warehouseId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
    this.flooridList = [];
    res.forEach(element => {
      this.flooridList.push({value: element.floorId, label: element.floorId + '-' + element.description});
    });
  });
  this.masterService.searchstoragesection({companyCodeId: [this.form.controls.companyCodeId.value], plantId: [this.form.controls.plantId.value], warehouseId: [this.form.controls.warehouseId.value], languageId: [this.form.controls.languageId.value],floorId:[this.form.controls.floorId.value]}).subscribe(res => {
    this.storagesectionList = [];
    res.forEach(element => {
      this.storagesectionList.push({value: element.storageSectionId, label: element.storageSectionId + '-' + element.storageSection});
    });
  });

  this.masterService.searchaisle({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value, languageId: [this.form.controls.languageId.value],floorId:[this.form.controls.floorId.value]}).subscribe(res => {
    this.aisleList = [];
    res.forEach(element => {
      this.aisleList.push({value: element.aisleId, label: element.aisleId + '-' + element.aisleDescription});
    });
  });
  this.masterService.searchrow({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value, languageId: [this.form.controls.languageId.value],aisleId:[this.form.controls.aisleId.value]}).subscribe(res => {
    this.rowList = [];
    res.forEach(element => {
      this.rowList.push({value: element.rowId, label: element.rowId + '-' + element.rowNumber});
    });
  });
   this.spin.hide();
    }, (err) => {
      this.toastr.error(err, "");
      this.spin.hide();
    });
  }
  onLanguageChange(value){
    this.masterService.searchCompany({languageId: [value.value]}).subscribe(res => {
      this.companyidList = [];
      res.forEach(element => {
        this.companyidList.push({value: element.companyCodeId, label: element.companyCodeId + '-' + element.description});
      });
    });
    this.masterService.searchPlant({companyCodeId: [this.form.controls.companyCodeId.value], languageId: [value.value]}).subscribe(res => {
      this.plantidList = [];
      res.forEach(element => {
        this.plantidList.push({value: element.plantId, label: element.plantId + '-' + element.description});
      });
    });
    this.masterService.searchWarehouse({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, languageId: [value.value]}).subscribe(res => {
      this.warehouseidList = [];
      res.forEach(element => {
        this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.warehouseDesc});
      });
    });
    this.masterService.searchFloor({companyCodeId: [this.form.controls.companyCodeId.value], plantId: [this.form.controls.plantId.value], warehouseId: [this.form.controls.warehouseId.value], languageId: [value.value]}).subscribe(res => {
      this.flooridList = [];
      res.forEach(element => {
        this.flooridList.push({value: element.floorId, label: element.floorId + '-' + element.description});
      });
    });
    this.masterService.searchstoragesection({companyCodeId: [this.form.controls.companyCodeId.value], plantId: [this.form.controls.plantId.value], warehouseId: [this.form.controls.warehouseId.value],floorId:[this.form.controls.floorId.value], languageId: [value.value]}).subscribe(res => {
      this.storagesectionList = [];
      res.forEach(element => {
        this.storagesectionList.push({value: element.storageSectionId, label: element.storageSectionId + '-' + element.storageSection});
      });
    });
    this.masterService.searchaisle({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value,floorId:[this.form.controls.floorId.value],storageSectionId:[this.form.controls.storageSectionId.value], languageId: [value.value]}).subscribe(res => {
      this.aisleList = [];
      res.forEach(element => {
        this.aisleList.push({value: element.aisleId, label: element.aisleId + '-' + element.aisleDescription});
      });
    });
    this.masterService.searchrow({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value,floorId:[this.form.controls.floorId.value],storageSectionId:[this.form.controls.storageSectionId.value], languageId: [value.value]}).subscribe(res => {
      this.rowList = [];
      res.forEach(element => {
        this.rowList.push({value: element.rowId, label: element.rowId + '-' + element.rowNumber});
      });
    });
  }
  onCompanyChange(value){
    this.masterService.searchPlant({companyCodeId: [value.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.plantidList = [];
      res.forEach(element => {
        this.plantidList.push({value: element.plantId, label: element.plantId + '-' + element.description});
      });
    });
    this.masterService.searchWarehouse({companyCodeId: value.value, plantId: this.form.controls.plantId.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.warehouseidList = [];
      res.forEach(element => {
        this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.warehouseDesc});
      });
    });
    this.masterService.searchFloor({companyCodeId: [value.value], plantId: [this.form.controls.plantId.value], warehouseId: [this.form.controls.warehouseId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.flooridList = [];
      res.forEach(element => {
        this.flooridList.push({value: element.floorId, label: element.floorId + '-' + element.description});
      });
    });
    this.masterService.searchstoragesection({companyCodeId: [value.value], plantId: [this.form.controls.plantId.value], warehouseId: [this.form.controls.warehouseId.value], languageId: [this.form.controls.languageId.value],floorId:[this.form.controls.floorId.value]}).subscribe(res => {
      this.storagesectionList = [];
      res.forEach(element => {
        this.storagesectionList.push({value: element.storageSectionId, label: element.storageSectionId + '-' + element.storageSection});
      });
    });
    this.masterService.searchaisle({companyCodeId: value.value, plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value,floorId:[this.form.controls.floorId.value],storageSectionId:[this.form.controls.storageSectionId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.aisleList = [];
      res.forEach(element => {
        this.aisleList.push({value: element.aisleId, label: element.aisleId + '-' + element.aisleDescription});
      });
    });
    this.masterService.searchrow({companyCodeId: value.value, plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value,floorId:[this.form.controls.floorId.value],storageSectionId:[this.form.controls.storageSectionId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.rowList = [];
      res.forEach(element => {
        this.rowList.push({value: element.rowId, label: element.rowId + '-' + element.rowNumber});
      });
    });
  }
  onPlantChange(value){
      this.masterService.searchWarehouse({companyCodeId: this.form.controls.companyCodeId.value, plantId: value.value, languageId: [this.form.controls.languageId.value]}).subscribe(res => {
        this.warehouseidList = [];
        res.forEach(element => {
          this.warehouseidList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.warehouseDesc});
        });
      });
      this.masterService.searchFloor({companyCodeId: [this.form.controls.companyCodeId.value], plantId: [value.value], warehouseId: [this.form.controls.warehouseId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
        this.flooridList = [];
        res.forEach(element => {
          this.flooridList.push({value: element.floorId, label: element.floorId + '-' + element.description});
        });
      });
      this.masterService.searchstoragesection({companyCodeId: [this.form.controls.companyCodeId.value], plantId: [value.value], warehouseId: [this.form.controls.warehouseId.value],floorId:[this.form.controls.floorId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
        this.storagesectionList = [];
        res.forEach(element => {
          this.storagesectionList.push({value: element.storageSectionId, label: element.storageSectionId + '-' + element.storageSection});
        });
      });
      this.masterService.searchaisle({companyCodeId: this.form.controls.companyCodeId.value, plantId: value.value, warehouseId: this.form.controls.warehouseId.value,floorId:[this.form.controls.floorId.value],storageSectionId:[this.form.controls.storageSectionId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
        this.aisleList = [];
        res.forEach(element => {
          this.aisleList.push({value: element.aisleId, label: element.aisleId + '-' + element.aisleDescription});
        });
      });
      this.masterService.searchrow({companyCodeId: this.form.controls.companyCodeId.value, plantId: value.value, warehouseId: this.form.controls.warehouseId.value,floorId:[this.form.controls.floorId.value],storageSectionId:[this.form.controls.storageSectionId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
        this.rowList = [];
        res.forEach(element => {
          this.rowList.push({value: element.rowId, label: element.rowId + '-' + element.rowNumber});
        });
      });
  }

  onWarehouseChange(value){
    this.masterService.searchFloor({companyCodeId: [this.form.controls.companyCodeId.value], plantId: [this.form.controls.plantId.value], warehouseId: [value.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.flooridList = [];
      res.forEach(element => {
        this.flooridList.push({value: element.floorId, label: element.floorId + '-' + element.description});
      });
    });
    this.masterService.searchstoragesection({companyCodeId: [this.form.controls.companyCodeId.value], plantId: [this.form.controls.plantId.value], warehouseId: [value.value],floorId:[this.form.controls.floorId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.storagesectionList = [];
      res.forEach(element => {
        this.storagesectionList.push({value: element.storageSectionId, label: element.storageSectionId + '-' + element.storageSection});
      });
    });
    this.masterService.searchaisle({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId: value.value,floorId:[this.form.controls.floorId.value],storageSectionId:[this.form.controls.storageSectionId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.aisleList = [];
      res.forEach(element => {
        this.aisleList.push({value: element.aisleId, label: element.aisleId + '-' + element.aisleDescription});
      });
    });
    this.masterService.searchrow({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId: value.value,floorId:[this.form.controls.floorId.value],storageSectionId:[this.form.controls.storageSectionId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
      this.rowList = [];
      res.forEach(element => {
        this.rowList.push({value: element.rowId, label: element.rowId + '-' + element.rowNumber});
      });
    });
}
onfloorChange(value){
  this.masterService.searchstoragesection({companyCodeId: [this.form.controls.companyCodeId.value], plantId: [this.form.controls.plantId.value], warehouseId: [this.form.controls.warehouseId.value],floorId:[value.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
    this.storagesectionList = [];
    res.forEach(element => {
      this.storagesectionList.push({value: element.storageSectionId, label: element.storageSectionId + '-' + element.storageSection});
    });
  });
  this.masterService.searchaisle({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value,floorId:[value.value],storageSectionId:[this.form.controls.storageSectionId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
    this.aisleList = [];
    res.forEach(element => {
      this.aisleList.push({value: element.aisleId, label: element.aisleId + '-' + element.aisleDescription});
    });
  });
  this.masterService.searchrow({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value,floorId:[value.value],storageSectionId:[this.form.controls.storageSectionId.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
    this.rowList = [];
    res.forEach(element => {
      this.rowList.push({value: element.rowId, label: element.rowId + '-' + element.rowNumber});
    });
  });
}
onstoragesectionChange(value){
  this.masterService.searchaisle({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value,floorId:[this.form.controls.floorId.value],storageSectionId:[value.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
    this.aisleList = [];
    res.forEach(element => {
      this.aisleList.push({value: element.aisleId, label: element.aisleId + '-' + element.aisleDescription});
    });
  });
  this.masterService.searchrow({companyCodeId: this.form.controls.companyCodeId.value, plantId: this.form.controls.plantId.value, warehouseId: this.form.controls.warehouseId.value,floorId:[this.form.controls.floorId.value],storageSectionId:[value.value], languageId: [this.form.controls.languageId.value]}).subscribe(res => {
    this.rowList = [];
    res.forEach(element => {
      this.rowList.push({value: element.rowId, label: element.rowId + '-' + element.rowNumber});
    });
  });
}

  submit(){
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
  
  if (this.data.code) {
    this.sub.add(this.service.Update(this.form.getRawValue(), this.data.code,this.data.warehouseId,this.data.languageId,this.data.companyCodeId,this.data.plantId,this.data.aisleId,this.data.rowId,this.data.storageSectionId,this.data.floorId).subscribe(res => {
      this.toastr.success(this.data.code + " updated successfully!","Notification",{
        timeOut: 2000,
        progressBar: false,
      });
      this.spin.hide();
      this.dialogRef.close();
  
    }, err => {
  
      this.cs.commonerrorNew(err);
      this.spin.hide();
  
    }));
  }else{
    this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
      this.toastr.success(res.spanId + " Saved Successfully!","Notification",{
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
  
   }
   email = new FormControl('', [Validators.required, Validators.email]);
   public errorHandling = (control: string, error: string = "required") => {
     return this.form.controls[control].hasError(error) && this.submitted;
   }
   getErrorMessage() {
     if (this.email.hasError('required')) {
       return ' Field should not be blank';
     }
     return this.email.hasError('email') ? 'Not a valid email' : '';
   }
}













 



