import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { Router } from '@angular/router';
import { Subscription, timer } from 'rxjs';
import { map, share } from 'rxjs/operators';
import { MenuService } from 'src/app/common-service/menu.service';
import { AuthService } from 'src/app/core/core';
import { environment } from 'src/environments/environment';
import { trigger, state, style, animate, transition } from '@angular/animations';
import { MatDialog } from '@angular/material/dialog';
import { BarcodePrintComponent } from './barcode-print/barcode-print.component';
import { PreoutboundService } from '../Outbound/preoutbound/preoutbound.service';
import { UpdateInventoryComponent } from './update-inventory/update-inventory.component';
import { InhouseTransferService } from '../make&change/inhouse-transfer/inhouse-transfer.service';
import { UpdateOutboundLineComponent } from './update-outbound-line/update-outbound-line.component';
import { CronJob } from 'cron';
import { ShipmentSummaryComponent } from '../reports/shipment-summary/shipment-summary.component';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  animations: [
    // Each unique animation requires its own trigger. The first argument of the trigger function is the name
    trigger('rotatedState', [
      state('default', style({ transform: 'rotate(0)' })),
      state('rotated', style({ transform: 'rotate(-180deg)' })),
      transition('rotated => default', animate('400ms ease-out')),
      transition('default => rotated', animate('400ms ease-in'))
    ])
  ]
})
export class HomeComponent implements OnInit {


  public icon = 'menu';
  showFloatingButtons: any;
  toggle = true;
  state: string = 'default';
  toggleFloat() {
    this.toggle = !this.toggle;
    this.state = (this.state === 'default' ? 'rotated' : 'default');
    if (this.icon === 'menu') {
      this.icon = 'arrow_back_ios_new';
    } else {
      this.icon = 'menu'
    }
  }



  username = this.auth.userID;
  usertype = this.auth.userTypeId;
  currentdate = new Date();
  // default list of  menu items
  menulist: any;

  sub = new Subscription();
  cronJob: CronJob;
  constructor(private router: Router, public dialog: MatDialog, private ms: MenuService, private auth: AuthService, private service: PreoutboundService,
    private uploadService: ShipmentSummaryComponent) { 
    this.currentEnv = environment.name; 

      //     var cron = require('cron');
      // var cronJob = cron.job("35 15 * * *", 
      // this.idmastser.Getall().subscribe(res => {
      //   console.log('Job Completed')
      // })
      // this.cronJob = new CronJob('*/5 * * * *', async () => {
      //   try {
      //     await this.bar();
      //   } catch (e) {
      //     console.error(e);
      //   }
      // });
      // if (!this.cronJob.running) {
      //   this.cronJob.start();
      // }

  }
  // async bar(): Promise<void> {
  //   console.log('job completed')
  //   this.uploadService.scheduleSearch()
  // }
  showFiller = false;

  currentEnv: string;
  prod: boolean;
  
  ngOnInit(): void {
    console.log(this.auth.userTypeId)
    // sidenav.toggle();
    // Using RxJS Timer
    var username = this.auth.username

    //this.username = this.auth.username.toUpperCase();
    let menu =
      JSON.parse("[" + sessionStorage.getItem('menu') + "]");
    this.menulist = this.ms.getMeuList('save').filter(x => menu.includes(x.id));
    console.log(menu);
    this.sub = timer(0, 10000)
      .pipe(
        map(() => new Date()),
        share()
      )
      .subscribe(time => {
        this.currentdate = time;
      });

      if(this.currentEnv == 'prod'){
        this.prod = true;
      }else{
        this.prod = false;
      }

  }
  logout() {
    this.auth.logout();
    setTimeout(() => {
      window.location.reload();
  }, 200);
  }

