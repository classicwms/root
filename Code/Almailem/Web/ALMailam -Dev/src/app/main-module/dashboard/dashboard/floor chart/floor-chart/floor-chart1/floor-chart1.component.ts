import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { BoxDetails2Component } from '../box-details2/box-details2.component';
import { BoxdetailsComponent } from '../boxdetails/boxdetails.component';
import { TemperatureDetailsComponent } from '../temperature-details/temperature-details.component';

@Component({
  selector: 'app-floor-chart1',
  templateUrl: './floor-chart1.component.html',
  styleUrls: ['./floor-chart1.component.scss']
})
export class FloorChart1Component implements OnInit {

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



}
