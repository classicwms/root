import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ControltypeidComponent } from './controltypeid.component';

describe('ControltypeidComponent', () => {
  let component: ControltypeidComponent;
  let fixture: ComponentFixture<ControltypeidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ControltypeidComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ControltypeidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
