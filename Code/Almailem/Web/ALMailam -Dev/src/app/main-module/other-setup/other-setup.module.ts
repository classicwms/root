import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { OtherSetupRoutingModule } from './other-setup-routing.module';
import { VerticalComponent } from './vertical/vertical.component';
import { SetupTabComponent } from './setup-tab/setup-tab.component';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { CompanyComponent } from './company/company.component';
import { WarehouseComponent } from './warehouse/warehouse.component';
import { PlantComponent } from './plant/plant.component';
import { StorageSectionComponent } from './storage-section/storage-section.component';
import { RowComponent } from './row/row.component';
import { RowNewComponent } from './row/row-new/row-new.component';
import { VerticalNewComponent } from './vertical/vertical-new/vertical-new.component';
import { PlantNewComponent } from './plant/plant-new/plant-new.component';
import { WarehouseNewComponent } from './warehouse/warehouse-new/warehouse-new.component';
import { StorageSectionNewComponent } from './storage-section/storage-section-new/storage-section-new.component';
import { StatusidComponent } from './statusid/statusid.component';
import { StatusidNewComponent } from './statusid/statusid-new/statusid-new.component';
import { CompanyNewComponent } from './company/company-new/company-new.component';
import { AisleComponent } from './aisle/aisle.component';
import { StorageclassComponent } from './storageclass/storageclass.component';
import { StorageclassNewComponent } from './storageclass/storageclass-new/storageclass-new.component';
import { StoragetypeComponent } from './storagetype/storagetype.component';
import { StoragetypeNewComponent } from './storagetype/storagetype-new/storagetype-new.component';
import { StoragebintypeComponent } from './storagebintype/storagebintype.component';
import { StoragebintypeNewComponent } from './storagebintype/storagebintype-new/storagebintype-new.component';
import { ProcessComponent } from './process/process.component';
import { ProcessNewComponent } from './process/process-new/process-new.component';
import { UsertypeComponent } from './usertype/usertype.component';
import { UsertypeNewComponent } from './usertype/usertype-new/usertype-new.component';
import { UomComponent } from './uom/uom.component';
import { UomNewComponent } from './uom/uom-new/uom-new.component';
import { LevelComponent } from './level/level.component';
import { LevelNewComponent } from './level/level-new/level-new.component';
import { CurrencyComponent } from './currency/currency.component';
import { CurrencyNewComponent } from './currency/currency-new/currency-new.component';
import { BarcodetypeidComponent } from './barcodetypeid/barcodetypeid.component';
import { BarcodetypeidNewComponent } from './barcodetypeid/barcodetypeid-new/barcodetypeid-new.component';
import { BarcodesubtypeidComponent } from './barcodesubtypeid/barcodesubtypeid.component';
import { BarcodesubtypeidNewComponent } from './barcodesubtypeid/barcodesubtypeid-new/barcodesubtypeid-new.component';
import { StratergyComponent } from './stratergy/stratergy.component';
import { StratergyNewComponent } from './stratergy/stratergy-new/stratergy-new.component';
import { WarehousetypeComponent } from './warehousetype/warehousetype.component';
import { WarehousetypeNewComponent } from './warehousetype/warehousetype-new/warehousetype-new.component';
import { MenuComponent } from './menu/menu.component';
import { MenuNewComponent } from './menu/menu-new/menu-new.component';
import { CountryComponent } from './country/country.component';
import { CountryNewComponent } from './country/country-new/country-new.component';
import { StateComponent } from './state/state.component';
import { StateNewComponent } from './state/state-new/state-new.component';
import { CityComponent } from './city/city.component';
import { CityNewComponent } from './city/city-new/city-new.component';
import { ItemtypeComponent } from './itemtype/itemtype.component';
import { ItemtypeNewComponent } from './itemtype/itemtype-new/itemtype-new.component';
import { ItemgroupComponent } from './itemgroup/itemgroup.component';
import { ItemgroupNewComponent } from './itemgroup/itemgroup-new/itemgroup-new.component';
import { FloorComponent } from './floor/floor.component';
import { FloorNewComponent } from './floor/floor-new/floor-new.component';
import { SubitemgroupComponent } from './subitemgroup/subitemgroup.component';
import { SubitemgroupNewComponent } from './subitemgroup/subitemgroup-new/subitemgroup-new.component';
import { DooridComponent } from './doorid/doorid.component';
import { DooridNewComponent } from './doorid/doorid-new/doorid-new.component';
import { ModuleidComponent } from './moduleid/moduleid.component';
import { ModuleidNewComponent } from './moduleid/moduleid-new/moduleid-new.component';
import { AdhocmoduleidComponent } from './adhocmoduleid/adhocmoduleid.component';
import { AdhocmoduleidNewComponent } from './adhocmoduleid/adhocmoduleid-new/adhocmoduleid-new.component';
import { PalletizationLevelIdComponent } from './palletization-level-id/palletization-level-id.component';
import { PalletizationLevelIdNewComponent } from './palletization-level-id/palletization-level-id-new/palletization-level-id-new.component';
import { EmployeeidComponent } from './employeeid/employeeid.component';
import { EmployeeidNewComponent } from './employeeid/employeeid-new/employeeid-new.component';
import { DockidComponent } from './dockid/dockid.component';
import { DockidNewComponent } from './dockid/dockid-new/dockid-new.component';
import { WorkcenterComponent } from './workcenter/workcenter.component';
import { WorkcenterNewComponent } from './workcenter/workcenter-new/workcenter-new.component';
import { OutboundorderstatusidComponent } from './outboundorderstatusid/outboundorderstatusid.component';
import { OutboundorderstatusidNewComponent } from './outboundorderstatusid/outboundorderstatusid-new/outboundorderstatusid-new.component';
import { InboundorderstatusidComponent } from './inboundorderstatusid/inboundorderstatusid.component';
import { InboundorderstatusNewComponent } from './inboundorderstatusid/inboundorderstatus-new/inboundorderstatus-new.component';
import { ControltypeidComponent } from './controltypeid/controltypeid.component';
import { ControltypeNewComponent } from './controltypeid/controltype-new/controltype-new.component';
import { ApprovalidComponent } from './approvalid/approvalid.component';
import { ApprovalidNewComponent } from './approvalid/approvalid-new/approvalid-new.component';
import { RefdoctypeidComponent } from './refdoctypeid/refdoctypeid.component';
import { RefdoctypeidNewComponent } from './refdoctypeid/refdoctypeid-new/refdoctypeid-new.component';
import { ControlprocessidComponent } from './controlprocessid/controlprocessid.component';
import { ControlprocessidNewComponent } from './controlprocessid/controlprocessid-new/controlprocessid-new.component';
import { SpanidComponent } from './spanid/spanid.component';
import { SpanidNewComponent } from './spanid/spanid-new/spanid-new.component';
import { ShelfidComponent } from './shelfid/shelfid.component';
import { ShelfidNewComponent } from './shelfid/shelfid-new/shelfid-new.component';
import { BinsectionidComponent } from './binsectionid/binsectionid.component';
import { BinsectionidNewComponent } from './binsectionid/binsectionid-new/binsectionid-new.component';
import { DateformatidComponent } from './dateformatid/dateformatid.component';
import { DateformatidNewComponent } from './dateformatid/dateformatid-new/dateformatid-new.component';
import { DecimalnotationidComponent } from './decimalnotationid/decimalnotationid.component';
import { DecimalnotationidNewComponent } from './decimalnotationid/decimalnotationid-new/decimalnotationid-new.component';
import { LanguageidComponent } from './languageid/languageid.component';
import { LanguageidNewComponent } from './languageid/languageid-new/languageid-new.component';
import { OutboundordertypeidComponent } from './outboundordertypeid/outboundordertypeid.component';
import { OutboundordertypeidNewComponent } from './outboundordertypeid/outboundordertypeid-new/outboundordertypeid-new.component';
import { VariantidComponent } from './variantid/variantid.component';
import { VariantidNewComponent } from './variantid/variantid-new/variantid-new.component';
import { StatussmessagesidComponent } from './statussmessagesid/statussmessagesid.component';
import { StatusmessagesidNewComponent } from './statussmessagesid/statusmessagesid-new/statusmessagesid-new.component';
import { BinclassidComponent } from './binclassid/binclassid.component';
import { BinclassidNewComponent } from './binclassid/binclassid-new/binclassid-new.component';
import { MovementtypeidComponent } from './movementtypeid/movementtypeid.component';
import { MovementtypeidNewComponent } from './movementtypeid/movementtypeid-new/movementtypeid-new.component';
import { EnterpriseComponent } from './enterprise/enterprise.component';
import { StorageTabComponent } from './storage-tab/storage-tab.component';
import { WarehouseTabComponent } from './warehouse-tab/warehouse-tab.component';
import { OrderTabComponent } from './order-tab/order-tab.component';
import { ProductTabComponent } from './product-tab/product-tab.component';
import { MakeandchangeTabComponent } from './makeandchange-tab/makeandchange-tab.component';
import { AisleNewComponent } from './aisle/aisle-new/aisle-new.component';
import { StocktypeComponent } from './stocktype/stocktype.component';
import { StocktypeNewComponent } from './stocktype/stocktype-new/stocktype-new.component';
import { HandlingequipmentidComponent } from './handlingequipmentid/handlingequipmentid.component';
import { HandlingequipmentidNewComponent } from './handlingequipmentid/handlingequipmentid-new/handlingequipmentid-new.component';
import { HandlingunitidComponent } from './handlingunitid/handlingunitid.component';
import { HandlingunitNewComponent } from './handlingunitid/handlingunit-new/handlingunit-new.component';
import { ReturntypeidComponent } from './returntypeid/returntypeid.component';
import { ReturtypeidNewComponent } from './returntypeid/returtypeid-new/returtypeid-new.component';
import { SpecialstockindicatorComponent } from './specialstockindicator/specialstockindicator.component';
import { SpecialstockindicatorNewComponent } from './specialstockindicator/specialstockindicator-new/specialstockindicator-new.component';
import { ProcessidComponent } from './processid/processid.component';
import { ProcessidNewComponent } from './processid/processid-new/processid-new.component';
import { SublevelidComponent } from './sublevelid/sublevelid.component';
import { SublevelidNewComponent } from './sublevelid/sublevelid-new/sublevelid-new.component';
import { InboundordertypeidComponent } from './inboundordertypeid/inboundordertypeid.component';
import { InboundordertypeidNewComponent } from './inboundordertypeid/inboundordertypeid-new/inboundordertypeid-new.component';
import { CyclecounttypeidComponent } from './cyclecounttypeid/cyclecounttypeid.component';
import { CyclecounttypeidNewComponent } from './cyclecounttypeid/cyclecounttypeid-new/cyclecounttypeid-new.component';
import { ApprovalprocessidComponent } from './approvalprocessid/approvalprocessid.component';
import { ApprovalprocessidNewComponent } from './approvalprocessid/approvalprocessid-new/approvalprocessid-new.component';
import { SubmovementtypeidComponent } from './submovementtypeid/submovementtypeid.component';
import { SubmovemnttypeidNewComponent } from './submovementtypeid/submovemnttypeid-new/submovemnttypeid-new.component';
import { TransfertypeidComponent } from './transfertypeid/transfertypeid.component';
import { TransfertypeidNewComponent } from './transfertypeid/transfertypeid-new/transfertypeid-new.component';
import { ThreePlComponent } from './three-pl/three-pl/three-pl.component';
import { ServiceidComponent } from './serviceid/serviceid.component';
import { ServiceidNewComponent } from './serviceid/serviceid-new/serviceid-new.component';
import { PaymenttermidComponent } from './paymenttermid/paymenttermid.component';
import { PaymenttermidNewComponent } from './paymenttermid/paymenttermid-new/paymenttermid-new.component';
import { BillingmodeComponent } from './billingmode/billingmode.component';
import { BillingmodeNewComponent } from './billingmode/billingmode-new/billingmode-new.component';
import { BillingfrequencyComponent } from './billingfrequency/billingfrequency.component';
import { BillingfrequencyNewComponent } from './billingfrequency/billingfrequency-new/billingfrequency-new.component';
import { BillingformatComponent } from './billingformat/billingformat.component';
import { BillingformatNewComponent } from './billingformat/billingformat-new/billingformat-new.component';
import { PaymentmodeComponent } from './paymentmode/paymentmode.component';
import { PaymentmodeNewComponent } from './paymentmode/paymentmode-new/paymentmode-new.component';
import { NumberrangeComponent } from './numberrange/numberrange.component';
import { NumberrangeNewComponent } from './numberrange/numberrange-new/numberrange-new.component';
import { SetupAccessComponent } from './setup-access/setup-access.component';



