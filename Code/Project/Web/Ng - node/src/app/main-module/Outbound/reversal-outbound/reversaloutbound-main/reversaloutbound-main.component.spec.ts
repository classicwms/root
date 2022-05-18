import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReversaloutboundMainComponent } from './reversaloutbound-main.component';

describe('ReversaloutboundMainComponent', () => {
  let component: ReversaloutboundMainComponent;
  let fixture: ComponentFixture<ReversaloutboundMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReversaloutboundMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReversaloutboundMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
