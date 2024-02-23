export interface Warehouse {
    address1?: string;
    address2?: string;
    city?: string;
    companyId?: string;
    contactName?: string;
    country?: string;
    createdBy?: string;
    createdOn?: Date;
    deletionIndicator?: number;
    designation?: string;
    email?: string;
    inboundQACheck?: boolean;
    languageId?: string;
    latitude?: number;
    length?: number;
    longitude?: number;
    modeOfImplementation?: string;
    noAisles?: number;
    outboundQACheck?: boolean;
    phoneNumber?: string;
    plantId?: string;
    state?: string;
    storageMethod?: string;
    totalArea?: number;
    uom?: string;
    updatedBy?: string;
    updatedOn?: Date;
    warehouseId?: string;
    warehouseTypeId?: number;
    width?: number;
    zipCode?: number;
    zone?: string;
}

export interface WarehouseList {
    warehouse?: Warehouse[];
}