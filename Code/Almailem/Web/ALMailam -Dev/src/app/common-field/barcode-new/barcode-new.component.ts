import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { CommonService } from 'src/app/common-service/common-service.service';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-barcode-new',
  templateUrl: './barcode-new.component.html',
  styleUrls: ['./barcode-new.component.scss']
})
export class BarcodeNewComponent implements OnInit {


  barcodeValueList: any[] = [];
  user = '';
  date: any;
  lable = 'barcode';
  data: any;
  constructor(private auth: AuthService, private c: CommonService, private location:  Location) { }
  imagePath: any;
  ngOnInit(): void {
    this.user = this.auth.userID;
    this.date = this.c.getdate();
    this.data = JSON.parse(sessionStorage.getItem('barcode') as string);

    this.barcodeValueList = this.data.list

    console.log(this.barcodeValueList)
    this.lable = this.data.BCfor == 'case' ? 'case' : 'barcode';
    console.log(this.barcodeValueList);
  }
  ngAfterViewInit(): void {
 //   window.print();
//window.onafterprint = () => { window.close(); sessionStorage.removeItem('barcode'); };

  }
  ngOnDestroy() {
    sessionStorage.removeItem('barcode');
  }


  back(){
    window.close();
  }
}

