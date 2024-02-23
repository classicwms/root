
import { Component, OnInit, ViewChild } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { NgxSpinnerService } from 'ngx-spinner';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { DailyOrderStatusService } from '../daily-order-status.service';
import { SuccessfulOrdersNewComponent } from './successful-orders-new/successful-orders-new.component';
import { MatDialog } from '@angular/material/dialog';


export interface PeriodicElement {
  name: string;
  position: number;
  weight: number;
  symbol: string;
}

const ELEMENT_DATA: PeriodicElement[] = [
  {position: 1, name: 'Hydrogen', weight: 1.0079, symbol: 'H'},
  {position: 2, name: 'Helium', weight: 4.0026, symbol: 'He'},
  {position: 3, name: 'Lithium', weight: 6.941, symbol: 'Li'},
  {position: 4, name: 'Beryllium', weight: 9.0122, symbol: 'Be'},
];



@Component({
  selector: 'app-successful-orders',
  templateUrl: './successful-orders.component.html',
  styleUrls: ['./successful-orders.component.scss']
})
export class SuccessfulOrdersComponent implements OnInit {

  sub = new Subscription();
  toastr: any;

  selection: any;

  constructor(private http: HttpClient,private service:DailyOrderStatusService ,public dialog: MatDialog,
    private spin: NgxSpinnerService, private cs : CommonService) { }
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  displayedColumns: string[] = ['orderId', 'lines','warehouseID', 'orderProcessedOn', 'orderReceivedOn',];
  dataSource = new MatTableDataSource<any>(ELEMENT_DATA);
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
this.getAll();
}
isAllSelected() {
  const numSelected = this.selection.selected.length;
  const numRows = this.dataSource.data.length;
  return numSelected === numRows;
}
masterToggle() {
  if (this.isAllSelected()) {
    this.selection.clear();
    return;
  }

  this.selection.select(...this.dataSource.data);
}
openDialog(element): void {
  const dialogRef = this.dialog.open(SuccessfulOrdersNewComponent, {
    disableClose: true,
    width: '90%',
    maxWidth: '100%',
    data:element,
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
getAll() {
  this.spin.show();
  this.sub.add(this.service.Getall().subscribe(res => {
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
      "Order Processed On Date  ":this.cs.dateapiutc0(x.orderProcessedOn),
      "Order Received Date":this.cs.dateapiutc0(x.orderReceivedOn),
       "Lines":x.lines,
       " Container No ":x.containerNumber,
      
      
    });

  })
   
  this.cs.exportAsExcel(res, "Inbound Sucessful Orders");
}
   
}
