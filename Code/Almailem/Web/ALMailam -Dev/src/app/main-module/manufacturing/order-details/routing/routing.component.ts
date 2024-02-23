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


export interface  variant1 {
  processNo:  string;
  processDescription:  string;
  workCenter:  string;
  baseQty:  string;
  setupTime:  string;
  machineTime:  string;
  activityTime:  string;
  unit:  string;
  status:  string;
} 
const ELEMENT_DATA1:  variant1[] = [
  {processNo:  '010', processDescription:  'cutting ',workCenter:  'CT001', baseQty: '1',setupTime: '10',machineTime: '15',
  activityTime: '4',unit: 'Min', status:'Active'},

  {processNo:  '020', processDescription:  'Molding ',workCenter:  'MO004', baseQty: '1',setupTime: '2',machineTime: '25',
  activityTime: '6',unit: 'Min', status:'Active'},

  {processNo:  '030', processDescription:  'Fabrication ',workCenter:  'FB1101', baseQty: '1',setupTime: '8',machineTime: '45',
  activityTime: '6',unit: 'Min', status:'Active'},

  {processNo:  '030', processDescription:  'Assembly ',workCenter:  'ASS001', baseQty: '1',setupTime: '15',machineTime: '30',
  activityTime: '10',unit: 'Min', status:'Active'},

  {processNo:  '030', processDescription:  'Finishing ',workCenter:  'FIN002', baseQty: '1',setupTime: '10',machineTime: '20',
  activityTime: '4',unit: 'Min', status:'Active'},

  {processNo:  '030', processDescription:  'Packaging & Delivery ',workCenter:  'PAC010', baseQty: '1',setupTime: '5',machineTime: '10',
  activityTime: '4',unit: 'Min', status:'Active'},
];


@Component({
  selector: 'app-routing',
  templateUrl: './routing.component.html',
  styleUrls: ['./routing.component.scss']
})
export class RoutingComponent implements OnInit {

  
  advanceFilterShow: boolean;
  @ViewChild('routing') routing: Table | undefined;
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
  applyFilterGlobal($event: any, stringVal: any) {
    this.routing!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
  }
  bomLines: any[] = [];
  ngOnInit(): void {
    this.OrderDetails = ELEMENT_DATA1;
  }

}
 


