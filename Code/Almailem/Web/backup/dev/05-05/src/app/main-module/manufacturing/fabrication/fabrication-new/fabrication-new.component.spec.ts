import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FabricationNewComponent } from './fabrication-new.component';

describe('FabricationNewComponent', () => {
  let component: FabricationNewComponent;
  let fixture: ComponentFixture<FabricationNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FabricationNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FabricationNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
