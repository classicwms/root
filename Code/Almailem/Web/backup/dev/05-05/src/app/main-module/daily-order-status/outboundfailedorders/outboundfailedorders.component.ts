import { HttpClient } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { NgxSpinnerService } from 'ngx-spinner';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { OutboundfailedordersService } from './outboundfailedorders.service';

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
  selector: 'app-outboundfailedorders',
  templateUrl: './outboundfailedorders.component.html',
  styleUrls: ['./outboundfailedorders.component.scss']
})
export class OutboundfailedordersComponent implements OnInit {

  sub = new Subscription();

  constructor(private http: HttpClient,private service:OutboundfailedordersService ,
    private spin: NgxSpinnerService, private cs : CommonService) { }
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  displayedColumns: string[] = ['integrationLogNumber', 'integrationStatus', 'orderReceiptDate', 'createdBy','createdOn'];
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
      "Integration Log Number":x.integrationLogNumber,
     "Integration Status":x.integrationStatus,
    " Order  Receipt Date ":this.cs.dateapiutc0(x.orderReceiptDate),
 
     "Created By":x.createdBy,
     "Created One":this.cs.dateapi(x.createdOn),
    
    });

  })
  this.cs.exportAsExcel(res, "Outbound Failed Orders");
}

   
}

