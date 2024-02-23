import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Basicdata2NewComponent } from './basicdata2-new.component';

describe('Basicdata2NewComponent', () => {
  let component: Basicdata2NewComponent;
  let fixture: ComponentFixture<Basicdata2NewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ Basicdata2NewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(Basicdata2NewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
