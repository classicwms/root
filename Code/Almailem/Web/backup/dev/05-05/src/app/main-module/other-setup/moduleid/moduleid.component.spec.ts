import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModuleidComponent } from './moduleid.component';

describe('ModuleidComponent', () => {
  let component: ModuleidComponent;
  let fixture: ComponentFixture<ModuleidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModuleidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ModuleidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
