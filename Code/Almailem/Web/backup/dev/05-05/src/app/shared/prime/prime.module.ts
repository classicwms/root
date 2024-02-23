import { NgModule } from "@angular/core";

import { AccordionModule } from "primeng/accordion";
import { TreeModule } from "primeng/tree";
import { FileUploadModule } from "primeng/fileupload";
import { MessagesModule } from "primeng/messages";
import { MessageModule } from "primeng/message";
import { BlockUIModule } from "primeng/blockui";

import { PanelModule } from "primeng/panel";
import { EditorModule } from "primeng/editor";
import { ScrollPanelModule } from "primeng/scrollpanel";
import { TreeTableModule } from "primeng/treetable";
import { DataViewModule } from "primeng/dataview";
import { MenubarModule } from "primeng/menubar";
import { TableModule } from "primeng/table";
import { CalendarModule } from "primeng/calendar";
import { OrganizationChartModule } from "primeng/organizationchart";
import {AutoCompleteModule} from 'primeng/autocomplete';
import {MultiSelectModule} from 'primeng/multiselect';
import { PaginatorModule } from "primeng/paginator";
import {PasswordModule} from 'primeng/password';
import {ToastModule} from 'primeng/toast';
import {SpeedDialModule} from 'primeng/speeddial';
import {DockModule} from 'primeng/dock';
import {InputTextModule} from 'primeng/inputtext';
import {TabViewModule} from 'primeng/tabview';
import {CheckboxModule} from 'primeng/checkbox';
import {OverlayPanelModule} from 'primeng/overlaypanel';
import {BreadcrumbModule} from 'primeng/breadcrumb';
@NgModule({


	exports: [
		AccordionModule,
		TreeModule,
		FileUploadModule,
		MessagesModule,
		MessageModule,
		SpeedDialModule,
		PaginatorModule,
		BlockUIModule,
		PanelModule,
		EditorModule,
		ScrollPanelModule,
		TreeTableModule,
		DataViewModule,
		MenubarModule,
		TableModule,
		CalendarModule,
		OrganizationChartModule,
		AutoCompleteModule,
		MultiSelectModule,
		PasswordModule,
		ToastModule,
		DockModule,
		InputTextModule,
		TabViewModule,
		CheckboxModule,
		OverlayPanelModule,
		BreadcrumbModule
	]
})
export class PrimeModule { }
