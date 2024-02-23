import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-barcode-generate',
  templateUrl: './barcode-generate.component.html',
  styleUrls: ['./barcode-generate.component.scss']
})
export class BarcodeGenerateComponent implements OnInit {

  constructor() { }
  rack: any;
  floor: any;
  row: any;
  shelf: any;
  bin: any;
  
  ngOnInit(): void {
  }

}
