import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdhocmoduleidComponent } from './adhocmoduleid/adhocmoduleid.component';
import { AisleComponent } from './aisle/aisle.component';
import { ApprovalidComponent } from './approvalid/approvalid.component';
import { ApprovalprocessidComponent } from './approvalprocessid/approvalprocessid.component';
import { BarcodesubtypeidComponent } from './barcodesubtypeid/barcodesubtypeid.component';
import { BarcodetypeidComponent } from './barcodetypeid/barcodetypeid.component';
import { BillingformatComponent } from './billingformat/billingformat.component';
import { BillingfrequencyComponent } from './billingfrequency/billingfrequency.component';
import { BillingmodeComponent } from './billingmode/billingmode.component';
import { BinclassidComponent } from './binclassid/binclassid.component';
import { BinsectionidComponent } from './binsectionid/binsectionid.component';
import { CityComponent } from './city/city.component';
import { CompanyComponent } from './company/company.component';
import { ControlprocessidComponent } from './controlprocessid/controlprocessid.component';
import { ControltypeidComponent } from './controltypeid/controltypeid.component';
import { CountryComponent } from './country/country.component';
import { CurrencyComponent } from './currency/currency.component';
import { CyclecounttypeidComponent } from './cyclecounttypeid/cyclecounttypeid.component';
import { DateformatidComponent } from './dateformatid/dateformatid.component';
import { DecimalnotationidComponent } from './decimalnotationid/decimalnotationid.component';
import { DockidComponent } from './dockid/dockid.component';
import { DooridComponent } from './doorid/doorid.component';
import { EmployeeidComponent } from './employeeid/employeeid.component';
import { FloorComponent } from './floor/floor.component';
import { HandlingequipmentidComponent } from './handlingequipmentid/handlingequipmentid.component';
import { HandlingequipmentidService } from './handlingequipmentid/handlingequipmentid.service';
import { HandlingunitidComponent } from './handlingunitid/handlingunitid.component';
import { InboundorderstatusidComponent } from './inboundorderstatusid/inboundorderstatusid.component';
import { InboundordertypeidComponent } from './inboundordertypeid/inboundordertypeid.component';
import { ItemgroupComponent } from './itemgroup/itemgroup.component';
import { ItemtypeComponent } from './itemtype/itemtype.component';
import { LanguageidComponent } from './languageid/languageid.component';
import { LevelComponent } from './level/level.component';
import { MenuNewComponent } from './menu/menu-new/menu-new.component';
import { MenuComponent } from './menu/menu.component';
import { ModuleidComponent } from './moduleid/moduleid.component';
import { MovementtypeidComponent } from './movementtypeid/movementtypeid.component';
import { OutboundorderstatusidComponent } from './outboundorderstatusid/outboundorderstatusid.component';
import { OutboundordertypeidComponent } from './outboundordertypeid/outboundordertypeid.component';
import { PalletizationLevelIdComponent } from './palletization-level-id/palletization-level-id.component';
import { PaymentmodeComponent } from './paymentmode/paymentmode.component';
import { PaymenttermidComponent } from './paymenttermid/paymenttermid.component';
import { PlantComponent } from './plant/plant.component';
import { ProcessComponent } from './process/process.component';
import { ProcessidComponent } from './processid/processid.component';
import { RefdoctypeidComponent } from './refdoctypeid/refdoctypeid.component';
import { ReturntypeidComponent } from './returntypeid/returntypeid.component';
import { RowComponent } from './row/row.component';
import { RowService } from './row/row.service';
import { ServiceidComponent } from './serviceid/serviceid.component';
import { ShelfidComponent } from './shelfid/shelfid.component';
import { SpanidComponent } from './spanid/spanid.component';
import { SpecialstockindicatorComponent } from './specialstockindicator/specialstockindicator.component';
import { StateComponent } from './state/state.component';
import { StatusidComponent } from './statusid/statusid.component';
import { StatussmessagesidComponent } from './statussmessagesid/statussmessagesid.component';
import { StocktypeComponent } from './stocktype/stocktype.component';
import { StorageSectionComponent } from './storage-section/storage-section.component';
import { StoragebintypeComponent } from './storagebintype/storagebintype.component';
import { StorageclassComponent } from './storageclass/storageclass.component';
import { StoragetypeComponent } from './storagetype/storagetype.component';
import { StratergyComponent } from './stratergy/stratergy.component';
import { SubitemgroupComponent } from './subitemgroup/subitemgroup.component';
import { SublevelidComponent } from './sublevelid/sublevelid.component';
import { SubmovementtypeidComponent } from './submovementtypeid/submovementtypeid.component';
import { ThreePlComponent } from './three-pl/three-pl/three-pl.component';
import { TransfertypeidComponent } from './transfertypeid/transfertypeid.component';
import { UomComponent } from './uom/uom.component';
import { UsertypeComponent } from './usertype/usertype.component';
import { VariantidComponent } from './variantid/variantid.component';

import { VerticalComponent } from './vertical/vertical.component';
import { WarehouseComponent } from './warehouse/warehouse.component';
import { WarehousetypeComponent } from './warehousetype/warehousetype.component';
import { WorkcenterComponent } from './workcenter/workcenter.component';
import { NumberrangeComponent } from './numberrange/numberrange.component';
import { ModuleidNewComponent } from './moduleid/moduleid-new/moduleid-new.component';
import { SetupAccessComponent } from './setup-access/setup-access.component';

