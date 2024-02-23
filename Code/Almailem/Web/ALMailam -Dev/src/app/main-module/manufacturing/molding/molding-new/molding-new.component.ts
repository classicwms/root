import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { BOMService } from 'src/app/main-module/Masters -1/other-masters/bom/bom.service';

@Component({
  selector: 'app-molding-new',
  templateUrl: './molding-new.component.html',
  styleUrls: ['./molding-new.component.scss']
})
export class MoldingNewComponent implements OnInit {
  confirmedActivityTime: any;
  activityTime: any;
  confirmSetupTime: any;
  confirmedMachineTime: any;
  machineTime: any;
  setupTime: any;
  confirmedQty: any;
  inputQty: any;
 
  advanceFilterShow: boolean;
  @ViewChild('Setupstoragesection') Setupstoragesection: Table | undefined;
  OrderDetails: any;
  selectedOrderDetails : any;

  sub = new Subscription();
  isShowDiv = false;
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';
  constructor( public dialogRef: MatDialogRef<any>,
    @Inject(MAT_DIALOG_DATA) public data: any,
   // private cas: CommonApiService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public cs: CommonService,
    public bom: BOMService,
   // private excel: ExcelService,
    private fb: FormBuilder,
    private auth: AuthService,) { }
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
  
  applyFilterGlobal($event: any, stringVal: any) {
    this.Setupstoragesection!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
  }
  bomLines: any[] = [];
  ngOnInit(): void {

    this.activityTime = 6;
    this.machineTime = 25;
    this.setupTime = 5;
    this.inputQty = 1;

  // this.OrderDetails = 
  this.bomLines = [];
  this.bom.search({parentItemCode: [this.data.itemCode]}).subscribe(res => {
    res.forEach(element => {
      this.bomLines.push(element.bomLines[0]);
    });
    this.OrderDetails = this.bomLines;
  })
  }


  save(){
    this.toastr.success("Created successfully", "Notification", {
      timeOut: 2000,
      progressBar: false,
    });

    let obj: any = {};

    obj.confirmedActivityTime = this.confirmedActivityTime;
    obj.activityTime = this.activityTime;
    obj.confirmSetupTime = this.confirmSetupTime;
    obj.confirmedMachineTime = this.confirmedMachineTime;
    obj.machineTime = this.machineTime;
    obj.setupTime = this.setupTime;
    obj.confirmedQty = this.confirmedQty;
    obj.inputQty = this.inputQty;

    this.dialogRef.close("success");
  }
}
 


