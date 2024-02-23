import { Component, OnInit, ViewChild, ElementRef, ViewChildren, QueryList } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { BoxDetails2Component } from "./box-details2/box-details2.component";
import { BoxdetailsComponent } from "./boxdetails/boxdetails.component";
import { TemperatureDetailsComponent } from "./temperature-details/temperature-details.component";


@Component({
  selector: 'app-floor-chart',
  templateUrl: './floor-chart.component.html',
  styleUrls: ['./floor-chart.component.scss']
})
export class FloorChartComponent implements OnInit {
  showBinDetails: boolean;
  binCodeFound: boolean;
  itemslist: any[]= []
  constructor(  public dialog: MatDialog,public toastr: ToastrService, private spinner: NgxSpinnerService,) {

   }

  ngOnInit(): void {
    this.showBinDetails =  false;
    this.binCodeFound =  false;
  }

  showBin(){ 
    this.spinner.show();
    setTimeout(() => {
      this.spinner.hide();
    }, 1000);

    this.showBinDetails = !this.showBinDetails;
    this.binCodeFound = false; }

  findBin(){
    this.spinner.show();
    setTimeout(() => {
      this.spinner.hide();
    }, 1000);

    this.binCodeFound = !this.binCodeFound }
  
  boxDetails(): void {
    const dialogRef = this.dialog.open(BoxdetailsComponent, {
      disableClose: true,
      width: '40%',
      position: { right: '5%'},
      maxWidth: '80%',
     // data: { No: binNo,}
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }

  boxDetails1(): void {
    const dialogRef = this.dialog.open(BoxDetails2Component, {
      disableClose: true,
      width: '40%',
      position: { right: '5%'},
      maxWidth: '80%',
     // data: { No: binNo,}
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }


  binBlocked(){  this.toastr.error("Storage bin is blocked for maintenance", "Notification", {
    timeOut: 2000,
    progressBar: false,
  });}




  tempDetails(): void {
    const dialogRef = this.dialog.open(TemperatureDetailsComponent, {
      disableClose: true,
      width: '40%',
      position: { right: '35%'},
      maxWidth: '17%',
     // data: { No: binNo,}
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }



  // @ViewChild('scrollframe', { static: false }) scrollFrame: ElementRef;
  // @ViewChildren('item') itemElements: QueryList<any>;

  // private itemContainer: any;
  // private scrollContainer: any;
  // public items: any[] = [];
  // private isNearBottom = true;



  // ngAfterViewInit() {
  //   this.scrollContainer = this.scrollFrame.nativeElement;
  //   this.itemElements.changes.subscribe((_) => this.onItemElementsChanged());

  //   // Add a new item every 2 seconds for demo purposes
  //   setInterval(() => {
  //     this.items.push(this.itemslist);
  //   }, 2000);
  //   // this.itemslist.forEach((x: { label: string;}) => {
  //   //   for(let i=0; i<this.itemslist.length; i++){
  //   // setInterval(() => {
  //   //     this.items.push({value: x.label}); 
  //   // }, 200);
  //   //   }
  //   // })
  
  // }

  
  // private onItemElementsChanged(): void {
  //   if (this.isNearBottom) {
  //     this.scrollToBottom();
  //   }
  // }

  // private scrollToBottom(): void {
  //   this.scrollContainer.scroll({
  //     top: this.scrollContainer.scrollHeight,
  //     left: 0,
  //     behavior: 'smooth',
  //   });
  // }

  // private isUserNearBottom(): boolean {
  //   const threshold = 150;
  //   const position =
  //     this.scrollContainer.scrollTop + this.scrollContainer.offsetHeight;
  //   const height = this.scrollContainer.scrollHeight;
  //   return position > height - threshold;
  // }

  // scrolled(event: any): void {
  //   this.isNearBottom = this.isUserNearBottom();
  // }
}
