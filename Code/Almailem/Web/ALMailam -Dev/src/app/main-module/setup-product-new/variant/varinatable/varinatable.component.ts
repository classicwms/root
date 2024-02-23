import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { Table } from 'primeng/table';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';
import { VariantService } from '../variant.service';

@Component({
  selector: 'app-varinatable',
  templateUrl: './varinatable.component.html',
  styleUrls: ['./varinatable.component.scss']
})
export class VarinatableComponent implements OnInit {

 
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
  public variant: VariantService,
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


levelReferenceVariants: any[]=[];
ngOnInit(): void {
// this.OrderDetails = ;

  this.levelReferenceVariants = this.data.levelReferenceVariants;

}

}





