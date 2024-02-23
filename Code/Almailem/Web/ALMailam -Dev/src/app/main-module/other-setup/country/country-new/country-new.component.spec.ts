import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CountryNewComponent } from './country-new.component';

describe('CountryNewComponent', () => {
  let component: CountryNewComponent;
  let fixture: ComponentFixture<CountryNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CountryNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CountryNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
