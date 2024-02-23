export interface Storage {
  companyId?: string;
  createdBy?: string;
  createdOn?: Date;
  deletionIndicator?: number;
  floorId?: number;
  itemGroup?: number;
  itemType?: number;
  languageId?: string;
  noAisles?: number;
  noRows?: number;
  noShelves?: number;
  noSpan?: number;
  noStorageBins?: number;
  plantId?: string;
  storageSectionId?: string;
  storageTypeNo?: string;
  subItemGroup?: number;
  updatedBy?: string;
  updatedOn?: Date;
  warehouseId?: string;
  }

  export interface StorageList {
    storage?: Storage[];
}