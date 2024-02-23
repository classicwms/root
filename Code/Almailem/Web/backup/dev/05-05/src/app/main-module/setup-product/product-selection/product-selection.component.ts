import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit, ViewChild } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { Table } from "primeng/table";
import { CommonService } from "src/app/common-service/common-service.service";
import { SetupProductService } from "../setup-product.service";

// export interface selection {
//   no: string;
//   lineno: string;
//   partner: string;
//   product: string;
//   description: string;
//   refdocno: string;
  
//   }

export interface ItemGroup {
  warehouseId?: string;
  itemTypeId?: number;
  itemGroupId?: number;
  createdBy?: string;
  createdOn?: Date;
  }
  
@Component({
  selector: 'app-product-selection',
  templateUrl: './product-selection.component.html',
  styleUrls: ['./product-selection.component.scss']
})
export class ProductSelectionComponent implements OnInit {

  constructor(private spinner: NgxSpinnerService,private router: Router, private setupProductService: SetupProductService,
    public dialog: MatDialog,  public cs: CommonService,) {}
    routeto(url: any, id: any) {
      sessionStorage.setItem('crrentmenu', id);
      this.router.navigate([url]);
    }
    advanceFilterShow: boolean;
    @ViewChild('Setupproduct') Setupproduct: Table | undefined;
    product: any;
    selectedproduct : any;

  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  toggleFloat() {
    this.isShowDiv = !this.isShowDiv;  
    this.toggle = !this.toggle;

    if (this.icon === 'expand_more') {
      this.icon = 'chevron_left';
    } else {
      this.icon = 'expand_more'
    }
    this.showFloatingButtons = !this.showFloatingButtons;
     console.log('show:' + this.showFloatingButtons);
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
  applyFilterGlobal($event: any, stringVal: any) {
    this.Setupproduct!.filterGlobal(($event.target as HTMLInputElement).value, stringVal);
  }
 

  ngOnInit(): void {
    /** spinner starts on init */
    this.spinner.show();

    setTimeout(() => {
      /** spinner ends after 5 seconds */
      this.spinner.hide();
    }, 500);

    this.getSetupMasterData();
    
 }

  displayedColumns: string[] = ['select','lineno', 'partner', 'product',  'description','refdocno',];
  dataSource = new MatTableDataSource<ItemGroup>();
  selection = new SelectionModel<ItemGroup>(true, []);

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selectedproduct[0].length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSource.data);
  }

  /** The label for the checkbox on the passed row */
  // checkboxLabel(row?:  selection): string {
  //   if (!row) {
  //     return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
  //   }
  //   return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  // }
  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }

  getSetupMasterData()
  {
    this.setupProductService.getSetupMasters().subscribe(
      result => {
        console.log(result);
        this.dataSource = new MatTableDataSource(result);
      },
      error => {
        console.log(error);
      }
    );
  }

  onEditClick()
  {
    sessionStorage.setItem('itemGroup', JSON.stringify(this.selectedproduct[0]));
    this.routeto('/main/product/itemtype', 1010);
  }

  onNewClick()
  {
    sessionStorage.removeItem('itemGroup');
    this.routeto('/main/product/itemtype', 1010);
  }
  onChange() {
    console.log(this.selectedproduct.length)
    const choosen= this.selectedproduct[this.selectedproduct.length - 1];   
    this.selectedproduct.length = 0;
    this.selectedproduct.push(choosen);
  } 
  downloadexcel() {
    var res: any = [];
    this.product.forEach(x => {
      res.push({
        "S No":x.no,
        "Warehouse ID":x.warehouseId,
        "Item Type ID":x.itemTypeId,
        "Item Group ID":x.itemGroupId,
        "Created  By":x.createdBy,
        "Created On":x.createdOn,
      });
  
    })
    this.cs.exportAsExcel(res, "Setup -Product");
  }
}