  ngOnDestroy() {
    if (this.sub) {
      this.sub.unsubscribe();
    }
  }
  routeto(url: any, id: any) {
    sessionStorage.setItem('crrentmenu', id);
    console.log(url);
    if (!url) {
      let menu =
        JSON.parse("[" + sessionStorage.getItem('menu') + "]");
      url = this.ms.getMeuList('save').filter(x => x.id == Number(id))[0].children?.filter(x => menu.includes(x.id))[0].url;
    }
    this.router.navigate([url]);
  }
  OpenBarcodePrint() {
    const dialogRef = this.dialog.open(BarcodePrintComponent, {
      disableClose: true,
      width: '50%',
      maxWidth: '80%',
      position: { top: '9%', },
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }
  ///side pannel

  @ViewChild('sidenav') sidenav: MatSidenav | undefined;
  isExpanded = false;
  showSubmenu: boolean = false;
  isShowing = false;
  showSubSubMenu: boolean = false;



  updateInventory() {
    const dialogRef = this.dialog.open(UpdateInventoryComponent, {
      disableClose: true,
      width: '50%',
      maxWidth: '80%',
      position: { top: '6.5%' },
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }

  updateOutbound(){
    const dialogRef = this.dialog.open(UpdateOutboundLineComponent, {
      disableClose: true,
      width: '50%',
      maxWidth: '80%',
      position: { top: '6.5%' },
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }

  callSoCreate() {
    let obj = JSON.parse(`
    [{"storeReturnHeader":{"transferOrderNumber":"TO-064207","wareHouseId":"110"},"storeReturnLine":[{"containerNumber":"","expectedDate":"09-20-2022","expectedQty":1.0,"invoiceNmber":"","lineReference":1.0,"manufacturerName":"","manufacturerPartNo":"","packQty":0.0,"sku":"026811798","skuDescription":"LED VANITY MIRROR BLACK MODO","storeID":"109","supplierPartNumber":"","uom":"PIECE"},{"containerNumber":"","expectedDate":"09-20-2022","expectedQty":1.0,"invoiceNmber":"","lineReference":2.0,"manufacturerName":"BLOMUS GMBH","manufacturerPartNo":"66350","packQty":0.0,"sku":"026811799","skuDescription":"LED VANITY MIRROR WHITE MODO","storeID":"109","supplierPartNumber":"","uom":"PIECE"}]}
    ,{"storeReturnHeader":{"transferOrderNumber":"TO-064242","wareHouseId":"110"},"storeReturnLine":[{"containerNumber":"","expectedDate":"09-21-2022","expectedQty":4.0,"invoiceNmber":"","lineReference":3.0,"manufacturerName":"BLOMUS GMBH","manufacturerPartNo":"66351","packQty":0.0,"sku":"026811799","skuDescription":"LED VANITY MIRROR WHITE MODO","storeID":"109","supplierPartNumber":"","uom":"PIECE"}]}
    ,{"soHeader":{"requiredDeliveryDate":"09-14-2022","storeID":"101","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-063875","wareHouseId":"110"},"soLine":[{"lineReference":1,"orderType":"N","orderedQty":1.0,"sku":"0203011369","skuDescription":"RATTAN DINING TABLE 300X100X75cm","uom":"PIECE"},{"lineReference":2,"orderType":"N","orderedQty":1.0,"sku":"0203011816","skuDescription":"ALU UMBRELLA 3*3m RED","uom":"PIECE"},{"lineReference":3,"orderType":"N","orderedQty":1.0,"sku":"0203012105","skuDescription":"4pcs TABLE AND CHAIR","uom":"SET"},{"lineReference":4,"orderType":"N","orderedQty":2.0,"sku":"0203012671","skuDescription":"PAVILION 3x3 W/CLOTH CURTAIN GREEN","uom":"PIECE"},{"lineReference":5,"orderType":"N","orderedQty":5.0,"sku":"0203013582","skuDescription":"SWING CHAIR","uom":"PIECE"},{"lineReference":6,"orderType":"N","orderedQty":1.0,"sku":"023321005","skuDescription":"GLASS TUBE","uom":"PIECE"},{"lineReference":7,"orderType":"N","orderedQty":1.0,"sku":"026231004","skuDescription":"GARDEN BENCH DARK GRN-3 SEATER","uom":"PIECE"},{"lineReference":8,"orderType":"N","orderedQty":2.0,"sku":"026811351","skuDescription":"ANI LAMP 3 IN 1 RECHARGEABLE LED, WHITE,13 x 8.7 CM","uom":"PIECE"},{"lineReference":9,"orderType":"N","orderedQty":1.0,"sku":"026811605","skuDescription":"MOBILE LED FLOOR LAMP, SATELLITE - FAROL","uom":"PIECE"},{"lineReference":10,"orderType":"N","orderedQty":1.0,"sku":"026841041","skuDescription":"SAMO BAMBOO FLOOR LAMP d42.5xh96cm","uom":"PIECE"},{"lineReference":11,"orderType":"N","orderedQty":1.0,"sku":"026841055","skuDescription":"FLOOR LAMP ROMANA d36xh48cm","uom":"PIECE"},{"lineReference":12,"orderType":"N","orderedQty":2.0,"sku":"026851310","skuDescription":"TUNIS LANTERN BAMBOO/GLASS, 35 x 60 cm, AQUA COLOUR","uom":"PIECE"},{"lineReference":13,"orderType":"N","orderedQty":1.0,"sku":"027251033","skuDescription":"OAK PIECE FLAT NO. 32 - NATURE H7x29cm","uom":"PIECE"},{"lineReference":14,"orderType":"N","orderedQty":1.0,"sku":"027651157","skuDescription":"BLVM ROSIE IRON&WOOD SIDE TABLE 67× 50×75cm","uom":"PIECE"},{"lineReference":15,"orderType":"N","orderedQty":2.0,"sku":"028211060","skuDescription":"MS TUBE CRUZER PTR","uom":"PIECE"},{"lineReference":16,"orderType":"N","orderedQty":2.0,"sku":"028501081","skuDescription":"BOAT BOWL XLARGE 89 x 20 x 7.6 CM WHITE","uom":"PIECE"},{"lineReference":17,"orderType":"N","orderedQty":1.0,"sku":"112733","skuDescription":"DECK BOX W/STORAGE COMPARTMENT 103GAL","uom":"PIECE"},{"lineReference":18,"orderType":"N","orderedQty":5.0,"sku":"121285","skuDescription":"UPTOWN BISTRO CHAIR","uom":"PIECE"},{"lineReference":19,"orderType":"N","orderedQty":1.0,"sku":"203663","skuDescription":"FIBERGLASS STEP LADDER 244CM/102KG -DEWALT","uom":"PIECE"},{"lineReference":20,"orderType":"N","orderedQty":1.0,"sku":"21305128988","skuDescription":"GARDEN GLIDER BRONZE","uom":"PIECE"},{"lineReference":21,"orderType":"N","orderedQty":1.0,"sku":"21307000077","skuDescription":"PORTABLE GRILL 2 GOX200","uom":"PIECE"},{"lineReference":22,"orderType":"N","orderedQty":1.0,"sku":"21309997215","skuDescription":"CHARCOAL OFFSET SMOKER GRILL  AMERICAN GOURMET 430SERIES","uom":"PIECE"},{"lineReference":23,"orderType":"N","orderedQty":1.0,"sku":"233833","skuDescription":"4FT ALU II STEPLADDER","uom":"PIECE"},{"lineReference":24,"orderType":"N","orderedQty":1.0,"sku":"246321","skuDescription":"FS CHARLEST SLAT TABLE","uom":"PIECE"}]}
    ,{"soHeader":{"requiredDeliveryDate":"09-26-2022","storeID":"103","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-064490","wareHouseId":"110"},"soLine":[{"lineReference":25,"orderType":"N","orderedQty":12.0,"sku":"009451310","skuDescription":"COIR MAT 100cm/21mm NATURAL COCO","uom":"METRE"},{"lineReference":26,"orderType":"N","orderedQty":50.0,"sku":"009451427","skuDescription":"GRIP LINER 30X150cm WHT MEGA-STOP","uom":"PIECE"},{"lineReference":27,"orderType":"N","orderedQty":50.0,"sku":"009452769","skuDescription":"COOL & FRESH ANTI SLIP LINER 50X150cm WHT","uom":"PIECE"},{"lineReference":28,"orderType":"N","orderedQty":50.0,"sku":"009453998","skuDescription":"COOL & FRESH ANTI SLIP LINER 50X150cm SLVRSTRIPES","uom":"PIECE"},{"lineReference":29,"orderType":"N","orderedQty":6.0,"sku":"009454045","skuDescription":"COIR MAT 70X100cm ARABIC WELCOME","uom":"METRE"},{"lineReference":30,"orderType":"N","orderedQty":15.0,"sku":"009454048","skuDescription":"FLOOR COVERING ROLL 130cm BLK","uom":"METRE"},{"lineReference":31,"orderType":"N","orderedQty":15.0,"sku":"009454049","skuDescription":"FLOOR COVERING ROLL 65cm YELLOW","uom":"METRE"},{"lineReference":32,"orderType":"N","orderedQty":4.0,"sku":"009454087","skuDescription":"3pc BATHMAT H20mm WHITE/LIGHT BROWN","uom":"PIECE"},{"lineReference":33,"orderType":"N","orderedQty":4.0,"sku":"009454088","skuDescription":"3pc BATHMAT H20mm WHITE/GREY","uom":"PIECE"},{"lineReference":34,"orderType":"N","orderedQty":4.0,"sku":"009454098","skuDescription":"3pc BATHMAT H20mm BEIGE/GREY","uom":"PIECE"},{"lineReference":35,"orderType":"N","orderedQty":2.0,"sku":"009454096","skuDescription":"3pc BATHMAT H20mm WHITE/BLACK","uom":"PIECE"},{"lineReference":36,"orderType":"N","orderedQty":4.0,"sku":"009454500","skuDescription":"3pc BATHMAT H20mm SOLID WHITE","uom":"PIECE"},{"lineReference":37,"orderType":"N","orderedQty":2.0,"sku":"009454503","skuDescription":"BATHMAT 70x140cm WHITE/LIGHT BROWN","uom":"PIECE"},{"lineReference":38,"orderType":"N","orderedQty":4.0,"sku":"009454508","skuDescription":"BATHMAT 70x140cm BEIGE/GREY","uom":"PIECE"},{"lineReference":39,"orderType":"N","orderedQty":4.0,"sku":"009454510","skuDescription":"BATHMAT 70x140cm BEIGE/PINK","uom":"PIECE"},{"lineReference":40,"orderType":"N","orderedQty":4.0,"sku":"009454511","skuDescription":"BATHMAT 70x140cm WHITE/LIGHT BROWN","uom":"PIECE"},{"lineReference":41,"orderType":"N","orderedQty":2.0,"sku":"009454514","skuDescription":"BATHMAT 70x140cm BEIGE/LIGHT BROWN","uom":"PIECE"},{"lineReference":42,"orderType":"N","orderedQty":2.0,"sku":"009454515","skuDescription":"BATHMAT 70x140cm BEIGE/GREY","uom":"PIECE"},{"lineReference":43,"orderType":"N","orderedQty":4.0,"sku":"009454516","skuDescription":"BATHMAT 70x140cm BEIGE/BLACK","uom":"PIECE"},{"lineReference":44,"orderType":"N","orderedQty":4.0,"sku":"009454517","skuDescription":"BATHMAT 70x140cm SOLID/BEIGE","uom":"PIECE"},{"lineReference":45,"orderType":"N","orderedQty":15.0,"sku":"009454520","skuDescription":"FLOOR COVERING ROLL 65cm TERRAZZO BRANDY VINTAGE","uom":"METRE"},{"lineReference":46,"orderType":"N","orderedQty":15.0,"sku":"009454523","skuDescription":"FLOOR COVERING ROLL130CM TERRAZZO BRANDY VINTAGE","uom":"METRE"},{"lineReference":47,"orderType":"N","orderedQty":50.0,"sku":"009454562","skuDescription":"COOL & FRESH ANTI SLIP LINER 50X150cm LIGHT GREY","uom":"PIECE"}]}
    ,{"soHeader":{"requiredDeliveryDate":"09-26-2022","storeID":"103","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-064412","wareHouseId":"110"},"soLine":[{"lineReference":1,"orderType":"N","orderedQty":2.0,"sku":"0203012166","skuDescription":"BARBECUE GRILL -BLACK","uom":"PIECE"},{"lineReference":2,"orderType":"N","orderedQty":1.0,"sku":"9028324","skuDescription":"UMBRELLA SHANGHAI 325CM","uom":"PIECE"},{"lineReference":3,"orderType":"N","orderedQty":14.0,"sku":"728212","skuDescription":"24\\\" BAR CLAMP SPREADER","uom":"PIECE"},{"lineReference":4,"orderType":"N","orderedQty":14.0,"sku":"728204","skuDescription":"12\\\" BAR CLAMP SPREADER","uom":"PIECE"}]}
    ,{"soHeader":{"requiredDeliveryDate":"09-26-2022","storeID":"103","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-064497","wareHouseId":"110"},"soLine":[{"lineReference":1,"orderType":"N","orderedQty":12.0,"sku":"024911005","skuDescription":"GARMENT RACK SINGLE EXTENDABLE","uom":"PIECE"},{"lineReference":2,"orderType":"N","orderedQty":14.0,"sku":"024911014","skuDescription":"EXT.SINGLE GARMENT RACK","uom":"PIECE"},{"lineReference":3,"orderType":"N","orderedQty":12.0,"sku":"024911019","skuDescription":"SINGLE GARMENT RACK 84x43x168cm","uom":"PIECE"},{"lineReference":4,"orderType":"N","orderedQty":75.0,"sku":"020306450","skuDescription":"MOP HEAD","uom":"PIECE"},{"lineReference":5,"orderType":"N","orderedQty":72.0,"sku":"020306751","skuDescription":"JUMBO WASHING SPONGE 3PCS","uom":"PIECE"},{"lineReference":6,"orderType":"N","orderedQty":6.0,"sku":"024911020","skuDescription":"4 TIER WHT TROLLY W/MESH DRAWERS 30.5x35.5x81cm","uom":"PIECE"},{"lineReference":7,"orderType":"N","orderedQty":6.0,"sku":"024911021","skuDescription":"5 TIER WHT TROLLY W/MESH DRAWERS 30.5x35.5x108.5cm","uom":"PIECE"},{"lineReference":8,"orderType":"N","orderedQty":6.0,"sku":"0203011302","skuDescription":"MOP BUCKET SQUARE","uom":"PIECE"}]}
    ,{"soHeader":{"requiredDeliveryDate":"09-26-2022","storeID":"103","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-064500","wareHouseId":"110"},"soLine":[{"lineReference":9,"orderType":"N","orderedQty":6.0,"sku":"0203012205","skuDescription":"CUP HOLDER 17x21.5x23.5cm","uom":"PIECE"},{"lineReference":10,"orderType":"N","orderedQty":30.0,"sku":"0057517055","skuDescription":"LEMON KEEPER","uom":"PIECE"},{"lineReference":11,"orderType":"N","orderedQty":24.0,"sku":"0057517056","skuDescription":"ONION KEEPER","uom":"PIECE"},{"lineReference":12,"orderType":"N","orderedQty":6.0,"sku":"0203013838","skuDescription":"12pcs SILICONE KITCHENWARE GREY/BLACK","uom":"PIECE"},{"lineReference":13,"orderType":"N","orderedQty":24.0,"sku":"0203010508","skuDescription":"KITCHEN CLOTH","uom":"PIECE"},{"lineReference":14,"orderType":"N","orderedQty":6.0,"sku":"0203010469","skuDescription":"GLASS TEA POT 1300ML BLK","uom":"PIECE"},{"lineReference":15,"orderType":"N","orderedQty":6.0,"sku":"0203013171","skuDescription":"16PC JAR SPICE RACK SET","uom":"PIECE"},{"lineReference":16,"orderType":"N","orderedQty":6.0,"sku":"9029734","skuDescription":"SALT AND PEPPER SHAKER GLASS","uom":"SET"},{"lineReference":17,"orderType":"N","orderedQty":4.0,"sku":"21303297271","skuDescription":"CL EDES 18OZ SUNLIT MAND BRY","uom":"PIECE"},{"lineReference":18,"orderType":"N","orderedQty":6.0,"sku":"21301879354","skuDescription":"CL EDES 10OZ CHASNGBUTTERFLIES","uom":"PIECE"},{"lineReference":19,"orderType":"N","orderedQty":6.0,"sku":"21304449099","skuDescription":"CL EDES 3OZ TROP FRUIT MEDLEY","uom":"PIECE"},{"lineReference":20,"orderType":"N","orderedQty":6.0,"sku":"21304449024","skuDescription":"CL EDES 3OZ SWEET PEAR LILY","uom":"PIECE"},{"lineReference":21,"orderType":"N","orderedQty":6.0,"sku":"21303297330","skuDescription":"CL EDES 18OZ SALTWATER LOTUS","uom":"PIECE"},{"lineReference":22,"orderType":"N","orderedQty":6.0,"sku":"21301879146","skuDescription":"CL EDES 10OZ JUCY WATERMLN SLC","uom":"PIECE"},{"lineReference":23,"orderType":"N","orderedQty":6.0,"sku":"21301879066","skuDescription":"CL EDES 10OZ MOONLT STARYNIGHT","uom":"PIECE"},{"lineReference":24,"orderType":"N","orderedQty":6.0,"sku":"21304449066","skuDescription":"CL EDES 3OZ MOONLITSTARRYNIGHT","uom":"PIECE"}]}

    ,{"soHeader":{"requiredDeliveryDate":"09-26-2022","storeID":"103","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-064513","wareHouseId":"110"},"soLine":[{"lineReference":1,"orderType":"S","orderedQty":1.0,"sku":"020307584","skuDescription":"SWING CHAIR 3 SEAT","uom":"PIECE"}]}

    ,{"soHeader":{"requiredDeliveryDate":"09-26-2022","storeID":"103","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-064515","wareHouseId":"110"},"soLine":[{"lineReference":1,"orderType":"S","orderedQty":16.0,"sku":"020302751","skuDescription":"UMBRELLA SQUARE 2.5mtr W/BASE BEIGE","uom":"PIECE"}]}
    ,{"soHeader":{"requiredDeliveryDate":"09-26-2022","storeID":"103","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-064516","wareHouseId":"110"},"soLine":[{"lineReference":1,"orderType":"N","orderedQty":15.0,"sku":"009451046","skuDescription":"FLOOR COVERING ROLL 65cm LITTLE GRAPHIC CAPPUCCINO","uom":"METRE"},{"lineReference":2,"orderType":"N","orderedQty":15.0,"sku":"009453710","skuDescription":"FLOOR COVERING ROLL 65cm METAL STRIPES","uom":"METRE"},{"lineReference":3,"orderType":"N","orderedQty":15.0,"sku":"009454051","skuDescription":"FLOOR COVERING ROLL 65cm KELIM BROWN","uom":"METRE"},{"lineReference":4,"orderType":"N","orderedQty":15.0,"sku":"009452362","skuDescription":"FLOOR COVERING ROLL 65cm CRYSTAL BLUE","uom":"METRE"},{"lineReference":5,"orderType":"N","orderedQty":10.0,"sku":"020309253","skuDescription":"FLOOR MAT 45X76cm","uom":"PIECE"},{"lineReference":6,"orderType":"N","orderedQty":50.0,"sku":"0203010906","skuDescription":"TABLE COVER 1.37 X 0.3MM","uom":"METRE"},{"lineReference":7,"orderType":"N","orderedQty":50.0,"sku":"0203013778","skuDescription":"10pcs HANGER 40cm PINK","uom":"PIECE"},{"lineReference":8,"orderType":"N","orderedQty":30.0,"sku":"0203013456","skuDescription":"10pc METAL NONSLIP HANGER -BEIGE","uom":"PIECE"},{"lineReference":9,"orderType":"N","orderedQty":30.0,"sku":"020307366","skuDescription":"WELCOME MAT 60X90cm GRN","uom":"PIECE"},{"lineReference":10,"orderType":"N","orderedQty":50.0,"sku":"0203013777","skuDescription":"10pcs HANGER 40cm L-BROWN","uom":"PIECE"},{"lineReference":11,"orderType":"N","orderedQty":50.0,"sku":"0203013455","skuDescription":"10pc METAL NONSLIP HANGER -GRAY","uom":"PIECE"},{"lineReference":12,"orderType":"N","orderedQty":50.0,"sku":"0203013454","skuDescription":"10pc METAL NONSLIP HANGER -PINK","uom":"PIECE"},{"lineReference":13,"orderType":"N","orderedQty":20.0,"sku":"020307364","skuDescription":"WELCOME MAT 60X90cm COFFEE","uom":"PIECE"},{"lineReference":14,"orderType":"N","orderedQty":7.0,"sku":"020302492","skuDescription":"WELCOME MAT 60x90CM BRWN","uom":"PIECE"}]}

    ,{"soHeader":{"requiredDeliveryDate":"09-26-2022","storeID":"103","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-064511","wareHouseId":"110"},"soLine":[{"lineReference":1,"orderType":"N","orderedQty":6.0,"sku":"024911020","skuDescription":"4 TIER WHT TROLLY W/MESH DRAWERS 30.5x35.5x81cm","uom":"PIECE"},{"lineReference":2,"orderType":"N","orderedQty":3.0,"sku":"024911021","skuDescription":"5 TIER WHT TROLLY W/MESH DRAWERS 30.5x35.5x108.5cm","uom":"PIECE"},{"lineReference":3,"orderType":"N","orderedQty":3.0,"sku":"024911024","skuDescription":"8 TIER MULTI CLR STORAGE TROLLEY 41.5x39x95cm","uom":"PIECE"},{"lineReference":4,"orderType":"N","orderedQty":3.0,"sku":"024911022","skuDescription":"15 TIER MULTI CLR STORAGE TROLLEY 63x37x87cm","uom":"PIECE"},{"lineReference":5,"orderType":"N","orderedQty":12.0,"sku":"0203011615","skuDescription":"CLEANING BRUSH WHITE&BEIGE","uom":"PIECE"},{"lineReference":6,"orderType":"N","orderedQty":12.0,"sku":"0203011617","skuDescription":"CLEANING BRUSH WHITE&BEIGE","uom":"PIECE"},{"lineReference":7,"orderType":"N","orderedQty":4.0,"sku":"9021476","skuDescription":"PEDEL BIN EASY CLOSE S.STEEL - STUDIO 61","uom":"PIECE"},{"lineReference":8,"orderType":"N","orderedQty":6.0,"sku":"0203011623","skuDescription":"DUSTPAN WITH BRUSH WHITE&BEIGE MIX COLOR","uom":"PIECE"},{"lineReference":9,"orderType":"N","orderedQty":6.0,"sku":"0203011625","skuDescription":"DUSTER 69cm WHITE&BEIGE","uom":"PIECE"},{"lineReference":10,"orderType":"N","orderedQty":3.0,"sku":"0203012218","skuDescription":"3 TIER MULTI FUNCTION MOBILE CART,GRN","uom":"PIECE"},{"lineReference":11,"orderType":"N","orderedQty":3.0,"sku":"9028507","skuDescription":"TOILET BRUSH BAMBUSA","uom":"PIECE"},{"lineReference":12,"orderType":"N","orderedQty":4.0,"sku":"9028513","skuDescription":"PEDAL BIN MOD. LEMAN, WHITE 5LTR","uom":"PIECE"},{"lineReference":13,"orderType":"N","orderedQty":2.0,"sku":"0203013776","skuDescription":"DUSTBIN 19x25x27cm","uom":"PIECE"},{"lineReference":14,"orderType":"N","orderedQty":3.0,"sku":"0203010523","skuDescription":"DUST BIN OVAL BRWN","uom":"PIECE"},{"lineReference":15,"orderType":"N","orderedQty":6.0,"sku":"0203010521","skuDescription":"DUST BIN OVAL BLK","uom":"PIECE"},{"lineReference":16,"orderType":"N","orderedQty":6.0,"sku":"0203010524","skuDescription":"DUST BIN rnd 24x27 blk","uom":"PIECE"},{"lineReference":17,"orderType":"N","orderedQty":6.0,"sku":"0203010526","skuDescription":"DUST BIN RND 24X27 BRWN","uom":"PIECE"},{"lineReference":18,"orderType":"N","orderedQty":3.0,"sku":"0203012670","skuDescription":"METAL DUSTBIN OVAL  RATTAN DESIGN 19x25x27cm","uom":"PIECE"},{"lineReference":19,"orderType":"N","orderedQty":12.0,"sku":"020301159922","skuDescription":"TRASH CAN 28x30cm","uom":"PIECE"},{"lineReference":20,"orderType":"N","orderedQty":6.0,"sku":"0203010161","skuDescription":"DUSTBIN BEIGE","uom":"PIECE"},{"lineReference":21,"orderType":"N","orderedQty":6.0,"sku":"0203011640","skuDescription":"DUSTPAN WITH BROOM WHITE&BEIGE","uom":"PIECE"},{"lineReference":22,"orderType":"N","orderedQty":6.0,"sku":"020301206","skuDescription":"DUST PAN W/LONG HANDLE BRUSH","uom":"PIECE"},{"lineReference":23,"orderType":"N","orderedQty":3.0,"sku":"0203010043","skuDescription":"FLAT MOP BIG","uom":"PIECE"},{"lineReference":24,"orderType":"N","orderedQty":6.0,"sku":"020306960","skuDescription":"BRUSH","uom":"PIECE"},{"lineReference":25,"orderType":"N","orderedQty":6.0,"sku":"020301697","skuDescription":"SHOES BRUSH","uom":"PIECE"},{"lineReference":26,"orderType":"N","orderedQty":12.0,"sku":"0203011629","skuDescription":"BROOM W/STICK WHITE&BEIGE","uom":"PIECE"},{"lineReference":27,"orderType":"N","orderedQty":6.0,"sku":"0203011608","skuDescription":"GLASS WIPER W/STICK WHITE&BEIGE","uom":"PIECE"},{"lineReference":28,"orderType":"N","orderedQty":6.0,"sku":"0203011607","skuDescription":"GLASS WIPER W/STICK WHITE&BEIGE","uom":"PIECE"},{"lineReference":29,"orderType":"N","orderedQty":4.0,"sku":"9019312","skuDescription":"BATH BASKET MINI SQUR WHT -ADRIA","uom":"PIECE"}]}

    ,{"soHeader":{"requiredDeliveryDate":"09-26-2022","storeID":"107","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-064530","wareHouseId":"110"},"soLine":[{"lineReference":1,"orderType":"S","orderedQty":2.0,"sku":"020309661","skuDescription":"DRAWER CABINET","uom":"PIECE"}]}

    ,{"soHeader":{"requiredDeliveryDate":"09-26-2022","storeID":"107","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-064526","wareHouseId":"110"},"soLine":[{"lineReference":1,"orderType":"N","orderedQty":6.0,"sku":"0203010977","skuDescription":"BASKET W/BUTTERFLY PINK","uom":"PIECE"},{"lineReference":2,"orderType":"N","orderedQty":6.0,"sku":"0203010979","skuDescription":"BASKET W/BUTTERFLY PURPLE","uom":"PIECE"},{"lineReference":3,"orderType":"N","orderedQty":6.0,"sku":"0203010978","skuDescription":"BASKET W/BUTTERFLY BLUE","uom":"PIECE"},{"lineReference":4,"orderType":"N","orderedQty":12.0,"sku":"0203013662","skuDescription":"STORAGE BOX 21.5x21.5x13cm PINK","uom":"PIECE"},{"lineReference":5,"orderType":"N","orderedQty":6.0,"sku":"0203013749","skuDescription":"BASKET 26x19x15.54cm YELLOW","uom":"PIECE"},{"lineReference":6,"orderType":"N","orderedQty":6.0,"sku":"0203013748","skuDescription":"BASKET 26x19x15.54cm ORANGE","uom":"PIECE"},{"lineReference":7,"orderType":"N","orderedQty":12.0,"sku":"0203013661","skuDescription":"STORAGE BOX 21.5x21.5x13cm COFFEE","uom":"PIECE"},{"lineReference":8,"orderType":"N","orderedQty":6.0,"sku":"0203013664","skuDescription":"STORAGE BASKET 20.3x20x15.5cm BEIGE","uom":"PIECE"},{"lineReference":9,"orderType":"N","orderedQty":24.0,"sku":"0203011601","skuDescription":"TABLE TRASH CAN10x12.5 WHITE","uom":"PIECE"},{"lineReference":10,"orderType":"N","orderedQty":12.0,"sku":"0203013762","skuDescription":"STORAGE BUCKET 10x10x11cm GREEN","uom":"PIECE"},{"lineReference":11,"orderType":"N","orderedQty":12.0,"sku":"0203013646","skuDescription":"BASKET 33.5x27.5x12cm GREY","uom":"PIECE"},{"lineReference":12,"orderType":"N","orderedQty":6.0,"sku":"0203013645","skuDescription":"BASKET 33.5x27.5x12cm BEIGE","uom":"PIECE"},{"lineReference":13,"orderType":"N","orderedQty":12.0,"sku":"0203013641","skuDescription":"STORAGE BASKET 29x22.5x12cm BLUE","uom":"PIECE"},{"lineReference":14,"orderType":"N","orderedQty":4.0,"sku":"0203012357","skuDescription":"MULTI PURPOSE PLASTIC BASKET W/HANDLE 26CM BLUE","uom":"PIECE"},{"lineReference":15,"orderType":"N","orderedQty":4.0,"sku":"0203012359","skuDescription":"MULTI PURPOSE PLASTIC BASKET W/HANDLE 26CM COFFEE","uom":"PIECE"},{"lineReference":16,"orderType":"N","orderedQty":4.0,"sku":"0203012366","skuDescription":"MULTI PURPOSE PLASTIC BASKET W/HANDLE 19CM BLUE","uom":"PIECE"}]}

    ,{"soHeader":{"requiredDeliveryDate":"09-26-2022","storeID":"102","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-064541","wareHouseId":"110"},"soLine":[{"lineReference":1,"orderType":"S","orderedQty":1.0,"sku":"185257","skuDescription":"UMBRELLA 9FT -BELLEUVE","uom":"PIECE"},{"lineReference":2,"orderType":"S","orderedQty":1.0,"sku":"0203013263","skuDescription":"3PC SOFA SET","uom":"SET"}]}

    ,{"soHeader":{"requiredDeliveryDate":"09-27-2022","storeID":"102","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-064540","wareHouseId":"110"},"soLine":[{"lineReference":1,"orderType":"N","orderedQty":12.0,"sku":"0203011582","skuDescription":"ROUND COPPER CRISPER 2pcs","uom":"PIECE"},{"lineReference":2,"orderType":"N","orderedQty":12.0,"sku":"0203011583","skuDescription":"SQURE COPPER CRISPER 2pcs","uom":"PIECE"},{"lineReference":3,"orderType":"N","orderedQty":4.0,"sku":"0203010360","skuDescription":"FRY PAN 35*20CM","uom":"PIECE"},{"lineReference":4,"orderType":"N","orderedQty":6.0,"sku":"0203012220","skuDescription":"ROUND STORAGE BASKET GREY 22.5x6.7cm","uom":"PIECE"},{"lineReference":5,"orderType":"N","orderedQty":12.0,"sku":"0203012221","skuDescription":"ROUND STORAGE BASKET GREY 30x10cm","uom":"PIECE"},{"lineReference":6,"orderType":"N","orderedQty":24.0,"sku":"020306124","skuDescription":"POPCORN BUCKET W/COVER 16.6 X 21","uom":"PIECE"},{"lineReference":7,"orderType":"N","orderedQty":6.0,"sku":"022241342","skuDescription":"PLACE MATS 30X40cm 100PK CREAM - SOFT SELECTION","uom":"PIECE"},{"lineReference":8,"orderType":"N","orderedQty":2.0,"sku":"021581103","skuDescription":"16PC DINNER SET OVI LEMON STYLE/9104","uom":"SET"},{"lineReference":9,"orderType":"N","orderedQty":2.0,"sku":"0203011454","skuDescription":"WOODEN TRAY RECT 40X30cm BLUE-SADU DESIGN","uom":"PIECE"},{"lineReference":10,"orderType":"N","orderedQty":6.0,"sku":"020307906","skuDescription":"SILICON CHOCLATE MOULD MUME FLOWER SHAPE","uom":"PIECE"},{"lineReference":11,"orderType":"N","orderedQty":12.0,"sku":"022241060","skuDescription":"3PLY NAPKINS 33X33CM 20PK OLIVE TWIG","uom":"PIECE"},{"lineReference":12,"orderType":"N","orderedQty":12.0,"sku":"022241185","skuDescription":"TABLE CLOTH 120X180cm GREY SOFT SELECTION","uom":"PIECE"},{"lineReference":13,"orderType":"N","orderedQty":6.0,"sku":"0203012275","skuDescription":"ACRYLIC SUGAR BOWL W/SPOON","uom":"PIECE"},{"lineReference":14,"orderType":"N","orderedQty":6.0,"sku":"020306938","skuDescription":"CHUCK SHELF","uom":"PIECE"},{"lineReference":15,"orderType":"N","orderedQty":12.0,"sku":"020306965","skuDescription":"BRUSH","uom":"PIECE"},{"lineReference":16,"orderType":"N","orderedQty":6.0,"sku":"020302120","skuDescription":"STAINLESS STRAINER 19.5 W/HANDLE","uom":"PIECE"},{"lineReference":17,"orderType":"N","orderedQty":12.0,"sku":"0203012920","skuDescription":"CERAMIC MUG  GREEN","uom":"PIECE"},{"lineReference":18,"orderType":"N","orderedQty":12.0,"sku":"0203013758","skuDescription":"CERAMIC MUG LIGHT ORANGE","uom":"PIECE"},{"lineReference":19,"orderType":"N","orderedQty":6.0,"sku":"022241216","skuDescription":"JUICE GLASS 5CMX17.5CM 5PK CRYSTAL CLEAR","uom":"PIECE"},{"lineReference":20,"orderType":"N","orderedQty":2.0,"sku":"0203010359","skuDescription":"CAST IRON SKILLET 39*18CM","uom":"PIECE"},{"lineReference":21,"orderType":"N","orderedQty":12.0,"sku":"022241187","skuDescription":"TABLE CLOTH 120X180cm TURQUOISE SOFT SELECTION","uom":"PIECE"},{"lineReference":22,"orderType":"N","orderedQty":4.0,"sku":"0203011392","skuDescription":"16CM DEEP PAN","uom":"PIECE"},{"lineReference":23,"orderType":"N","orderedQty":4.0,"sku":"0203011393","skuDescription":"20CM DEEP PAN","uom":"PIECE"},{"lineReference":24,"orderType":"N","orderedQty":3.0,"sku":"9027626","skuDescription":"DISH PLATE HOLDER EXC.DUO S.L STEEL","uom":"PIECE"}]}

    ,{"soHeader":{"requiredDeliveryDate":"09-26-2022","storeID":"107","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-064542","wareHouseId":"110"},"soLine":[{"lineReference":25,"orderType":"S","orderedQty":187.0,"sku":"9019115","skuDescription":"SIP-A-CUP","uom":"PIECE"}]}

    ,{"soHeader":{"requiredDeliveryDate":"09-27-2022","storeID":"103","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-064559","wareHouseId":"110"},"soLine":[{"lineReference":1,"orderType":"S","orderedQty":1.0,"sku":"0203013583","skuDescription":"PAVILION3X4M","uom":"SET"}]}

    ,{"soHeader":{"requiredDeliveryDate":"09-26-2022","storeID":"107","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-064563","wareHouseId":"110"},"soLine":[{"lineReference":1,"orderType":"S","orderedQty":5.0,"sku":"9028578","skuDescription":"CLOTHES STAND BLK","uom":"PIECE"}]}

    ,{"soHeader":{"requiredDeliveryDate":"09-26-2022","storeID":"102","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-064545","wareHouseId":"110"},"soLine":[{"lineReference":1,"orderType":"S","orderedQty":8.0,"sku":"025051001","skuDescription":"50PC VELVET HANGER W/NOTCHES BLACK","uom":"PIECE"},{"lineReference":2,"orderType":"S","orderedQty":24.0,"sku":"025051005","skuDescription":"20PC VELVET HANGER GRY","uom":"PIECE"},{"lineReference":3,"orderType":"S","orderedQty":50.0,"sku":"0203013459","skuDescription":"5pc METAL NONSLIP HANGER -GRAY","uom":"PIECE"},{"lineReference":4,"orderType":"S","orderedQty":6.0,"sku":"9013597","skuDescription":"MAGIC SLIDERS MINI JACK","uom":"PIECE"},{"lineReference":5,"orderType":"S","orderedQty":16.0,"sku":"024911005","skuDescription":"GARMENT RACK SINGLE EXTENDABLE","uom":"PIECE"},{"lineReference":6,"orderType":"S","orderedQty":6.0,"sku":"9020000","skuDescription":"OVER DOOR QUAD 5 HOOK RACK -FORMA ULTRA","uom":"PIECE"}]}

    ,{"soHeader":{"requiredDeliveryDate":"09-26-2022","storeID":"103","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-064568","wareHouseId":"110"},"soLine":[{"lineReference":7,"orderType":"S","orderedQty":6.0,"sku":"0203012182","skuDescription":"STORAGE BOX CLEAR 55x40x32cm","uom":"PIECE"}]}

    ,{"soHeader":{"requiredDeliveryDate":"09-27-2022","storeID":"103","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-064570","wareHouseId":"110"},"soLine":[{"lineReference":1,"orderType":"S","orderedQty":3.0,"sku":"160423","skuDescription":"HIGH BACK CHAIR -ERGONOMIC - RED","uom":"PIECE"},{"lineReference":2,"orderType":"S","orderedQty":2.0,"sku":"21309697301","skuDescription":"ADIRONDACK CHAIR WHT","uom":"PIECE"},{"lineReference":3,"orderType":"S","orderedQty":1.0,"sku":"023861028","skuDescription":"4PC TABLE AND CHAIR SET GRY W/GRYCUSHION","uom":"SET"}]}

    ,{"soHeader":{"requiredDeliveryDate":"09-28-2022","storeID":"103","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-064571","wareHouseId":"110"},"soLine":[{"lineReference":1,"orderType":"S","orderedQty":1.0,"sku":"272356","skuDescription":"CHAR-BROIL, PERFORMANCE SERIES, LP GAS GRILL, 5 BURNER S.STE","uom":"PIECE"}]}

    ,{"soHeader":{"requiredDeliveryDate":"09-27-2022","storeID":"103","storeName":"Amgharah (Innerworks)","transferOrderNumber":"TO-064572","wareHouseId":"110"},"soLine":[{"lineReference":1,"orderType":"S","orderedQty":1.0,"sku":"027611001","skuDescription":"4PC PLASTIC RATTAN SET 5-SEATER-MOCA","uom":"SET"}]}]`);
    // for (let i = 0; i < 30; i++) {
    //   let transferOrderNumber = obj.soHeader.transferOrderNumber.split("-");
    //   let transferOrderNumberValue = Number(transferOrderNumber[1]) + 1;
    //   obj.soHeader.transferOrderNumber = transferOrderNumber[0] + "-" + transferOrderNumberValue;
    //   this.sub.add(this.service.createSo(obj).subscribe(res => {
    //     console.log(res);
    //   }, err => {
    //     console.log(err);
    //   }));
    // }
    // obj.forEach(element => {
    //   this.sub.add(this.service.createSo(element).subscribe(res => {
    //     console.log(res);
    //   }, err => {
    //     console.log(err);
    //   }));
    // });
  }

  validateToggle(){
    if(this.toggle == false){
      this.toggle =  true;
      this.isExpanded =  false;
      this.state = (this.state === 'default' ? 'rotated' : 'default');
      if (this.icon === 'menu') {
        this.icon = 'arrow_back_ios_new';
      } else {
        this.icon = 'menu'
      }
    }
    
  }
}
