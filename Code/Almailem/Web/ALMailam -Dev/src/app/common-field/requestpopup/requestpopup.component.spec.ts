import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestpopupComponent } from './requestpopup.component';

describe('RequestpopupComponent', () => {
  let component: RequestpopupComponent;
  let fixture: ComponentFixture<RequestpopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RequestpopupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestpopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
