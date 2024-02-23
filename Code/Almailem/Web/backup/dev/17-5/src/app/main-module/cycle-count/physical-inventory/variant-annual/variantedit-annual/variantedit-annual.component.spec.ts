import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VarianteditAnnualComponent } from './variantedit-annual.component';

describe('VarianteditAnnualComponent', () => {
  let component: VarianteditAnnualComponent;
  let fixture: ComponentFixture<VarianteditAnnualComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VarianteditAnnualComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VarianteditAnnualComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
