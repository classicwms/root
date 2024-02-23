import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImbatchserialComponent } from './imbatchserial.component';

describe('ImbatchserialComponent', () => {
  let component: ImbatchserialComponent;
  let fixture: ComponentFixture<ImbatchserialComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImbatchserialComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ImbatchserialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
