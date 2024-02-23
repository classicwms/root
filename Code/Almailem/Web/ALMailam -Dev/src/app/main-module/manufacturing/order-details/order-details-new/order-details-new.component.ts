import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { BomDialogComponent } from '../bom-dialog/bom-dialog.component';
import { RoutingComponent } from '../routing/routing.component';

@Component({
  selector: 'app-order-details-new',
  templateUrl: './order-details-new.component.html',
  styleUrls: ['./order-details-new.component.scss']
})
export class OrderDetailsNewComponent implements OnInit {

  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
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


  constructor( private spin: NgxSpinnerService, public cs: CommonService,   public auth: AuthService,
    public toastr: ToastrService,
//   @Inject(MAT_DIALOG_DATA) public data: any, 
    public dialog: MatDialog,
    ) { }
  

  ngOnInit(): void {
  }


  openDialog(element){
    let obj: any = {};
    obj.itemCode = element;
    const dialogRef = this.dialog.open(BomDialogComponent, {
      disableClose: true,
      width: '55%',
      maxWidth: '80%',
     // data: { pageflow: data, code: data != 'New' ? this.selectedaisle[0].aisleId : null, floorId: data != 'New' ? this.selectedaisle[0].floorId : null, storageSectionId: data != 'New' ? this.selectedaisle[0].storageSectionId : null,}
     data: obj
    });
  
    dialogRef.afterClosed().subscribe(result => {
    //  this.getAll();
    });
  }
  
  
  routing(){
    const dialogRef = this.dialog.open(RoutingComponent, {
      disableClose: true,
      width: '65%',
      maxWidth: '80%',
    });
  
    dialogRef.afterClosed().subscribe(result => {
    //  this.getAll();
    });
  }
  


}
