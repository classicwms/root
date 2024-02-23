import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreoutboundCreateComponent } from './preoutbound-create.component';

describe('PreoutboundCreateComponent', () => {
  let component: PreoutboundCreateComponent;
  let fixture: ComponentFixture<PreoutboundCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PreoutboundCreateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PreoutboundCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
