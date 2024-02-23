import { Component, OnInit } from '@angular/core';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-box-details2',
  templateUrl: './box-details2.component.html',
  styleUrls: ['./box-details2.component.scss']
})
export class BoxDetails2Component implements OnInit {

  constructor(    private spinner: NgxSpinnerService,) { }

  ngOnInit(): void {
    this.spinner.show();
    setTimeout(() => {
      this.spinner.hide();
    }, 1000);
  }

}
