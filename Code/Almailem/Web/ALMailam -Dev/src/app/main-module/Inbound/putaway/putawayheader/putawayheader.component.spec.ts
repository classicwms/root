import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PutawayheaderComponent } from './putawayheader.component';

describe('PutawayheaderComponent', () => {
  let component: PutawayheaderComponent;
  let fixture: ComponentFixture<PutawayheaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PutawayheaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PutawayheaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
