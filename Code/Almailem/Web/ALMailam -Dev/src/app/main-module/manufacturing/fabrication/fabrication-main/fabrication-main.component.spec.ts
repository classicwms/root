import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FabricationMainComponent } from './fabrication-main.component';

describe('FabricationMainComponent', () => {
  let component: FabricationMainComponent;
  let fixture: ComponentFixture<FabricationMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FabricationMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FabricationMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
