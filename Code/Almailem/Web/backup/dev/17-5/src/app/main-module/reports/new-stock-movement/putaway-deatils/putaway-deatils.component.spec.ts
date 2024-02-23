import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PutawayDeatilsComponent } from './putaway-deatils.component';

describe('PutawayDeatilsComponent', () => {
  let component: PutawayDeatilsComponent;
  let fixture: ComponentFixture<PutawayDeatilsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PutawayDeatilsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PutawayDeatilsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
