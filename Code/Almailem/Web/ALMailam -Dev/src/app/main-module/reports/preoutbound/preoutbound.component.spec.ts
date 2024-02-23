import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreoutboundComponent } from './preoutbound.component';

describe('PreoutboundComponent', () => {
  let component: PreoutboundComponent;
  let fixture: ComponentFixture<PreoutboundComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PreoutboundComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PreoutboundComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
