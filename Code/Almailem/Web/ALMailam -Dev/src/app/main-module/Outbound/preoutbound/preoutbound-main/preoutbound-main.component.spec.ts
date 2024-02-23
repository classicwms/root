import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreoutboundMainComponent } from './preoutbound-main.component';

describe('PreoutboundMainComponent', () => {
  let component: PreoutboundMainComponent;
  let fixture: ComponentFixture<PreoutboundMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PreoutboundMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PreoutboundMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
