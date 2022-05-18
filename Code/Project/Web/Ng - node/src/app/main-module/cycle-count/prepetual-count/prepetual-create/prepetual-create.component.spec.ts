import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrepetualCreateComponent } from './prepetual-create.component';

describe('PrepetualCreateComponent', () => {
  let component: PrepetualCreateComponent;
  let fixture: ComponentFixture<PrepetualCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PrepetualCreateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PrepetualCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
