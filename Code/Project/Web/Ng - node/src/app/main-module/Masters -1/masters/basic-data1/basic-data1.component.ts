import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-basic-data1',
  templateUrl: './basic-data1.component.html',
  styleUrls: ['./basic-data1.component.scss']
})
export class BasicData1Component implements OnInit {

 
  constructor() { }

  ngOnInit(): void {
  }
  title1 = "Masters";
  title2 = "Product";
}