import {
  Component,
  Inject,
  OnInit
} from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormControl,
  FormArray,
  Validators
} from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef
} from '@angular/material/dialog';
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
} from 'src/app/main-module/Masters -1/masternew/partner/partner.service';
import {
  MasterService
} from 'src/app/shared/master.service';

@Component({
  selector: 'app-add-lines',
  templateUrl: './add-lines.component.html',
  styleUrls: ['./add-lines.component.scss']
})
export class AddLinesComponent implements OnInit {

  warehouseidDropdown: any;
  isLinear = false;
  batch: any;
  selectedbatch: any;
  warehouseId: any;
  languageId: any;
  plantId: any;
  companyCodeId: any;
  itemCode: any;
  businessPartnerCode: any;
  businessPartnerType: any;
  partnerItemBarcode: any;
  createdOn: any;
  createdBy: any;
  updatedBy: any;
  updatedOn: any;
  createdOnFE: any;
  id: any;
  updatedOnFE: any;
  constructor(private fb: FormBuilder, private auth: AuthService, private masterService: MasterService, private spin: NgxSpinnerService,
    public dialogRef: MatDialogRef < any > ,
    @Inject(MAT_DIALOG_DATA) public data: any, private cs: CommonService) {}



  form = this.fb.group({
    companyId: new FormControl(this.auth.companyId),
    companyIdAndDescription: [],
    createdBy: [],
    createdOn: [],
    deletionIndicator: [],
    description: [],
    languageId: new FormControl(this.auth.languageId),
    plantId: new FormControl(this.auth.plantId),
    plantIdAndDescription: [],
    priority1: [],
    priority10: [],
    priority2: [],
    priority3: [],
    priority4: [],
    priority5: [],
    priority6: [],
    priority7: [],
    priority8: [],
    priority9: [],
    sequenceIndicator: [],
    strategyNo: [],
    strategyNoText: [],
    strategyTypeId: [],
    updatedBy: [],
    updatedOn: [],
    warehouseId: new FormControl(this.auth.warehouseId),
    warehouseIdAndDescription: [],
  })


  stratergyList: any[] = [];
  ngOnInit(): void {
    this.masterService.searchstrategy({
      companyCodeId: this.auth.companyId,
      warehouseId: this.auth.warehouseId,
      plantId: this.auth.plantId,
      languageId: [this.auth.languageId]
    }).subscribe(res => {
      this.stratergyList = [];
      res.forEach(element => {
        this.stratergyList.push({
          value: element.strategyTypeId,
          label: element.strategyTypeId + '-' + element.strategyTypeText
        });
      })
      this.stratergyList = this.cs.removeDuplicatesFromArrayNewstatus(this.stratergyList);
    });

    if (this.data.pageflow == 'Edit') {

      this.fill();
    }
    if (this.data.pageflow == 'Display') {
      this.fill();
      this.form.disable();
    }
  }


