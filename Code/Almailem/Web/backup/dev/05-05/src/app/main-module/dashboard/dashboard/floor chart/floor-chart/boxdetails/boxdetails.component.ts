import { Component, OnInit } from '@angular/core';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-boxdetails',
  templateUrl: './boxdetails.component.html',
  styleUrls: ['./boxdetails.component.scss']
})
export class BoxdetailsComponent implements OnInit {

  constructor(    private spinner: NgxSpinnerService,) { }


    ngOnInit(): void {
      this.spinner.show();
      setTimeout(() => {
        this.spinner.hide();
      }, 1000);
    }


}
