import {
  Component,
  OnInit
} from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  Validators
} from '@angular/forms';
import {
  ActivatedRoute,
  Router
} from '@angular/router';
import {
  NgxSpinnerService
} from 'ngx-spinner';
import {
  ToastrService
} from 'ngx-toastr';
import {
  Subscription
} from 'rxjs';
import {
  CommonApiService
} from 'src/app/common-service/common-api.service';
import {
  CommonService
} from 'src/app/common-service/common-service.service';
import {
  AuthService
} from 'src/app/core/core';
import {
  PartnerService
} from '../../partner/partner.service';
import {
  AltuomService
} from '../altuom.service';


@Component({
  selector: 'app-altuom-new',
  templateUrl: './altuom-new.component.html',
  styleUrls: ['./altuom-new.component.scss']
})
export class AltuomNewComponent implements OnInit {
  warehouseidDropdown: any;
  isLinear = false;
  batch: any;
  selectedbatch: any;
  warehouseId: any;
  languageId: any;
  plantId: any;
  companyCodeId: any;
  itemCode: any;
    alternateUom:any;
    uomId:any;
  // id: any;
  createdOn: any;
  createdBy:any;
  updatedBy:any;
  updatedOn:any;
  createdOnFE:any;
  updatedOnFE:any;

  constructor(private fb: FormBuilder,
    private auth: AuthService,
    public toastr: ToastrService,
    // private cas: CommonApiService,
    private spin: NgxSpinnerService,
    private route: ActivatedRoute, private router: Router,
    private cs: CommonService,
    private cas: CommonApiService,
    private service: AltuomService) {}


  form: FormGroup;


  private createTableRow(): FormGroup {
    return this.fb.group({
      id: new FormControl(),
      alternateUom: new FormControl(),
      alternateUomBarcode: new FormControl(),
      companyCodeId: new FormControl(this.companyCodeId),
      deletionIndicator: new FormControl(),
      fromUnit: new FormControl(),
      itemCode: new FormControl(),
      languageId: new FormControl(this.languageId, ),
      plantId: new FormControl(this.plantId, ),
      qpc20Ft: new FormControl(),
      qpc40Ft: new FormControl(),
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
      slNo: new FormControl(),
      statusId: new FormControl(),
      toUnit: new FormControl(),
      uomId: new FormControl(),
      createdOn: new FormControl(),
      createdBy: new FormControl(),
      updatedOn: new FormControl(),
      updatedBy: new FormControl(),
      warehouseId: new FormControl(this.warehouseId, )
    });
  }

  get tableRowArray(): FormArray {
    return this.form.get('tableRowArray') as FormArray;
  }

  onDeleteRow(rowIndex: number): void {
    this.tableRowArray.removeAt(rowIndex);
  }

  addNewRow(): void {
    this.tableRowArray.push(this.createTableRow());
  }

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


  js: any = {}

  createForm(): void {
    this.form = this.fb.group({
      tableRowArray: this.fb.array([
        this.createTableRow()
      ])
    })
  }


  ngOnInit(): void {
    let code = this.route.snapshot.params.code;
    this.js = this.cs.decrypt(code);
    console.log(this.js);


    this.warehouseId = (this.auth.warehouseId);
    this.languageId = (this.auth.languageId);
    this.plantId = (this.auth.plantId);
    this.companyCodeId = (this.auth.companyId);
   

    this.dropdownlist();
    if (this.js.pageflow == 'basicDataNew') {
      this.itemCode = (this.js.basicdataresult.itemCode);
      //  this.itemCode.disable();
    }
    if(this.js.pageflow == "Edit"){
      this.fill();
    }
    this.dropdownlist();
    this.createForm();
    if (this.js.pageflow != 'New' && this.js.pageflow != 'basicDataNew') {
      if (this.js.pageflow == 'Display')
        this.form.disable();
      this.fill();
    }
    if (this.js.pageflow == 'basicDataEdit'|| this.js.pageflow == 'basicDataEdit3' || this.js.pageflow =='basicDataEdit2' || this.js.pageflow =='basicDataNew') {

      this.fill();

    }
  }