  fill() {
    this.spin.show();
    this.masterService.searchstrategy({
      companyCodeId: this.auth.companyId,
      warehouseId: this.auth.warehouseId,
      plantId: this.auth.plantId,
      languageId: [this.auth.languageId],
      strategyTypeId: [this.data.code.strategyTypeId]
    }).subscribe(res => {
      this.priority1List = [];
      res.forEach(element => {
        this.priority1List.push({
          value: element.strategyNo,
          label: element.strategyNo + '-' + element.strategyText
        });
        this.priority1List = this.cs.removeDuplicatesFromArrayNewstatus(this.priority1List);

        this.priority2List.push({
          value: element.strategyNo,
          label: element.strategyNo + '-' + element.strategyText
        });
        this.priority2List = this.cs.removeDuplicatesFromArrayNewstatus(this.priority2List);

        this.priority3List.push({
          value: element.strategyNo,
          label: element.strategyNo + '-' + element.strategyText
        });
        this.priority3List = this.cs.removeDuplicatesFromArrayNewstatus(this.priority3List);

        this.priority4List.push({
          value: element.strategyNo,
          label: element.strategyNo + '-' + element.strategyText
        });
        this.priority4List = this.cs.removeDuplicatesFromArrayNewstatus(this.priority4List);

        this.priority5List.push({
          value: element.strategyNo,
          label: element.strategyNo + '-' + element.strategyText
        });
        this.priority5List = this.cs.removeDuplicatesFromArrayNewstatus(this.priority5List);

        this.priority6List.push({
          value: element.strategyNo,
          label: element.strategyNo + '-' + element.strategyText
        });
        this.priority6List = this.cs.removeDuplicatesFromArrayNewstatus(this.priority6List);

        this.priority7List.push({
          value: element.strategyNo,
          label: element.strategyNo + '-' + element.strategyText
        });
        this.priority7List = this.cs.removeDuplicatesFromArrayNewstatus(this.priority7List);

        this.priority8List.push({
          value: element.strategyNo,
          label: element.strategyNo + '-' + element.strategyText
        });
        this.priority8List = this.cs.removeDuplicatesFromArrayNewstatus(this.priority8List);

        this.priority9List.push({
          value: element.strategyNo,
          label: element.strategyNo + '-' + element.strategyText
        });
        this.priority9List = this.cs.removeDuplicatesFromArrayNewstatus(this.priority9List);

        this.priority10List.push({
          value: element.strategyNo,
          label: element.strategyNo + '-' + element.strategyText
        });
        this.priority10List = this.cs.removeDuplicatesFromArrayNewstatus(this.priority10List);
      })
      this.form.patchValue(this.data.code, {
        emitEvent: false
      });
      this.form.controls.priority1.value ? this.form.controls.priority1.patchValue(this.data.code.priority1.toString()) : '';
      this.form.controls.priority2.value ? this.form.controls.priority2.patchValue(this.data.code.priority2.toString()) : '';
      this.form.controls.priority3.value ? this.form.controls.priority3.patchValue(this.data.code.priority3.toString()) : '';
      this.form.controls.priority4.value ? this.form.controls.priority4.patchValue(this.data.code.priority4.toString()) : '';
      this.form.controls.priority5.value ? this.form.controls.priority5.patchValue(this.data.code.priority5.toString()) : '';
      this.form.controls.priority6.value ? this.form.controls.priority6.patchValue(this.data.code.priority6.toString()) : '';
      this.form.controls.priority7.value ? this.form.controls.priority7.patchValue(this.data.code.priority7.toString()) : '';
      this.form.controls.priority8.value ? this.form.controls.priority8.patchValue(this.data.code.priority8.toString()) : '';
      this.form.controls.priority9.value ? this.form.controls.priority9.patchValue(this.data.code.priority9.toString()) : '';
      this.form.controls.priority10.value ? this.form.controls.priority10.patchValue(this.data.code.priority10.toString()) : '';
      this.spin.hide();
    })


  }



  submit() {
    let obj: any = {};
    obj.data = this.form.value;
    obj.pageflow = this.data.pageflow
    this.dialogRef.close(obj);
  }

  priority1List: any[] = [];
  priority2List: any[] = [];
  priority3List: any[] = [];
  priority4List: any[] = [];
  priority5List: any[] = [];
  priority6List: any[] = [];
  priority7List: any[] = [];
  priority8List: any[] = [];
  priority9List: any[] = [];
  priority10List: any[] = [];

