
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-barcode',
  templateUrl: './barcode.component.html',
  styleUrls: ['./barcode.component.scss']
})
export class BarcodeComponent implements OnInit {

  barcodeValueList: any[] = [];
  user = '';
  date: any;
  lable = 'barcode';
  data: any;
  constructor(private auth: AuthService, private c: CommonService) { }
  imagePath: any;
  ngOnInit(): void {
    this.user = this.auth.userID;
    this.date = this.c.getdate();
    this.data = JSON.parse(sessionStorage.getItem('barcode') as string);
    
    this.barcodeValueList = this.data.list;
console.log(this.barcodeValueList)
    this.lable = this.data.BCfor == 'case' ? 'case' : 'barcode';
  }
  ngAfterViewInit(): void {
      window.print();
    window.onafterprint = () => { window.close(); sessionStorage.removeItem('barcode'); };
  }
}
