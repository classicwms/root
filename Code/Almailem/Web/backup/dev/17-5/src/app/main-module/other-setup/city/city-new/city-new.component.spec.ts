import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CityNewComponent } from './city-new.component';

describe('CityNewComponent', () => {
  let component: CityNewComponent;
  let fixture: ComponentFixture<CityNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CityNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CityNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