  selectedStratergyType(e) {
    this.masterService.searchstrategy({
      companyCodeId: this.auth.companyId,
      warehouseId: this.auth.warehouseId,
      plantId: this.auth.plantId,
      languageId: [this.auth.languageId],
      strategyTypeId: [e.value]
    }).subscribe(res => {
      this.priority1List = [];
      res.forEach(element => {
        this.priority1List.push({
          value: element.strategyNo,
          label: element.strategyNo + '-' + element.strategyText
        });
      })
    })
    let noofStratergy: any[] = [];
    this.data.lines.forEach(x => {
      console.log(x)
      if (x.strategyTypeId == e.value) {
        noofStratergy.push(x)
      }
    })
    this.form.controls.sequenceIndicator.patchValue(noofStratergy.length);
    console.log(noofStratergy.length)
  }


  priority1Selected(e) {
    if (this.data.pageflow != 'Edit') {
      const optionsCopy = [...this.priority1List];
      for (const selectedValue of e.value) {
        const index = optionsCopy.findIndex(option => option.value === selectedValue);
        if (index !== -1) {
          optionsCopy.splice(index, 1);
        }
      }
      this.priority2List = optionsCopy;
    }
  }
  priority2Selected(e) {
    if (this.data.pageflow != 'Edit') {
      const optionsCopy = [...this.priority2List];
      for (const selectedValue of e.value) {
        const index = optionsCopy.findIndex(option => option.value === selectedValue);
        if (index !== -1) {
          optionsCopy.splice(index, 1);
        }
      }
      this.priority3List = optionsCopy;
    }
  }
  priority3Selected(e) {
    if (this.data.pageflow != 'Edit') {
      const optionsCopy = [...this.priority3List];
      for (const selectedValue of e.value) {
        const index = optionsCopy.findIndex(option => option.value === selectedValue);
        if (index !== -1) {
          optionsCopy.splice(index, 1);
        }
      }
      this.priority4List = optionsCopy;
    }
  }
  priority4Selected(e) {
    if (this.data.pageflow != 'Edit') {
      const optionsCopy = [...this.priority4List];
      for (const selectedValue of e.value) {
        const index = optionsCopy.findIndex(option => option.value === selectedValue);
        if (index !== -1) {
          optionsCopy.splice(index, 1);
        }
      }
      this.priority5List = optionsCopy;
    }
  }
  priority5Selected(e) {
    if (this.data.pageflow != 'Edit') {
      const optionsCopy = [...this.priority5List];
      for (const selectedValue of e.value) {
        const index = optionsCopy.findIndex(option => option.value === selectedValue);
        if (index !== -1) {
          optionsCopy.splice(index, 1);
        }
      }
      this.priority6List = optionsCopy;
    }
  }

  priority6Selected(e) {
    if (this.data.pageflow != 'Edit') {
      const optionsCopy = [...this.priority6List];
      for (const selectedValue of e.value) {
        const index = optionsCopy.findIndex(option => option.value === selectedValue);
        if (index !== -1) {
          optionsCopy.splice(index, 1);
        }
      }
      this.priority7List = optionsCopy;
    }
  }

  priority7Selected(e) {
    if (this.data.pageflow != 'Edit') {
      const optionsCopy = [...this.priority7List];
      for (const selectedValue of e.value) {
        const index = optionsCopy.findIndex(option => option.value === selectedValue);
        if (index !== -1) {
          optionsCopy.splice(index, 1);
        }
      }
      this.priority8List = optionsCopy;
    }
  }

  priority8Selected(e) {
    if (this.data.pageflow != 'Edit') {
      const optionsCopy = [...this.priority8List];
      for (const selectedValue of e.value) {
        const index = optionsCopy.findIndex(option => option.value === selectedValue);
        if (index !== -1) {
          optionsCopy.splice(index, 1);
        }
      }
      this.priority9List = optionsCopy;
    }
  }

  priority9Selected(e) {
    if (this.data.pageflow != 'Edit') {
      const optionsCopy = [...this.priority9List];
      for (const selectedValue of e.value) {
        const index = optionsCopy.findIndex(option => option.value === selectedValue);
        if (index !== -1) {
          optionsCopy.splice(index, 1);
        }
      }
      this.priority10List = optionsCopy;
    }
  }
}