@NgModule({
  declarations: [
    VerticalComponent,
    SetupTabComponent,
    CompanyComponent,
    WarehouseComponent,
    PlantComponent,
    StorageSectionComponent,
    RowComponent,
    RowNewComponent,
    VerticalNewComponent,
    PlantNewComponent,
    WarehouseNewComponent,
    StorageSectionNewComponent,
    StatusidComponent,
    StatusidNewComponent,
    CompanyNewComponent,
    AisleComponent,
    StorageclassComponent,
    StorageclassNewComponent,
    StoragetypeComponent,
    StoragetypeNewComponent,
    StoragebintypeComponent,
    StoragebintypeNewComponent,
    ProcessComponent,
    ProcessNewComponent,
    UsertypeComponent,
    UsertypeNewComponent,
    UomComponent,
    UomNewComponent,
    LevelComponent,
    LevelNewComponent,
    CurrencyComponent,
    CurrencyNewComponent,
    BarcodetypeidComponent,
    BarcodetypeidNewComponent,
    BarcodesubtypeidComponent,
    BarcodesubtypeidNewComponent,
    StratergyComponent,
    StratergyNewComponent,
    WarehousetypeComponent,
    WarehousetypeNewComponent,
    MenuComponent,
    MenuNewComponent,
    CountryComponent,
    CountryNewComponent,
    StateComponent,
    StateNewComponent,
    CityComponent,
    CityNewComponent,
    ItemtypeComponent,
    ItemtypeNewComponent,
    ItemgroupComponent,
    ItemgroupNewComponent,
    FloorComponent,
    FloorNewComponent,
    SubitemgroupComponent,
    SubitemgroupNewComponent,
    DooridComponent,
    DooridNewComponent,
    ModuleidComponent,
    ModuleidNewComponent,
    AdhocmoduleidComponent,
    AdhocmoduleidNewComponent,
    PalletizationLevelIdComponent,
    PalletizationLevelIdNewComponent,
    EmployeeidComponent,
    EmployeeidNewComponent,
    DockidComponent,
    DockidNewComponent,
    WorkcenterComponent,
    WorkcenterNewComponent,
    OutboundorderstatusidComponent,
    OutboundorderstatusidNewComponent,
    InboundorderstatusidComponent,
    InboundorderstatusNewComponent,
    ControltypeidComponent,
    ControltypeNewComponent,
    ApprovalidComponent,
    ApprovalidNewComponent,
    RefdoctypeidComponent,
    RefdoctypeidNewComponent,
    ControlprocessidComponent,
    ControlprocessidNewComponent,
    SpanidComponent,
    SpanidNewComponent,
    ShelfidComponent,
    ShelfidNewComponent,
    BinsectionidComponent,
    BinsectionidNewComponent,
    DateformatidComponent,
    DateformatidNewComponent,
    DecimalnotationidComponent,
    DecimalnotationidNewComponent,
    LanguageidComponent,
    LanguageidNewComponent,
    OutboundordertypeidComponent,
    OutboundordertypeidNewComponent,
    VariantidComponent,
    VariantidNewComponent,
    StatussmessagesidComponent,
    StatusmessagesidNewComponent,
    BinclassidComponent,
    BinclassidNewComponent,
    MovementtypeidComponent,
    MovementtypeidNewComponent,
    EnterpriseComponent,
    StorageTabComponent,
    WarehouseTabComponent,
    OrderTabComponent,
    ProductTabComponent,
    MakeandchangeTabComponent,
    AisleNewComponent,
    StocktypeComponent,
    StocktypeNewComponent,
    HandlingequipmentidComponent,
    HandlingequipmentidNewComponent,
    HandlingunitidComponent,
    HandlingunitNewComponent,
    ReturntypeidComponent,
    ReturtypeidNewComponent,
    SpecialstockindicatorComponent,
    SpecialstockindicatorNewComponent,
    ProcessidComponent,
    ProcessidNewComponent,
    SublevelidComponent,
    SublevelidNewComponent,
    InboundordertypeidComponent,
    InboundordertypeidNewComponent,
    CyclecounttypeidComponent,
    CyclecounttypeidNewComponent,
    ApprovalprocessidComponent,
    ApprovalprocessidNewComponent,
    SubmovementtypeidComponent,
    SubmovemnttypeidNewComponent,
    TransfertypeidComponent,
    TransfertypeidNewComponent,
    ThreePlComponent,
    ServiceidComponent,
    ServiceidNewComponent,
    PaymenttermidComponent,
    PaymenttermidNewComponent,
    BillingmodeComponent,
    BillingmodeNewComponent,
    BillingfrequencyComponent,
    BillingfrequencyNewComponent,
    BillingformatComponent,
    BillingformatNewComponent,
    PaymentmodeComponent,
    PaymentmodeNewComponent,
    NumberrangeComponent,
    NumberrangeNewComponent,
    SetupAccessComponent
  ],
  imports: [
    CommonModule,
    OtherSetupRoutingModule,
    SharedModule,
    CommonFieldModule,
    NgMultiSelectDropDownModule.forRoot(),
    AngularMultiSelectModule,
  ],
  exports: [
    VerticalComponent,
    SetupTabComponent
  ]
})
export class OtherSetupModule { }
