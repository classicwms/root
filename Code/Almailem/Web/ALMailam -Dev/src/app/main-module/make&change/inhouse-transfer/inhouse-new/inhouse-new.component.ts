import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { CommonService } from "src/app/common-service/common-service.service";
import { AuthService } from "src/app/core/core";
import { BinPopupComponent } from "./bin-popup/bin-popup.component";
import { SkuPopupComponent } from "./sku-popup/sku-popup.component";
import { StockPopupComponent } from "./stock-popup/stock-popup.component";
import { Location } from "@angular/common";
export interface stock {
  no: string;
  lineno: string;
  supcode: string;
  one: string;
  receipt: string;
}
const ELEMENT_DATA: stock[] = [
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
];
export interface sku {
  no: string;
  lineno: string;
  supcode: string;
  one: string;
  receipt: string;
}
const ELEMENT_DATA2: sku[] = [
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
];
export interface bin {
  no: string;
  lineno: string;
  supcode: string;
  one: string;
  receipt: string;
}
const ELEMENT_DATA3: bin[] = [
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
  { no: "readonly", lineno: 'readonly', supcode: 'readonly', one: 'readonly', receipt: 'readonly', },
];
@Component({
  selector: 'app-inhouse-new',
  templateUrl: './inhouse-new.component.html',
  styleUrls: ['./inhouse-new.component.scss']
})
export class InhouseNewComponent implements OnInit {
  data: any = {
    companyCodeId: this.auth.companyId,
    plantId: this.auth.plantId,
    createdBy: this.auth.userID,
    createdOn: this.cs.todayapi(),
    inhouseTransferLine: [],
    languageId: this.auth.languageId,

    transferMethod: "ONESTEP",
    transferTypeId: null,

    warehouseId: this.auth.warehouseId
  };
  screenid: 1058 | undefined;
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




  ngOnInit(): void {
  }


  constructor(
    public dialog: MatDialog, private auth: AuthService, private cs: CommonService, private location: Location,) { }



  displayedColumns: string[] = ['select', 'no', 'lineno', 'supcode',];
  dataSource = new MatTableDataSource<stock>(ELEMENT_DATA);
  selection = new SelectionModel<stock>(true, []);

  displayedColumns2: string[] = ['select', 'no', 'lineno', 'supcode',];
  dataSource2 = new MatTableDataSource<sku>(ELEMENT_DATA2);
  selection2 = new SelectionModel<sku>(true, []);

  displayedColumns3: string[] = ['select', 'lineno', 'supcode',];
  dataSource3 = new MatTableDataSource<bin>(ELEMENT_DATA3);
  selection3 = new SelectionModel<bin>(true, []);

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
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
  checkboxLabel(row?: stock): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  }


  opensku(): void {

    const dialogRef = this.dialog.open(SkuPopupComponent, {
      disableClose: true,
      width: '100%',
      maxWidth: '75%',
      position: { top: '9%', },
      data: this.data
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result)
        this.location.back();
    });
  }

  openstock(): void {

    const dialogRef = this.dialog.open(StockPopupComponent, {
      disableClose: true,
      width: '90%',
      maxWidth: '90%',
      position: { top: '9%', },
      data: this.data
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result)

        this.location.back();
    });
  }
  openbin(): void {

    const dialogRef = this.dialog.open(BinPopupComponent, {
      disableClose: true,
      width: '90%',
      maxWidth: '90%',
      position: { top: '9%', },
      data: this.data
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result)

        this.location.back();
    });
  }
  disabled = false;
  step = 0;

  setStep(index: number) {
    this.step = index;
  }

  nextStep() {
    this.step++;
  }

  prevStep() {
    this.step--;
  }

  panelOpenState = false;

}

