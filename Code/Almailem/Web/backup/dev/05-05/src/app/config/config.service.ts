import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class ConfigService {

    constructor() {
    }


    /**
    * SETUP
    */
    get setupMaster_url(): string {
        return '/wms-enterprise-service/storagesection';
    }

    get company_url(): string {
        return '/wms-enterprise-service/company';
    }

    get plant_url(): string {
        return '/wms-enterprise-service/plant';
    }

    get warehouse_url(): string {
        return '/wms-enterprise-service/warehouse';
    }

    get floor_url(): string {
        return '/wms-enterprise-service/floor';
    }

    get storage_url(): string {
        return '/wms-enterprise-service/storagesection';
    }


    /**
    * ID MASTER
    */
    get companyMaster_url(): string {
        return '/wms-idmaster-service/companyid';
    }

    get verticalMaster_url(): string {
        return '/wms-idmaster-service/vertical';
    }

    get veticalMaster_url(): string {
        return '/wms-idmaster-service/vetical';
    }

    get currencyMaster_url(): string {
        return '/wms-idmaster-service/currency';
    }

    get countryMaster_url(): string {
        return '/wms-idmaster-service/country';
    }

    get stateMaster_url(): string {
        return '/wms-idmaster-service/state';
    }

    get cityMaster_url(): string {
        return '/wms-idmaster-service/city';
    }

    get itemTypeMaster_url(): string {
        return '/wms-idmaster-service/itemtypeid';
    }

    get itemGroupMaster_url(): string {
        return '/wms-idmaster-service/itemgroupid';
    }

    get plantMaster_url(): string {
        return '/wms-idmaster-service/plantid';
    }

    get warehouseMaster_url(): string {
        return '/wms-idmaster-service/warehouseid';
    }

    get floorMaster_url(): string {
        return '/wms-idmaster-service/floorid';
    }

    get storageSectionMaster_url(): string {
        return '/wms-idmaster-service/storagesectionid';
    }

    get storageClassMaster_url(): string {
        return '/wms-idmaster-service/storageclassid';
    }

    get subItemGroupMaster_url(): string {
        return '/wms-idmaster-service/subitemgroupid';
    }

    get storageTypeMaster_url(): string {
        return '/wms-idmaster-service/stroagetypeid';
    }

    get storageBinTypeMaster_url(): string {
        return '/wms-idmaster-service/storagebintypeid';
    }

    get levelMaster_url(): string {
        return '/wms-idmaster-service/levelid';
    }

    get variantMaster_url(): string {
        return '/wms-idmaster-service/variantid';
    }

    get strategyTypeMaster_url(): string {
        return '/wms-idmaster-service/strategyid';
    }

    /* Product Setup */
    get itemGroup_url(): string {
        return '/wms-enterprise-service/itemgroup'
    }

    get itemType_url(): string {
        return '/wms-enterprise-service/itemtype'
    }

    get batchSerial_url(): string {
        return '/wms-enterprise-service/batchserial'
    }

    get barCode_url(): string {
        return '/wms-enterprise-service/barcode'
    }

    get variant_url(): string {
        return '/wms-enterprise-service/variant'
    }

    /* Storage Setup */
    get storageClass_url(): string {
        return '/wms-enterprise-service/storageclass'
    }

    get storageType_url(): string {
        return '/wms-enterprise-service/storagetype'
    }

    get storageBinType_url(): string {
        return '/wms-enterprise-service/storagebintype'
    }

    get strategy_url(): string {
        return '/wms-enterprise-service/strategies';
    }

    /* Product Master*/
    get basicdata1_url(): string {
        return '/wms-masters-service/imbasicdata1'
    }

    /* Reports*/

    get stockReport_url(): string {
        return '/wms-transaction-service/reports/stockReport'
    }

    get newstockReportAllData_url(): string {
        return '/wms-transaction-service/reports/stockReport-all'
    }

    get inventoryReport_url(): string {
        return '/wms-transaction-service/reports/inventoryReport'
    }

    get stockMovement_url(): string {
        return '/wms-transaction-service/reports/stockMovementReport'
    }

    get newStockMovement_url(): string {
        return '/wms-transaction-service/outboundline/stock-movement-report/findOutboundLine'
    }

    get orderStatus_url(): string {
        return '/wms-transaction-service/reports/orderStatusReport'
    }

    get shipmentDelivery_url(): string {
        return '/wms-transaction-service/reports/shipmentDelivery'
    }

    get itemCode_url(): string {
        return '/wms-masters-service/imbasicdata1'
    }

    get itemCode_like_url(): string {
        return '/wms-masters-service/imbasicdata1/findItemCodeByLike'
    }    
    get storageBin_like_url(): string {
        return '/wms-masters-service/storagebin/findStorageBinByLike'
    }

    /*Prepetual-Count */
    get prepetualRun_url(): string {
        return '/wms-transaction-service/perpetualheader/run'
    }

    get prepetualCountList_url(): string {
        return '/wms-transaction-service/perpetualheader/findPerpetualHeader'
    }

    get prepetualCountSave(): string {
        return '/wms-transaction-service/perpetualheader'
    }


    get prepetualCountSaveHHT(): string {
        return '/wms-transaction-service/perpetualline/assigingHHTUser'
    }

    get prepetualLinePatch(): string {
        return '/wms-transaction-service/perpetualline'
    }



    get prepetualCountConfirm(): string {
        return '/wms-transaction-service/perpetualheader'
    }

    get getStorageBin(): string {
        return '/wms-masters-service/storagebin/findStorageBin'
    }


    //periodic header
    get periodicHeaderRun(): string {
        return '/wms-transaction-service/periodicheader/run'
    }

    get periodicHeaderCreate(): string {
        return '/wms-transaction-service/periodicheader'
    }

    get findPeriodicHeaderList(): string {
        return '/wms-transaction-service/periodicheader/findPeriodicHeader'
    }

    get getAllHHTUserList(): string {
        return '/wms-idmaster-service/hhtuser';
    }

    get periodicCountHHTUserPatch(): string {
        return '/wms-transaction-service/periodicline/assigingHHTUser'
    }

    get periodicHeaderUpdate(): string {
        return '/wms-transaction-service/periodicheader'
    }

    get periodicLineUpdate(): string {
        return '/wms-transaction-service/periodicline'
    }
}
