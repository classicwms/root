import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-masternew',
  templateUrl: './masternew.component.html',
  styleUrls: ['./masternew.component.scss']
})
export class MasternewComponent implements OnInit {

  link = [
    { url: "/main/masternew/partner", title: "Partner",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/masternew/altuom", title: "Alternate UOM",  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/masternew/batch", title: "Batch/Serial",  class: "mx-1 d-flex justify-content-center align-items-center" },
    //{ url: "/main/productsetup/variant", title: "Variant",  class: "mx-1 d-flex justify-content-center align-items-center" },
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

