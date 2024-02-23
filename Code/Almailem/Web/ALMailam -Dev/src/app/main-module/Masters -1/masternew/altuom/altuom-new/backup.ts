import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { CommonApiService } from 'src/app/common-service/common-api.service';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { PartnerService } from '../../partner/partner.service';
import { AltuomService } from '../altuom.service';


@Component({
  selector: 'app-altuom-new',
  templateUrl: './altuom-new.component.html',
  styleUrls: ['./altuom-new.component.scss']
})
export class AltuomNewComponent implements OnInit {
  warehouseidDropdown: any;
  isLinear = false;
  batch: any;
  selectedbatch : any;
  constructor(private fb: FormBuilder,
    private auth: AuthService,
    public toastr: ToastrService,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService,
    private cas: CommonApiService,
    private service : AltuomService) { }



  form = this.fb.group({
    alternateUom: [],
    alternateUomBarcode: [],
    companyCodeId: [],
    createdby: [],
    createdon: [],
    createdonFE: [],
    deletionIndicator: [],
    fromUnit: [],
    itemCode: [],
    languageId: [],
    plantId: [],
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
    slNo: [],
    statusId: [],
    toUnit: [],
    uomId: [],
    updatedby: [],
    updatedon: [],
    updatedonFE: [],
    warehouseId: [],
  
  });


  filterpartnercodeList: any[] = [];
  isShowDiv = false;
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';

  toggleFloat() {

    this.isShowDiv = !this.isShowDiv;
    this.toggle = !this.toggle;

    if (this.icon === 'expand_more') {
      this.icon = 'chevron_left';
    } else {
      this.icon = 'expand_more'
    }
    this.showFloatingButtons = !this.showFloatingButtons;

  }
    
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
  submitted = false;
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
  js: any = {}
  ngOnInit(): void {
    let code = this.route.snapshot.params.code;
    this.js = this.cs.decrypt(code);

    this.form.controls.updatedby.disable();
    this.form.controls.createdby.disable();
    this.form.controls.updatedonFE.disable();
    this.form.controls.createdonFE.disable();
    if(this.auth.userTypeId != 1){
      this.form.controls.companyCodeId.patchValue(this.auth.companyId);
      this.form.controls.languageId.patchValue(this.auth.languageId);
      this.form.controls.plantId.patchValue(this.auth.plantId);
      this.dropdownlist();
      }
      if(this.auth.userTypeId == 1){
       
        this.superadmindropdownlist();
        }
    if (this.js.pageflow != 'New') {
      this.form.controls.alternateUom.disable();
      if (this.js.pageflow == 'Display')
        this.form.disable();
       this.fill();
    }
  }
  superadmindropdownlist(){
      
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.warehouseid.url,
   
     
    ]).subscribe((results) => {
    this.warehouseidDropdown = results[0];
    this.warehouseidDropdown.forEach(element => {
      this.warehouseIdList.push({value: element.warehouseId, label: element.warehouseId + '-' + element.warehouseDesc, companyCodeId: element.companyCodeId, plantId: element.plantId, languageId: element.languageId});
    });
  
  
    });
    this.spin.hide();
  }
  sub = new Subscription();
   fill(){
   this.spin.show();
   this.sub.add(this.service.Get(this.js.code,this.js.warehouseId,this.js.languageId,this.js.plantId,this.js.companyCodeId,this.js.itemCode,this.js.uomId).subscribe(res => {
     this.form.patchValue(res, { emitEvent: false });
    this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
     this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
    }))
     this.spin.hide();
  }
  warehouseIdList: any[] = [];
    partnercodeList: any[] = [];
    dropdownlist(){
      this.spin.show();
      this.cas.getalldropdownlist([
        this.cas.dropdownlist.setup.warehouseid.url,  
      ]).subscribe((results) => {
      this.warehouseIdList = this.cas.foreachlist2(results[0], this.cas.dropdownlist.setup.warehouseid.key);
      if(this.auth.userTypeId != 1){
        this.form.controls.warehouseId.patchValue(this.auth.warehouseId);  
        this.form.controls.languageId.patchValue(this.auth.languageId);
        this.form.controls.plantId.patchValue(this.auth.plantId);
        this.form.controls.companyCodeId.patchValue(this.auth.companyId);
        this.form.controls.warehouseId.disable();
      }
      
      this.spin.hide();
      }, (err) => {
        this.toastr.error(err, "");
        this.spin.hide();
      });
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
    this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
   this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));
     if (this.js.code) {
     this.sub.add(this.service.Update(this.form.getRawValue(),this.js.code,this.js.warehouseId,this.js.languageId,this.js.plantId,this.js.companyCodeId,this.js.itemCode,this.js.uomId).subscribe(res => {
      this.toastr.success(res.alternateUom + " updated successfully!","Notification",{
        timeOut: 2000,
       progressBar: false,
      });
       this.router.navigate(['/main/masternew/altuom']);

       this.spin.hide();
  
     }, err => {
  
       this.cs.commonerrorNew(err);
       this.spin.hide();
  
     }));
   }else{
     this.sub.add(this.service.Create(this.form.getRawValue()).subscribe(res => {
       this.toastr.success(res.itemGroupId + " Saved Successfully!","Notification",{
         timeOut: 2000,
         progressBar: false,
       });
      this.router.navigate(['/main/masternew/altuom']);
       this.spin.hide();
  
     }, err => {
       this.cs.commonerrorNew(err);
      this.spin.hide();
  
     }));
    }
  
   }
   onChange() {
    console.log(this.selectedbatch.length)
    const choosen= this.selectedbatch[this.selectedbatch.length - 1];   
    this.selectedbatch.length = 0;
    this.selectedbatch.push(choosen);
  } 
}

