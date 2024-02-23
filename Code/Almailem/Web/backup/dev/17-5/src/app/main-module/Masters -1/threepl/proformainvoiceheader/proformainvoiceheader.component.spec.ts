import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProformainvoiceheaderComponent } from './proformainvoiceheader.component';

describe('ProformainvoiceheaderComponent', () => {
  let component: ProformainvoiceheaderComponent;
  let fixture: ComponentFixture<ProformainvoiceheaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProformainvoiceheaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProformainvoiceheaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
