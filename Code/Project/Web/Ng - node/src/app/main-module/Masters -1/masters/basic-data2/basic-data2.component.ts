import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-basic-data2',
  templateUrl: './basic-data2.component.html',
  styleUrls: ['./basic-data2.component.scss']
})
export class BasicData2Component implements OnInit {

   
  constructor() { }

  ngOnInit(): void {
  }
  title1 = "Masters";
  title2 = "Product";
}
