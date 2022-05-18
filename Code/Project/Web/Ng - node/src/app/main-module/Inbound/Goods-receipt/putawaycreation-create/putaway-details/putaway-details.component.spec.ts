import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PutawayDetailsComponent } from './putaway-details.component';

describe('PutawayDetailsComponent', () => {
  let component: PutawayDetailsComponent;
  let fixture: ComponentFixture<PutawayDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PutawayDetailsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PutawayDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