const routes: Routes = [
{
  path: 'vertical',
  component: VerticalComponent
},
{
  path: 'company',
  component: CompanyComponent
},
{
  path: 'plant',
  component: PlantComponent
},
{
  path: 'warehouse',
  component: WarehouseComponent
},
{
  path: 'storagesection',
  component: StorageSectionComponent
},

{
  path: 'status',
  component: StatusidComponent
},
{
  path: 'aisle',
  component: AisleComponent
},
{
  path: 'storageclass',
  component: StorageclassComponent
},
{
  path: 'storagetype',
  component: StoragetypeComponent
},
{
  path: 'storagebintype',
  component: StoragebintypeComponent
},
{
  path: 'processsequence',
  component:  ProcessComponent
},
{
  path: 'usertype',
  component:  UsertypeComponent
},
{
  path: 'uom',
  component: UomComponent
},
{
  path: 'level',
  component: LevelComponent
},
{
  path: 'row',
  component: RowComponent
},
{
  path: 'currency',
  component: CurrencyComponent
},
{
  path: 'barcodetypeid',
  component: BarcodetypeidComponent
},
{
  path: 'barcodesubtypeid',
  component: BarcodesubtypeidComponent 
},
{
  path: 'stratergy',
  component: StratergyComponent 
},
{
  path: 'warehousetype',
  component: WarehousetypeComponent 
},
{
  path: 'menu',
  component:  MenuComponent
},
{
  path: 'country',
  component:  CountryComponent
},
{
  path: 'state',
  component:  StateComponent
},
{
  path: 'city',
  component:  CityComponent
},
{
  path: 'itemtype',
  component:  ItemtypeComponent
},
{
  path: 'itemgroup',
  component:  ItemgroupComponent
},
{
  path: 'floor',
  component:  FloorComponent
},
{
  path: 'subitemgroup',
  component:  SubitemgroupComponent
},
{
  path: 'doorid',
  component:  DooridComponent
},
{
  path: 'moduleid',
  component:  ModuleidComponent
},
{
  path: 'moduleidNew/:code',
  component:  ModuleidNewComponent
},
{
  path: 'moduleid',
  component:  ModuleidComponent
},
{
  path: 'adhocmoduleid',
  component:  AdhocmoduleidComponent
},
{
  path: 'palletization-level-id',
  component:  PalletizationLevelIdComponent
},
{
  path: 'employeeid',
  component:  EmployeeidComponent
},
{
  path: 'dockid',
  component:  DockidComponent
},
{
  path: 'workcenter',
  component:  WorkcenterComponent
},
{
  path: 'outboundorderstatusid',
  component:  OutboundorderstatusidComponent
},
{
  path: 'inboundorderstatusid',
  component:  InboundorderstatusidComponent
},
{
  path: 'controltypeid',
  component:  ControltypeidComponent
},
{
  path: 'approvalid',
  component:  ApprovalidComponent
},
{
  path: 'refdoctypeid',
  component:  RefdoctypeidComponent
},
{
  path: 'controlprocessid',
  component:  ControlprocessidComponent
},
{
  path: 'spanid',
  component:  SpanidComponent
},
{
  path: 'shelfid',
  component:  ShelfidComponent
},
{
  path: 'binsectionid',
  component:  BinsectionidComponent
},
{
  path: 'dateformatid',
  component:  DateformatidComponent,
},
{
  path: 'decimalnotationid',
  component:  DecimalnotationidComponent,
},
{
  path: 'languageid',
  component:  LanguageidComponent,
},
{
  path: 'outboundordertypeid',
  component:  OutboundordertypeidComponent,
},
{
  path: 'variantid',
  component:  VariantidComponent,
},
{
  path: 'statussmessagesid',
  component:  StatussmessagesidComponent,
},
{
  path: 'binclassid',
  component:  BinclassidComponent,
},
{
  path: 'movementtypeid',
  component:  MovementtypeidComponent,
},
{
  path: 'stocktype',
  component:  StocktypeComponent,
},
{
  path: 'handlingequipmentid',
  component: HandlingequipmentidComponent ,
},
{
  path: 'handlingunitid',
  component: HandlingunitidComponent ,
},
{
  path: 'returntypeid',
  component: ReturntypeidComponent ,
},
{
  path: 'specialstockindicator',
  component: SpecialstockindicatorComponent ,
},
{
  path: 'processid',
  component: ProcessidComponent ,
},
{
  path: 'sublevelid',
  component: SublevelidComponent,
},
{
  path: 'inboundordertypeid',
  component: InboundordertypeidComponent,
},
{
  path: 'cyclecounttypeid',
  component: CyclecounttypeidComponent,
},
{
  path: 'approvalprocessid',
  component: ApprovalprocessidComponent,
},
{
  path: 'submovementtypeid',
  component: SubmovementtypeidComponent,
},
{
  path: 'transfertypeid',
  component: TransfertypeidComponent,
},
{
  path: 'threepl',
  component: ThreePlComponent,
},
{
  path: 'serviceid',
  component: ServiceidComponent,
},
{
  path: 'paymenttermid',
  component: PaymenttermidComponent,
},
{
  path: 'billingmodeid',
  component: BillingmodeComponent,
},
{
  path: 'billingfrequency',
  component: BillingfrequencyComponent,
},
{
  path: 'billingformat',
  component: BillingformatComponent,
},
{
  path: 'paymentmode',
  component: PaymentmodeComponent,
},
{
  path: 'numberrange',
  component: NumberrangeComponent,
},
{
  path: 'setupAccess',
  component: SetupAccessComponent,
},









{
  path: '',
  redirectTo: 'vertical'
},];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OtherSetupRoutingModule { }
