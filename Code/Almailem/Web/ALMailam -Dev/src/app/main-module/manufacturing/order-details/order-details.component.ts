import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { BomDialogComponent } from './bom-dialog/bom-dialog.component';
import { OrderDetailsNewComponent } from './order-details-new/order-details-new.component';
import { Router } from '@angular/router';

export interface  variant1 {
  orderDetails:  string;
  itemCode:  string;
  description:  string;
  bom:  string;
  orderQty:  string;
  routingNo:  string;
  startDate:  string;
  endDate:  string;
  status:  string;
} 
const ELEMENT_DATA1:  variant1[] = [
  {orderDetails:  '123',itemCode:  '0057510479',description:  'description',bom: '3',orderQty: '1',routingNo: '5',
  startDate: '05-04-2023',endDate: '15-04-2023', status:'Active'}
];


@Component({
  selector: 'app-order-details',
  templateUrl: './order-details.component.html',
  styleUrls: ['./order-details.component.scss']
})
export class OrderDetailsComponent implements OnInit {

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


openDialog(element){
  const dialogRef = this.dialog.open(BomDialogComponent, {
    disableClose: true,
    width: '55%',
    maxWidth: '80%',
   // data: { pageflow: data, code: data != 'New' ? this.selectedaisle[0].aisleId : null, floorId: data != 'New' ? this.selectedaisle[0].floorId : null, storageSectionId: data != 'New' ? this.selectedaisle[0].storageSectionId : null,}
   data: element
  });

  dialogRef.afterClosed().subscribe(result => {
  //  this.getAll();
  });
}

openNew(){
// let  paramdata = element
  this.router.navigate(['/main/manufacturing/orderNew'])
}


openDialogNew(){
  const dialogRef = this.dialog.open(OrderDetailsNewComponent, {
    disableClose: true,
    width: '55%',
    maxWidth: '80%',
   //data: element
  });
  dialogRef.afterClosed().subscribe(result => {
  //  this.getAll();
  });
}
}
 
