import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AuthService } from 'src/app/core/core';
import { MasterService } from 'src/app/shared/master.service';

@Component({
  selector: 'app-item-group-add-lines',
  templateUrl: './item-group-add-lines.component.html',
  styleUrls: ['./item-group-add-lines.component.scss']
})
export class ItemGroupAddLinesComponent implements OnInit {

  
  constructor(    private fb: FormBuilder, private auth: AuthService,  private masterService: MasterService,
    public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA) public data: any,) { }


  form = this.fb.group({
    companyId: new FormControl(this.auth.companyId),
    companyIdAndDescription:new FormControl(),
    createdBy: new FormControl(),
    createdOn: new FormControl(),
    deletionIndicator: new FormControl(),
    description: new FormControl(),
    itemGroupId: new FormControl(),
    itemTypeId: new FormControl(),
    itemTypeIdAndDescription: new FormControl(),
    languageId: new FormControl(),
    plantId: new FormControl(),
    plantIdAndDescription: new FormControl(),
    storageClassId: new FormControl(),
    storageSectionId: new FormControl(),
    subItemGroupId: new FormControl(),
    subItemGroupIdAndDescription: new FormControl(),
    updatedBy: new FormControl(),
    updatedOn: new FormControl(),
    warehouseId: new FormControl(),
    warehouseIdAndDescription: new FormControl(),
  });


  itemgroupList: any[] = [];
  ngOnInit(): void {
    this.masterService.searchitemgroup({companyCodeId:this.auth.companyId, warehouseId:this.auth.warehouseId, plantId:this.auth.plantId,languageId: [this.auth.languageId], itemTypeId: [this.data.itemTypeId]}).subscribe(res => {
      this.itemgroupList = [];
      res.forEach(element => {
        this.itemgroupList.push({value: element.itemGroupId, label: element.itemGroupId + '-' + element.itemGroup});
     })
    });


    if(this.data.pageflow == 'Edit'){
      this.fill();
    }
  }

  fill(){
   
    this.masterService.searchsubitemgroup({companyCodeId: this.auth.companyId, warehouseId:this.auth.warehouseId, plantId:this.auth.plantId, 
      languageId: [this.auth.languageId], itemTypeId: [this.data.code.itemTypeId],itemGroupId:[this.data.code.itemGroupId]}).subscribe(res => {
       this.subitemgroupList = [];
       res.forEach(element => {
         this.subitemgroupList.push({value: element.subItemGroupId, label: element.subItemGroupId + '-' + element.subItemGroup});
      })
     });
     this.form.patchValue(this.data.code, { emitEvent: false });
  }
  subitemgroupList: any[] = [];
  onItemgroupChange(value){
    this.masterService.searchsubitemgroup({companyCodeId: this.auth.companyId, warehouseId:this.auth.warehouseId, plantId:this.auth.plantId, 
      languageId: [this.auth.languageId], itemTypeId: [this.data.itemTypeId],itemGroupId:[value.value]}).subscribe(res => {
       this.subitemgroupList = [];
       res.forEach(element => {
         this.subitemgroupList.push({value: element.subItemGroupId, label: element.subItemGroupId + '-' + element.subItemGroup});
      })
     });
     this.masterService.searchitemgroup({companyCodeId: this.auth.companyId, warehouseId:this.auth.warehouseId, plantId:this.auth.plantId, 
      languageId: [this.auth.languageId], itemTypeId: [this.data.itemTypeId],itemGroupId:[value.value]}).subscribe(res => {
        this.form.controls.description.patchValue(res[0].itemGroup);
    });
 
  }


  submit(){
    let obj: any = {};
    obj.data = this.form.value;
    obj.pageflow = this.data.pageflow
    this.dialogRef.close(obj);
  }
}
