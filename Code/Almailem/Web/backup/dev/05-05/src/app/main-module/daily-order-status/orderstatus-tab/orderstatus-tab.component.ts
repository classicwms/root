import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-orderstatus-tab',
  templateUrl: './orderstatus-tab.component.html',
  styleUrls: ['./orderstatus-tab.component.scss']
})
export class OrderstatusTabComponent implements OnInit {

  
  link = [
    { url: "/main/orderStatus/successfulOrder", title: "Inbound Success Orders",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
    { url: "/main/orderStatus/failedOrders", title: "Inbound Failed Orders",  class: "mx-1 hovertab d-flex justify-content-center align-items-center" },
 ];
 activeLink = this.link[0].url;
 
 constructor(private router: Router, private auth: AuthService) { 
   this.router.events.pipe(
     filter((event: any) => event instanceof NavigationEnd),
   ).subscribe(x => {
     this.activeLink = this.router.url;
   });
 }

 ngOnInit(): void {
   this.activeLink = this.router.url;
 }

}
