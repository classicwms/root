import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InvoicelinesNewComponent } from './invoicelines-new.component';

describe('InvoicelinesNewComponent', () => {
  let component: InvoicelinesNewComponent;
  let fixture: ComponentFixture<InvoicelinesNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InvoicelinesNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InvoicelinesNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
