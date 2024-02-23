import { HttpClient } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { NgxSpinnerService } from 'ngx-spinner';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { OutboundsucessordersService } from './outboundsucessorders.service';
import { OutboundsucessordersNewComponent } from './outboundsucessorders-new/outboundsucessorders-new.component';

export interface PeriodicElement {
  name: string;
  position: number;
  weight: number;
  symbol: string;
}
// const ELEMENT_DATA: PeriodicElement[] = [
//   {position: 1, name: 'Hydrogen', weight: 1.0079, symbol: 'H'},
//   {position: 2, name: 'Helium', weight: 4.0026, symbol: 'He'},
//   {position: 3, name: 'Lithium', weight: 6.941, symbol: 'Li'},
//   {position: 4, name: 'Beryllium', weight: 9.0122, symbol: 'Be'},
// ];
@Component({
  selector: 'app-outboundsucessorders',
  templateUrl: './outboundsucessorders.component.html',
  styleUrls: ['./outboundsucessorders.component.scss']
})
export class OutboundsucessordersComponent implements OnInit {
  orderEndDate:any;
  orderStartDate:any;
  orderEndDateFE:any;
  orderStartDateFE:any;

  sub = new Subscription();
  toastr: any;

  selection: any;
  ELEMENT_DATA: any [] = []
  constructor(private http: HttpClient,private service: OutboundsucessordersService,public dialog: MatDialog,
    private spin: NgxSpinnerService, private cs : CommonService) { }
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  displayedColumns: string[] = ['orderId', 'lines','warehouseID','outboundOrderTypeID', 'orderReceivedOn', 'orderProcessedOn',];
  dataSource = new MatTableDataSource<any>(this.ELEMENT_DATA);
  @ViewChild(MatSort, {
    static: true
  })
  sort!: MatSort;
  @ViewChild(MatPaginator, {
    static: true
  })
  paginator!: MatPaginator; // Pagination
  // Pagination
  toggleFloat() {
    this.isShowDiv = !this.isShowDiv;  
    this.toggle = !this.toggle;

    if (this.icon === 'expand_more') {
      this.icon = 'chevron_left';
    } else {
      this.icon = 'expand_more'
    }
    this.showFloatingButtons = !this.showFloatingButtons;
     console.log('show:' + this.showFloatingButtons);
  }
  ngOnInit(): void {
}
openDialog(element): void {
  const dialogRef = this.dialog.open(OutboundsucessordersNewComponent, {
    disableClose: true,
    width: '80%',
    maxWidth: '80%',
   data: element,
  });

  dialogRef.afterClosed().subscribe(result => {
    this.getAll();
  });
}
  selectedapprovals(selectedapprovals: any) {
    throw new Error('Method not implemented.');
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
  filtersearch() {
    // if (this.form.invalid) {
    //   this.toastr.error(
    //     "Please fill required fields to continue",
    //     "Notification", {
    //     timeOut: 2000,
    //     progressBar: false,
    //   }
      //);
      return;
    }

  getAll() {
    this.spin.show();
    this.orderStartDate = this.cs.dateddMMYY(this.orderStartDateFE);
    this.orderEndDate = this.cs.dateddMMYY(this.orderEndDateFE);
    this.sub.add(this.service.Get(this.orderStartDate,this.orderEndDate).subscribe(res => {
      console.log(res)
  
     this.dataSource = new MatTableDataSource<any>(res);
     this.dataSource.sort = this.sort;
     this.dataSource.paginator = this.paginator;
  
      this.spin.hide();
    }, err => {
      this.cs.commonerrorNew(err);
      this.spin.hide();
    }));
  }
 
downloadexcel() {
  var res: any = [];
  this.dataSource.data.forEach(x => {
    res.push({
      " Order No":x.orderId,
      "Warehouse ID": x.warehouseID,
      "Order Type ":x.outboundOrderTypeID,
      "Order Processed On Date  ":this.cs.dateapiutc0(x.orderProcessedOn),
      "Order Received Date":this.cs.dateapiutc0(x.orderReceivedOn),
       "Lines":x.lines,
       " Container No ":x.containerNumber,
     
      
      
    });

  })


  this.cs.exportAsExcel(res, "Outbound Sucessful Orders");
}
   
}