  sub = new Subscription();
  fill() {
    this.spin.show();
    if(this.js.pageflow!="Display"){
      if(this.js.pageflow=='basicDataEdit2' || this.js.pageflow=='basicDataEdit3' || this.js.pageflow=='basicDataNew' || this.js.pageflow=='basicDataEdit'){
    let obj: any = {};
    obj.companyCodeId = [this.js.basicdataresult.companyCodeId];
    obj.plantId = [this.js.basicdataresult.plantId];
    obj.languageId = [this.js.basicdataresult.languageId];
    obj.warehouseId = [this.js.basicdataresult.warehouseId];
    obj.itemCode = [this.js.basicdataresult.itemCode];

    this.sub.add(this.service.search(obj).subscribe(res => {
      console.log(this.js.pageflow);
      console.log(res.length);
      if(res.length>0){
      this.languageSelection = res[0].languageId;
      this.warehouseSelection = res[0].warehouseId;
      this.companySelection = res[0].companyCodeId;
      this.plantSelection = res[0].plantId;
      this.itemCode = res[0].itemCode;
      this.createdOn=res[0].createdOn;
      this.createdBy=res[0].createdBy;
      this.updatedOn=res[0].updatedOn;
      this.createdOnFE=this.cs.dateTimeApi(res[0].createdOn);
      this.updatedOnFE=this.cs.dateTimeApi(res[0].updatedOn);
      this.updatedBy=res[0].updatedBy;
        this.uomId=res[0].uomId;

      this.alternateUom=res[0].alternateUom;
      //this.id = res[0].id;

      res.forEach((element, index) => {
        if(index != res.length -1){
          this.addNewRow()
        }
        });
        
      this.tableRowArray.patchValue(res);
      
      }
     if(res.length==0){
      console.log(this.js.pageflow);
      this.js.pageflow='basicDataNew',
      console.log(this.js.pageflow);
      this.itemCode = (this.js.basicdataresult.itemCode);
     }
    }))
  }
    }
  if(this.js.pageflow == "Edit" || this.js.pageflow == "Display"){
    let obj: any = {};
    obj.companyCodeId = [this.js.companyCodeId];
    obj.plantId = [this.js.plantId];
    obj.languageId = [this.js.languageId];
    obj.warehouseId = [this.js.warehouseId];
    obj.itemCode = [this.js.itemCode];

    this.sub.add(this.service.search(obj).subscribe(res => {
   
      this.languageSelection = res[0].languageId;
      this.warehouseSelection = res[0].warehouseId;
      this.companySelection = res[0].companyCodeId;
      this.plantSelection = res[0].plantId;
      this.itemCode = res[0].itemCode;
       this.uomId=res[0].uomId;
      this.alternateUom=res[0].alternateUom;
      this.createdOn=res[0].createdOn;
      this.createdBy=res[0].createdBy;
      this.updatedOn=res[0].updatedOn;
      this.createdOnFE=this.cs.dateTimeApi(res[0].createdOn);
      this.updatedOnFE=this.cs.dateTimeApi(res[0].updatedOn);
      this.updatedBy=res[0].updatedBy;
      //this.id = res[0].id;

      res.forEach((element, index) => {
        console.log(res);
        if(index != res.length -1){
          this.addNewRow()
          
        }
        if(res[0] != null){
          this.tableRowArray.patchValue(res);
        }
        });
        if(this.tableRowArray.length >0){
       
      this.tableRowArray.patchValue(res);
        }
     
        
    }))
  
  }
    this.spin.hide();
  }
  companyList: any[] = [];
  plantList: any[] = [];
  languageList: any[] = [];
  warehouseIdList: any[] = [];
  partnercodeList: any[] = [];
  warehouseSelection: any;
  languageSelection: any;
  companySelection: any;
  plantSelection: any;
  dropdownlist() {
    this.spin.show();
    this.cas.getalldropdownlist([
      this.cas.dropdownlist.setup.warehouseid.url,
      this.cas.dropdownlist.setup.companyid.url,
      this.cas.dropdownlist.setup.plantid.url,
      this.cas.dropdownlist.setup.languageid.url,
    ]).subscribe((results) => {
      this.warehouseIdList = this.cas.forLanguageFilter(results[0], this.cas.dropdownlist.setup.warehouseid.key);
      this.companyList = this.cas.forLanguageFilter(results[1], this.cas.dropdownlist.setup.companyid.key);
      this.plantList = this.cas.forLanguageFilter(results[2], this.cas.dropdownlist.setup.plantid.key);
      this.languageList = this.cas.foreachlist2(results[3], this.cas.dropdownlist.setup.languageid.key);
      this.warehouseId = (this.auth.warehouseId);
      this.warehouseSelection = this.auth.warehouseId;
      this.languageId = (this.auth.languageId);
      this.languageSelection = this.auth.languageId;
      this.plantId = (this.auth.plantId);
      this.plantSelection = this.auth.plantId;
      this.companyCodeId = (this.auth.companyId);
      this.companySelection = this.auth.companyId;

      this.spin.hide();
    }, (err) => {
      this.toastr.error(err, "");
      this.spin.hide();
    });
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
  onWarehouseChange(value) {
    this.companyCodeId = (value.companyCodeId);
    this.languageId = (value.languageId);
    this.plantId = (value.plantId);
    this.warehouseId = (value.warehouseId);
  }

  Skip() {
   
    if(this.js.pageflow=='basicDataNew'){
      let paramdata = "";
      paramdata = this.cs.encrypt({pageflow: 'basicDataNew', basicdataresult: this.js.basicdataresult});
      this.router.navigate(['/main/masternew/partnerNew/' + paramdata]);
    }
      if(this.js.pageflow=='basicDataEdit'){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow: 'basicDataEdit',basicdataresult: this.js.basicdataresult});
        this.router.navigate(['/main/masternew/partnerNew/' + paramdata]);
        
      }
      if(this.js.pageflow=='basicDataEdit2'){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow: 'basicDataEdit2',basicdataresult: this.js.basicdataresult});
        this.router.navigate(['/main/masternew/partnerNew/' + paramdata]);
        
      }
      if(this.js.pageflow=='basicDataEdit3'){
        let paramdata = "";
        paramdata = this.cs.encrypt({pageflow: 'basicDataEdit3',basicdataresult: this.js.basicdataresult});
        this.router.navigate(['/main/masternew/partnerNew/' + paramdata]);
        
      }
  }
  Back(){
    if(this.js.pageflow=='basicDataNew' || this.js.pageflow=='basicDataEdit2'){
      let paramdata = "";
      paramdata = this.cs.encrypt({pageflow:'basicDataEdit2', basicdataresult: this.js.basicdataresult})
    
    
      this.router.navigate(['/main/masternew/basicdata2New/' + paramdata]);
    
    }
    if(this.js.pageflow=='basicDataEdit' || this.js.pageflow=="basicDataEdit3"){
      let paramdata = "";
      paramdata = this.cs.encrypt({pageflow:'basicDataEdit3', basicdataresult: this.js.basicdataresult})
    
    
      this.router.navigate(['/main/masternew/basicdata2New/' + paramdata]);
    
    }

    
     }
  submit() {
    this.submitted = true;
    if (this.form.invalid) {
      this.toastr.error(
        "Please fill required fields to continue",
        "Notification", {
          timeOut: 2000,
          progressBar: false,
        }
      );
      this.cs.notifyOther(true);
      return;
    }

    this.form.controls.tableRowArray.value.forEach(element => {
      console.log(this.itemCode)
      element.warehouseId = this.warehouseId;
      console.log(this.warehouseId)
      element.languageId = this.languageId;
      element.companyCodeId = this.companyCodeId;
      element.plantId = this.plantId;
      element.itemCode = this.itemCode;
      // element.uomId=this.uomId;
      // element.alternateUom=this.alternateUom;
      element.id = element.id ? element.id : null;
    })

    this.cs.notifyOther(false);
    this.spin.show();
    //   this.form.controls.createdOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.createdOn.value));
    //  this.form.controls.updatedOnFE.patchValue(this.cs.dateTimeApi(this.form.controls.updatedOn.value));

    if (this.js.pageflow == "basicDataEdit") {

      if (this.js.basicdataresult.itemCode && this.js.pageflow == 'basicDataEdit' && this.js.pageflow != "New") {
        this.sub.add(this.service.Update(this.form.controls.tableRowArray.value, this.alternateUom, this.js.basicdataresult.warehouseId, this.js.basicdataresult.languageId, this.js.basicdataresult.plantId, this.js.basicdataresult.companyCodeId, this.js.basicdataresult.itemCode, this.uomId).subscribe(res => {
          this.toastr.success(this.alternateUom + " updated successfully!", "Notification", {
            timeOut: 2000,
            progressBar: false,
          });
          if (this.js.pageflow == "basicDataEdit") {
            let paramdata = "";
            paramdata = this.cs.encrypt({
              pageflow: 'basicDataEdit',
              basicdataresult: res[0]
            });
            this.router.navigate(['/main/masternew/partnerNew/' + paramdata]);
            this.spin.hide();
          };

        }, err => {

          this.cs.commonerrorNew(err);
          this.spin.hide();

        }));
      }
    } 
    if (this.js.pageflow == "basicDataEdit2") {

      if (this.js.basicdataresult.itemCode && this.js.pageflow == 'basicDataEdit2' && this.js.pageflow != "New") {
        this.sub.add(this.service.Update(this.form.controls.tableRowArray.value, this.alternateUom, this.js.basicdataresult.warehouseId, this.js.basicdataresult.languageId, this.js.basicdataresult.plantId, this.js.basicdataresult.companyCodeId, this.js.basicdataresult.itemCode, this.uomId).subscribe(res => {
          this.toastr.success(this.alternateUom + " updated successfully!", "Notification", {
            timeOut: 2000,
            progressBar: false,
          });
          if (this.js.pageflow == "basicDataEdit2") {
            let paramdata = "";
            paramdata = this.cs.encrypt({
              pageflow: 'basicDataEdit2',
              basicdataresult: res[0]
            });
            this.router.navigate(['/main/masternew/partnerNew/' + paramdata]);
            this.spin.hide();
          };

        }, err => {

          this.cs.commonerrorNew(err);
          this.spin.hide();

        }));
      }
    }
    if (this.js.pageflow == "Edit") {

      if (this.js.itemCode && this.js.pageflow == 'Edit' && this.js.pageflow != "New") {
        this.sub.add(this.service.Update(this.form.controls.tableRowArray.value, this.alternateUom, this.js.warehouseId, this.js.languageId, this.js.plantId, this.js.companyCodeId, this.js.itemCode, this.uomId).subscribe(res => {
          this.toastr.success(this.alternateUom + " updated successfully!", "Notification", {
            timeOut: 2000,
            progressBar: false,
          });
        
            this.router.navigate(['/main/masternew/altuom' ]);
            this.spin.hide();
      

        }, err => {

          this.cs.commonerrorNew(err);
          this.spin.hide();

        }));
      }
    }
    if (this.js.pageflow == "basicDataEdit3") {

      if (this.js.basicdataresult.itemCode && this.js.pageflow == 'basicDataEdit3' && this.js.pageflow != "New") {
        this.sub.add(this.service.Update(this.form.controls.tableRowArray.value, this.alternateUom, this.js.basicdataresult.warehouseId, this.js.basicdataresult.languageId, this.js.basicdataresult.plantId, this.js.basicdataresult.companyCodeId, this.js.basicdataresult.itemCode, this.uomId).subscribe(res => {
          this.toastr.success(this.alternateUom + " updated successfully!", "Notification", {
            timeOut: 2000,
            progressBar: false,
          });
          if (this.js.pageflow == "basicDataEdit3") {
            let paramdata = "";
            paramdata = this.cs.encrypt({
              pageflow: 'basicDataEdit3',
              basicdataresult: res[0]
            });
            this.router.navigate(['/main/masternew/partnerNew/' + paramdata]);
            this.spin.hide();
          };

        }, err => {

          this.cs.commonerrorNew(err);
          this.spin.hide();

        }));
      }
    } else {
      if(this.itemCode == null){
        this.toastr.error( "Please Enter Part No to Continue ","Notification",{
          timeOut: 2000,
          progressBar: false,
        });
        this.spin.hide();
        return
      }
      if(this.itemCode != null && this.js.pageflow != "Edit"){
      this.sub.add(this.service.Create(this.form.controls.tableRowArray.value).subscribe(res => {
        this.toastr.success(res[0].alternateUom + " Saved Successfully!", "Notification", {
          timeOut: 2000,
          progressBar: false,
        });
        if(this.js.pageflow=="basicDataNew"){
        let paramdata = "";
        paramdata = this.cs.encrypt({
          pageflow: 'basicDataNew',
          basicdataresult: res[0]
        });
        this.router.navigate(['/main/masternew/partnerNew/' + paramdata]);
        //console.log(paramdata)
        this.spin.hide();
      }
      if(this.js.pageflow=="New"){
        this.router.navigate(['/main/masternew/altuom']);
      }

      }, err => {
        this.cs.commonerrorNew(err);
        this.spin.hide();

      }));
    }
  }
  }
}
