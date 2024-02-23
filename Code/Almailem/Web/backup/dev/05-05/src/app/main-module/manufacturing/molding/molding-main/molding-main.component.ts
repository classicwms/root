import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { CuttingNewComponent } from '../../cutting/cutting-new/cutting-new.component';
import { MoldingNewComponent } from '../molding-new/molding-new.component';


export interface  variant1 {
  orderDetails:  string;
  itemCode:  string;
  description:  string;
  inputQty:  string;
  confirmedQty:  string;
  confirmedBy:  string;
  confirmedOn:  string;
  actions:  string;
  status:  string;
} 
const ELEMENT_DATA1:  variant1[] = [
  {orderDetails:  '123',itemCode:  '0057510479',description:  'description',inputQty: '3',confirmedQty: '3',confirmedBy: '5',
  confirmedOn: '05-04-2023',actions: '15-04-2023', status:'Open'}
];


@Component({
  selector: 'app-molding-main',
  templateUrl: './molding-main.component.html',
  styleUrls: ['./molding-main.component.scss']
})
export class MoldingMainComponent implements OnInit {

  advanceFilterShow: boolean;
  @ViewChild('Setupstoragesection') Setupstoragesection: Table | undefined;
  OrderDetails: any;
  selectedOrderDetails : any;

  sub = new Subscription();
  isShowDiv = false;
  showFloatingButtons: any;
  toggle = true;
  public icon = 'expand_more';
  constructor(public dialog: MatDialog,
   // private cas: CommonApiService,
    public toastr: ToastrService,
    private spin: NgxSpinnerService,
    public cs: CommonService,
    private router: Router,
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
    this.Setupstoragesection!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
  }
  ngOnInit(): void {
   this.OrderDetails = ELEMENT_DATA1
  }
onChange() {
  const choosen= this.selectedOrderDetails[this.selectedOrderDetails.length - 1];   
  this.selectedOrderDetails.length = 0;
  this.selectedOrderDetails.push(choosen);
} 






openDialogNew(element){
  const dialogRef = this.dialog.open(MoldingNewComponent, {
    disableClose: true,
    width: '55%',
    maxWidth: '80%',
   data: element
  });
  dialogRef.afterClosed().subscribe(result => {
if(result){
  this.OrderDetails = [];
  const ELEMENT_DATA1 =[
    {orderDetails:  '123',itemCode:  '0057510479',description:  'description',inputQty: '3',confirmedQty: '3',confirmedBy: '5',
    confirmedOn: '05-04-2023',actions: '15-04-2023', status:'Confirmed'}
  ];
  this.OrderDetails = ELEMENT_DATA1;
}
  });
}
}
 
