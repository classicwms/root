import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PutawayMainComponent } from './putaway-main.component';

describe('PutawayMainComponent', () => {
  let component: PutawayMainComponent;
  let fixture: ComponentFixture<PutawayMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PutawayMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PutawayMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
