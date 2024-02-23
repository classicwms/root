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
    { url: "/main/masternew/basicdata", title: "Basic Data 1", screenid: 3020, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/masternew/basicdata2", title: "Basic Data 2",  screenid: 3021, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/masternew/altuom", title: "Alternate UOM",  screenid: 3022, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/masternew/partner", title: "Partner",  screenid: 3025, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/masternew/impacking", title: " Packing", screenid: 3026,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/masternew/imcapacity", title: " Capacity",  screenid: 3148, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/masternew/batchserial", title: " Batch/Serial", screenid: 3023,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/masternew/imvariant", title: " Variant", screenid: 3028, class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/masternew/palletization", title: " Palletization",screenid: 3027,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/masternew/altpart", title: "Alternate Parts",screenid: 3147,  class: "mx-1 d-flex justify-content-center align-items-center" },
    { url: "/main/masternew/startegy", title: " Strategy",  screenid: 3024, class: "mx-1 d-flex justify-content-center align-items-center" },
  
   
   
    
   
   
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


 isdiabled(id: any) {
  this.auth.isMenudata();
  let fileterdata = this.auth.MenuData.filter((x: any) => x.subMenuId == id && (x.view || x.delete || x.createUpdate));

  if (fileterdata.length > 0) {
    return false;
  }

  return true;
}

}

