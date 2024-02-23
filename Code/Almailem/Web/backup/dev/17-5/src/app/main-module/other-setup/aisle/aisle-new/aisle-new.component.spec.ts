import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AisleNewComponent } from './aisle-new.component';

describe('AisleNewComponent', () => {
  let component: AisleNewComponent;
  let fixture: ComponentFixture<AisleNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AisleNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AisleNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
