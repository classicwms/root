import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BomNewComponent } from './bom-new.component';

describe('BomNewComponent', () => {
  let component: BomNewComponent;
  let fixture: ComponentFixture<BomNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BomNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BomNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
