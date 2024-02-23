import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailspopupComponent } from './detailspopup.component';

describe('DetailspopupComponent', () => {
  let component: DetailspopupComponent;
  let fixture: ComponentFixture<DetailspopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetailspopupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailspopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
